package com.gestionviajes.feature.reports.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.gestionviajes.R
import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.domain.model.TourDetail
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object ReportPdfExporter {

    /**
     * Informe limpio de viaje: listado de todos los tours registrados con
     * asiento, tipo de pasaje, nombre del responsable y abono total recibido.
     * Sin historial de pagos. Se genera desde el mismo estado de dominio que
     * el mapa y las cuentas; no autoriza una fuente de datos independiente.
     */
    suspend fun createTripManifest(context: Context, detail: TourDetail): File =
        createTripManifest(context, listOf(detail), customFileName = "informe-viaje-${detail.trip.id}.pdf")

    suspend fun createTripManifest(
        context: Context,
        details: List<TourDetail>,
        customFileName: String = "informe-viaje.pdf",
    ): File = withContext(Dispatchers.IO) {
        val writer = PdfWriter(PdfDocument(), PAGE_WIDTH, PAGE_HEIGHT, MARGIN)
        val currency = context.getString(R.string.currency_symbol)
        val generatedFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm", locale)

        writer.drawTitle(context.getString(R.string.trip_report_title))
        writer.drawInfoLine(
            context.getString(R.string.report_generated_label),
            generatedFormatter.format(LocalDateTime.now()),
        )

        if (details.isEmpty()) {
            writer.drawInfoLine(context.getString(R.string.reports_no_tours_message), "")
        } else {
            details
                .sortedBy { detail -> detail.trip.dateMillis }
                .forEach { detail ->
                    writer.spacer(10f)
                    writer.drawSectionTitle(manifestSectionTitle(context, detail.trip))
                    drawTourInfo(writer, context, detail.trip)
                    writer.spacer(4f)
                    val headers = listOf(
                        context.getString(R.string.report_seat_number_label),
                        context.getString(R.string.report_fare_type_label),
                        context.getString(R.string.report_name_label),
                        context.getString(R.string.report_abono_label),
                    )
                    val widths = listOf(60f, 95f, 220f, 156f)
                    val rows = buildTripManifestRows(detail).map { row ->
                        listOf(
                            row.seatNumber.formatSeatNumber(),
                            row.fareType?.let { fareLabel(context, it) } ?: "-",
                            row.name,
                            row.abonoCents?.let { formatMoney(it, currency) } ?: "-",
                        )
                    }
                    writer.drawTable(headers, widths, rows)
                }
        }

        val file = File(File(context.cacheDir, "reports").apply { mkdirs() }, customFileName)
        writer.saveTo(file)
        file
    }

    /**
     * Informe completo de un solo tour: listado de pasajeros con abonos y
     * estado, seguido del historial de pagos registrados.
     */
    suspend fun create(context: Context, detail: TourDetail): File = withContext(Dispatchers.IO) {
        val writer = PdfWriter(PdfDocument(), PAGE_WIDTH, PAGE_HEIGHT, MARGIN)
        val trip = detail.trip
        val departure = Instant.ofEpochMilli(trip.dateMillis).atZone(ZoneId.systemDefault())
        val currency = context.getString(R.string.currency_symbol)
        val bookingsById = detail.bookings.associateBy { booking -> booking.id }

        writer.drawTitle(context.getString(R.string.passenger_report_title))
        drawTourInfo(writer, context, trip, departure)
        writer.spacer(8f)

        writer.drawSectionTitle(context.getString(R.string.passenger_report_list_title))
        val passengerHeaders = listOf(
            context.getString(R.string.report_seat_number_label),
            context.getString(R.string.report_fare_type_label),
            context.getString(R.string.report_name_label),
            context.getString(R.string.report_status_label),
            context.getString(R.string.report_total_label),
            context.getString(R.string.report_paid_label),
            context.getString(R.string.report_balance_label),
        )
        val passengerWidths = listOf(50f, 80f, 135f, 60f, 65f, 70f, 71f)
        val passengerRows = detail.seats.map { tourSeat ->
            val booking = tourSeat.bookingId?.let(bookingsById::get)
            val bookingSeat = booking?.seats?.firstOrNull { seat -> seat.number == tourSeat.number }
            listOf(
                tourSeat.number.formatSeatNumber(),
                bookingSeat?.let { seat -> fareLabel(context, seat.fareType) } ?: "-",
                booking?.responsibleName ?: "-",
                statusLabel(context, tourSeat.status),
                booking?.let { formatMoney(it.totalCents, currency) } ?: "-",
                booking?.let { formatMoney(it.receivedCents, currency) } ?: "-",
                booking?.let { formatMoney(it.pendingCents, currency) } ?: "-",
            )
        }
        writer.drawTable(passengerHeaders, passengerWidths, passengerRows)

        writer.spacer(12f)
        writer.drawSectionTitle(context.getString(R.string.payment_history_title))
        val paymentHeaders = listOf(
            context.getString(R.string.report_name_label),
            context.getString(R.string.booking_seats_label),
            context.getString(R.string.payment_label),
            context.getString(R.string.payment_amount_column_label),
            context.getString(R.string.report_date_label),
        )
        val paymentWidths = listOf(150f, 110f, 75f, 85f, 111f)
        val paymentRows = detail.bookings
            .sortedBy { booking -> booking.seats.minOfOrNull { seat -> seat.number } }
            .flatMap { booking ->
                val seats = booking.seats.sortedBy { seat -> seat.number }
                    .joinToString(",") { seat -> seat.number.formatSeatNumber() }
                booking.payments.mapIndexed { index, payment ->
                    listOf(
                        booking.responsibleName,
                        seats,
                        context.getString(R.string.payment_history_item, index + 1),
                        formatMoney(payment.amountCents, currency),
                        paymentDateFormatter.format(
                            Instant.ofEpochMilli(payment.dateMillis).atZone(ZoneId.systemDefault()),
                        ),
                    )
                }
            }
        writer.drawTable(paymentHeaders, paymentWidths, paymentRows)

        val file = File(File(context.cacheDir, "reports").apply { mkdirs() }, "informe-tour-${trip.id}.pdf")
        writer.saveTo(file)
        file
    }

    private fun drawTourInfo(
        writer: PdfWriter,
        context: Context,
        trip: com.gestionviajes.feature.trips.domain.model.Trip,
        departure: java.time.ZonedDateTime = Instant.ofEpochMilli(trip.dateMillis)
            .atZone(ZoneId.systemDefault()),
    ) {
        writer.drawInfoLine(context.getString(R.string.report_destination_label), trip.destination.orEmpty())
        writer.drawInfoLine(context.getString(R.string.report_date_label), dateFormatter.format(departure))
        writer.drawInfoLine(context.getString(R.string.report_time_label), timeFormatter.format(departure))
        writer.drawInfoLine(context.getString(R.string.report_supervisor_label), trip.supervisor.orEmpty())
    }

    private fun manifestSectionTitle(
        context: Context,
        trip: com.gestionviajes.feature.trips.domain.model.Trip,
    ): String {
        val departure = Instant.ofEpochMilli(trip.dateMillis).atZone(ZoneId.systemDefault())
        return "${trip.destination.orEmpty()} · ${dateFormatter.format(departure)} · " +
            timeFormatter.format(departure)
    }

    private fun fareLabel(context: Context, fareType: FareType): String = when (fareType) {
        FareType.ROUND_TRIP -> context.getString(R.string.round_trip_fare_label)
        FareType.OUTBOUND -> context.getString(R.string.outbound_fare_label)
        FareType.RETURN -> context.getString(R.string.return_fare_label)
    }

    private fun statusLabel(context: Context, status: TourSeatStatus): String = when (status) {
        TourSeatStatus.EMPTY -> context.getString(R.string.seat_status_empty)
        TourSeatStatus.RESERVED -> context.getString(R.string.seat_status_reserved)
        TourSeatStatus.PARTIAL -> context.getString(R.string.seat_status_partial)
        TourSeatStatus.PAID -> context.getString(R.string.seat_status_paid)
    }

    private fun Int.formatSeatNumber(): String = String.format(locale, "%02d", this)

    private fun formatMoney(cents: Long, currency: String): String =
        "$currency${String.format(locale, "%d.%02d", cents / 100, cents % 100)}"

    private val locale = Locale.forLanguageTag("es-ES")
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM uuuu", locale)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", locale)
    private val paymentDateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu", locale)

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 32f

    /** Renderizador de páginas A4 que se reparte el dibujo entre informes. */
    private class PdfWriter(
        private val document: PdfDocument,
        private val pageWidth: Int,
        private val pageHeight: Int,
        private val margin: Float,
    ) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val contentWidth = pageWidth - margin * 2
        private val bottomLimit = pageHeight - margin
        private var pageNumber = 1
        private var page = document.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create(),
        )
        private var canvas = page.canvas
        private var y = margin

        fun drawTitle(text: String) {
            if (y + 24f > bottomLimit) newPage()
            paint.style = Paint.Style.FILL
            paint.color = Color.BLACK
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText(text, margin, y + 14f, paint)
            paint.isFakeBoldText = false
            y += 24f
        }

        fun drawInfoLine(label: String, value: String) {
            val lines = textLines("$label: $value", contentWidth, 9f)
            if (y + lines.size * 13f > bottomLimit) newPage()
            paint.style = Paint.Style.FILL
            paint.color = Color.DKGRAY
            paint.textSize = 9f
            lines.forEach { line ->
                canvas.drawText(line, margin, y + 10f, paint)
                y += 13f
            }
        }

        fun drawSectionTitle(title: String) {
            if (y + 24f > bottomLimit) newPage()
            paint.style = Paint.Style.FILL
            paint.color = Color.BLACK
            paint.textSize = 11f
            paint.isFakeBoldText = true
            canvas.drawText(title, margin, y + 12f, paint)
            paint.isFakeBoldText = false
            y += 20f
        }

        fun drawTable(headers: List<String>, widths: List<Float>, rows: List<List<String>>) {
            fun drawHeader() = drawRow(headers, widths, header = true)

            if (y + rowHeight(headers, widths, 8.5f) > bottomLimit) newPage()
            drawHeader()
            rows.forEach { cells ->
                if (y + rowHeight(cells, widths, 8f) > bottomLimit) {
                    newPage()
                    drawHeader()
                }
                drawRow(cells, widths)
            }
        }

        fun spacer(pixels: Float) {
            y += pixels
        }

        fun saveTo(file: File) {
            try {
                document.finishPage(page)
                FileOutputStream(file).use(document::writeTo)
            } finally {
                document.close()
            }
        }

        private fun newPage() {
            document.finishPage(page)
            pageNumber += 1
            page = document.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create(),
            )
            canvas = page.canvas
            y = MARGIN
        }

        private fun textLines(text: String, width: Float, textSize: Float): List<String> {
            paint.textSize = textSize
            return text.wrapToWidth(paint, width)
        }

        private fun rowHeight(cells: List<String>, widths: List<Float>, textSize: Float): Float {
            val lineHeight = textSize + 3f
            val lineCount = cells.indices.maxOf { index ->
                textLines(cells[index], widths[index] - 8f, textSize).size
            }
            return maxOf(24f, lineCount * lineHeight + 8f)
        }

        private fun drawRow(cells: List<String>, widths: List<Float>, header: Boolean = false) {
            val textSize = if (header) 8.5f else 8f
            val height = rowHeight(cells, widths, textSize)
            val lineHeight = textSize + 3f
            var x = MARGIN
            paint.style = Paint.Style.FILL
            paint.color = if (header) Color.rgb(228, 232, 238) else Color.WHITE
            canvas.drawRect(MARGIN, y, MARGIN + contentWidth, y + height, paint)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 0.6f
            paint.color = Color.rgb(105, 112, 122)
            widths.forEach { width ->
                canvas.drawRect(x, y, x + width, y + height, paint)
                x += width
            }

            paint.style = Paint.Style.FILL
            paint.color = Color.BLACK
            paint.textSize = textSize
            paint.isFakeBoldText = header
            x = MARGIN
            cells.indices.forEach { index ->
                textLines(cells[index], widths[index] - 8f, textSize).forEachIndexed { lineIndex, line ->
                    canvas.drawText(line, x + 4f, y + 13f + lineIndex * lineHeight, paint)
                }
                x += widths[index]
            }
            paint.isFakeBoldText = false
            y += height
        }
    }
}

private fun String.wrapToWidth(paint: Paint, width: Float): List<String> {
    if (isEmpty() || paint.measureText(this) <= width) return listOf(this)
    val lines = mutableListOf<String>()
    var line = ""
    split(' ').forEach { word ->
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