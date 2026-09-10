package com.gestionviajes.feature.reports.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.gestionviajes.R
import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.domain.model.TourDetail
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object TicketPdfExporter {

    /**
     * Asientos de un tipo de pasaje dentro de un boleto.
     * Ejemplo: FareType.ROUND_TRIP → [1, 2, 3]
     */
    private data class TicketSeatGroup(
        val fareType: FareType,
        val seats: List<Int>,
    )

    /**
     * Un boleto por responsable. Contiene uno o varios grupos de asientos
     * según los tipos de pasaje presentes en la reserva. Sin importes ni
     * estados económicos: únicamente nombre, grupos de asientos, fecha y
     * hora de salida.
     */
    private data class Ticket(
        val name: String,
        val seatGroups: List<TicketSeatGroup>,
    )

    /**
     * Genera el PDF de boletos del viaje. Cada reserva produce exactamente
     * 1 boleto para su responsable, con los asientos agrupados por tipo de
     * pasaje. No se filtra por tramo activo: el boleto refleja todos los
     * asientos de la reserva. Deriva del mismo estado de dominio que el mapa
     * y las cuentas; no autoriza una fuente de datos independiente.
     */
    suspend fun createTickets(context: Context, detail: TourDetail): File =
        withContext(Dispatchers.IO) {
            val tickets = buildTickets(detail)
            val departure = departureInstant(detail)
            val dateLabel = context.getString(R.string.ticket_departure_date_label)
            val timeLabel = context.getString(R.string.departure_time_label)
            val nameLabel = context.getString(R.string.report_name_label)
            val roundTripLabel = context.getString(R.string.round_trip_fare_label)
            val outboundLabel = context.getString(R.string.outbound_fare_label)
            val returnLabel = context.getString(R.string.return_fare_label)

            fun fareLabel(fareType: FareType): String = when (fareType) {
                FareType.ROUND_TRIP -> roundTripLabel
                FareType.OUTBOUND -> outboundLabel
                FareType.RETURN -> returnLabel
            }

            val document = PdfDocument()
            val writer = TicketPageWriter(document)

            tickets.forEachIndexed { index, ticket ->
                val slot = index % TICKETS_PER_PAGE
                if (slot == 0 && index != 0) {
                    writer.finishPage()
                    writer.startPage()
                }
                writer.drawTicket(
                    ticket = ticket,
                    departure = departure,
                    nameLabel = nameLabel,
                    dateLabel = dateLabel,
                    timeLabel = timeLabel,
                    fareLabel = ::fareLabel,
                    slot = slot,
                )
            }

            val file = File(
                File(context.cacheDir, "reports").apply { mkdirs() },
                "boletos-${detail.trip.id}.pdf",
            )
            writer.saveTo(file)
            file
        }

    /**
     * Construye un [Ticket] por cada booking que tenga al menos un asiento.
     * Los asientos se agrupan por tipo de pasaje; dentro de cada grupo se
     * ordenan de menor a mayor. El orden de los boletos sigue el número de
     * asiento más bajo de cada reserva.
     *
     * No filtra por tramo activo: el boleto muestra la reserva completa del
     * responsable, que puede mezclar Ida y vuelta, Solo ida y Solo venida.
     */
    private fun buildTickets(detail: TourDetail): List<Ticket> {
        return detail.bookings
            .mapNotNull { booking ->
                val groups = booking.seats
                    .groupBy { it.fareType }
                    .entries
                    // Orden canónico: ROUND_TRIP, OUTBOUND, RETURN
                    .sortedBy { (fareType, _) -> fareType.ordinal }
                    .map { (fareType, bookingSeats) ->
                        TicketSeatGroup(
                            fareType = fareType,
                            seats = bookingSeats.map { it.number }.sorted(),
                        )
                    }
                if (groups.isEmpty()) null
                else Ticket(booking.responsibleName, groups)
            }
            .sortedBy { ticket -> ticket.seatGroups.flatMap { it.seats }.minOrNull() ?: Int.MAX_VALUE }
    }

    private fun departureInstant(detail: TourDetail): Instant =
        Instant.ofEpochMilli(detail.trip.horaSalidaMillis ?: detail.trip.dateMillis)

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 24f
    private const val COLUMNS = 2
    private const val ROWS = 3
    private const val TICKETS_PER_PAGE = COLUMNS * ROWS
    private const val COL_GAP = 14f
    private const val ROW_GAP = 14f

    private class TicketPageWriter(private val document: PdfDocument) {

        private val locale = Locale.forLanguageTag("es-ES")
        private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM uuuu", locale)
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", locale)

        private val ticketWidth = (PAGE_WIDTH - MARGIN * 2 - COL_GAP * (COLUMNS - 1)) / COLUMNS
        private val ticketHeight = (PAGE_HEIGHT - MARGIN * 2 - ROW_GAP * (ROWS - 1)) / ROWS

        var pageNumber = 1
            private set

        private var page = document.startPage(pageInfo(pageNumber))
        private var canvas = page.canvas

        fun startPage() {
            pageNumber += 1
            page = document.startPage(pageInfo(pageNumber))
            canvas = page.canvas
        }

        fun finishPage() {
            document.finishPage(page)
        }

        fun drawTicket(
            ticket: Ticket,
            departure: Instant,
            nameLabel: String,
            dateLabel: String,
            timeLabel: String,
            fareLabel: (FareType) -> String,
            slot: Int,
        ) {
            val column = slot % COLUMNS
            val row = slot / COLUMNS
            val left = MARGIN + column * (ticketWidth + COL_GAP)
            val top = MARGIN + row * (ticketHeight + ROW_GAP)

            val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1.2f
                color = Color.BLACK
            }
            canvas.drawRoundRect(left, top, left + ticketWidth, top + ticketHeight, 6f, 6f, border)

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
            }

            // ── Nombre del responsable ──────────────────────────────────────
            var y = top + ticketHeight * 0.18f

            textPaint.textSize = 15f
            textPaint.isFakeBoldText = true
            textPaint.style = Paint.Style.FILL
            drawWrapped(canvas, textPaint, ticket.name, left + PAD, y, ticketWidth - PAD * 2)
            textPaint.isFakeBoldText = false

            // ── Grupos de asientos por tipo ─────────────────────────────────
            // Área disponible para los grupos: desde ~38% hasta ~68% de la altura.
            // Máximo 3 grupos; cada grupo ocupa una franja proporcional.
            val groupAreaTop = top + ticketHeight * 0.38f
            val groupAreaBottom = top + ticketHeight * 0.68f
            val groupCount = ticket.seatGroups.size.coerceAtLeast(1)
            val groupSlotHeight = (groupAreaBottom - groupAreaTop) / groupCount

            ticket.seatGroups.forEachIndexed { groupIndex, group ->
                val groupTop = groupAreaTop + groupIndex * groupSlotHeight
                val seatNumbers = group.seats.joinToString(", ") {
                    String.format(locale, "%02d", it)
                }
                drawSeatGroup(
                    canvas = canvas,
                    paint = textPaint,
                    label = fareLabel(group.fareType),
                    value = seatNumbers,
                    left = left,
                    top = groupTop,
                    width = ticketWidth,
                )
            }

            // ── Fecha y hora de salida ──────────────────────────────────────
            y = top + ticketHeight * 0.75f
            drawLabelValue(
                canvas, textPaint, dateLabel,
                dateFormatter.format(departure.atZone(ZoneId.systemDefault())).uppercase(locale),
                left, y, ticketWidth,
            )
            y = top + ticketHeight * 0.89f
            drawLabelValue(
                canvas, textPaint, timeLabel,
                timeFormatter.format(departure.atZone(ZoneId.systemDefault())),
                left, y, ticketWidth,
            )
        }

        /**
         * Dibuja una línea "Tipo: 01, 02, 03" dentro de un boleto.
         * El tipo se pinta en gris oscuro pequeño y los números en negro más grande.
         */
        private fun drawSeatGroup(
            canvas: android.graphics.Canvas,
            paint: Paint,
            label: String,
            value: String,
            left: Float,
            top: Float,
            width: Float,
        ) {
            paint.style = Paint.Style.FILL
            paint.color = Color.DKGRAY
            paint.textSize = 8f
            paint.isFakeBoldText = true
            canvas.drawText("$label:", left + PAD, top, paint)

            paint.color = Color.BLACK
            paint.isFakeBoldText = false
            paint.textSize = 11f
            drawWrapped(canvas, paint, value, left + PAD, top + 16f, width - PAD * 2)
        }

        private fun drawLabelValue(
            canvas: android.graphics.Canvas,
            paint: Paint,
            label: String,
            value: String,
            left: Float,
            top: Float,
            width: Float,
        ) {
            paint.style = Paint.Style.FILL
            paint.color = Color.DKGRAY
            paint.textSize = 9f
            paint.isFakeBoldText = true
            canvas.drawText(label, left + PAD, top, paint)

            paint.color = Color.BLACK
            paint.isFakeBoldText = false
            paint.textSize = 13f
            drawWrapped(canvas, paint, value, left + PAD, top + 20f, width - PAD * 2)
        }

        private fun drawWrapped(
            canvas: android.graphics.Canvas,
            paint: Paint,
            text: String,
            x: Float,
            y: Float,
            width: Float,
        ) {
            val lines = wrapToWidth(text, paint, width)
            lines.forEachIndexed { index, line ->
                canvas.drawText(line, x, y + index * (paint.textSize + 4f), paint)
            }
        }

        fun saveTo(file: File) {
            try {
                document.finishPage(page)
                FileOutputStream(file).use(document::writeTo)
            } finally {
                document.close()
            }
        }

        private fun pageInfo(number: Int): PdfDocument.PageInfo =
            PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, number).create()

        private fun wrapToWidth(text: String, paint: Paint, width: Float): List<String> {
            if (text.isEmpty() || paint.measureText(text) <= width) return listOf(text)
            val lines = mutableListOf<String>()
            var line = ""
            text.split(' ').forEach { word ->
                val candidate = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(candidate) <= width) {
                    line = candidate
                } else {
                    if (line.isNotEmpty()) lines += line
                    line = word
                }
            }
            if (line.isNotEmpty()) lines += line
            return lines
        }

        private companion object {
            const val PAD = 16f
        }
    }
}