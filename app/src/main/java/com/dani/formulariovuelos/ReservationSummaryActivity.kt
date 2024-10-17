package com.dani.formulariovuelos

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReservationSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservationsummaryactivity)

        // Obtener referencia al TextView donde se mostrará el resumen
        val tvReservationSummary: TextView = findViewById(R.id.tvReservationSummary)

        // Obtener los datos enviados desde la MainActivity
        val fullName = intent.getStringExtra("FULL_NAME")
        val origin = intent.getStringExtra("ORIGIN")
        val destination = intent.getStringExtra("DESTINATION")
        val departureDate = intent.getStringExtra("DEPARTURE_DATE")
        val returnDate = intent.getStringExtra("RETURN_DATE")
        val flightPrice = intent.getDoubleExtra("FLIGHT_PRICE", 0.0)
        val originalPrice = intent.getDoubleExtra("ORIGINAL_PRICE", 0.0) // Nuevo dato para el precio original
        val departureTime = intent.getStringExtra("DEPARTURE_TIME") // Nuevo dato
        val returnTime = intent.getStringExtra("RETURN_TIME") // Nuevo dato

        // Asignar asientos aleatorios
        val departureSeat = generateSeatNumber()
        val returnSeat = generateSeatNumber()

        // Crear el resumen en formato de texto utilizando getString() y strings.xml
        val summary = """
            ${getString(R.string.summary_name, fullName)}
            ${getString(R.string.summary_origin, origin)}
            ${getString(R.string.summary_destination, destination)}
            ${getString(R.string.summary_departure, departureDate, departureTime)}
            ${getString(R.string.summary_departure_seat, departureSeat)}
            ${getString(R.string.summary_return, returnDate, returnTime)}
            ${getString(R.string.summary_return_seat, returnSeat)}
           ${getString(R.string.summary_original_price, originalPrice)}
            ${getString(R.string.summary_final_price, flightPrice)}
        """.trimIndent()

        // Mostrar el resumen en el TextView
        tvReservationSummary.text = summary
    }

    // Generar un número de asiento aleatorio (ejemplo: 12C, 7A)
    private fun generateSeatNumber(): String {
        val rows = (1..25).random()  // Números de fila entre 1 y 25
        val seatLetter = arrayOf("A", "B", "C", "D").random()  // Letras de asiento
        return "$rows$seatLetter"
    }
}
