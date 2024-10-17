package com.dani.formulariovuelos

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var spnOrigin: Spinner
    private lateinit var spnDestination: Spinner
    private lateinit var btnDepartureDate: Button
    private lateinit var btnReturnDate: Button
    private lateinit var spnDepartureTimes: Spinner
    private lateinit var spnReturnTimes: Spinner
    private lateinit var etEmail: EditText
    private lateinit var etFrequentFlyerNumber: EditText
    private lateinit var btnReserve: Button

    private var departureDate: String = ""
    private var returnDate: String = ""
    private var flightPrice: Double = 0.0
    private var basePrice: Double = 0.0
    private val random = Random()

    private val destinations: Array<String> by lazy {
        resources.getStringArray(R.array.destinatinos)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        spnOrigin = findViewById(R.id.spnOrigin)
        spnDestination = findViewById(R.id.spnDestination)
        btnDepartureDate = findViewById(R.id.btnDepartureDate)
        btnReturnDate = findViewById(R.id.btnReturnDate)
        spnDepartureTimes = findViewById(R.id.spnDepartureTimes)
        spnReturnTimes = findViewById(R.id.spnReturnTimes)
        etEmail = findViewById(R.id.etEmail)
        etFrequentFlyerNumber = findViewById(R.id.etFrequentFlyerNumber)
        btnReserve = findViewById(R.id.btnReserve)

        // Configurar Spinners de origen y destino
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, destinations)
        spnOrigin.adapter = adapter
        spnDestination.adapter = adapter

        // Escuchadores para evitar que origen y destino sean iguales
        spnOrigin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedOrigin = spnOrigin.selectedItem.toString()
                updateDestinationSpinner(selectedOrigin)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spnDestination.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedDestination = spnDestination.selectedItem.toString()
                val originSelected = spnOrigin.selectedItem.toString()

                // Volver a permitir la selección del destino si el usuario cambia el origen
                if (selectedDestination == originSelected) {
                    spnOrigin.setSelection(0) // Reiniciar origen si se selecciona el mismo
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Configurar Spinners de horarios disponibles
        val availableTimes = arrayOf("06:00 AM", "09:00 AM", "12:00 PM", "03:00 PM", "06:00 PM")
        val timesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, availableTimes)
        spnDepartureTimes.adapter = timesAdapter
        spnReturnTimes.adapter = timesAdapter // Usar el mismo adapter para horarios de regreso

        // Seleccionar fecha de salida
        btnDepartureDate.setOnClickListener {
            showDatePicker { date ->
                departureDate = date
                btnDepartureDate.text = date
                // Establecer la fecha mínima de regreso basada en la fecha de salida
                setMinReturnDate(date)
            }
        }

        // Seleccionar fecha de regreso
        btnReturnDate.setOnClickListener {
            showDatePicker { date ->
                returnDate = date
                btnReturnDate.text = date
            }
        }

        // Botón Reservar
        btnReserve.setOnClickListener {
            if (validateInput()) {
                calculateFlightPrice()
                goToReservationSummary()
            }
        }
    }

    private fun updateDestinationSpinner(selectedOrigin: String) {
        val filteredDestinations = destinations.filter { it != selectedOrigin }.toTypedArray()
        val destinationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filteredDestinations)
        spnDestination.adapter = destinationAdapter
        // Seleccionar el primer destino si el destino actual es igual al origen
        if (spnDestination.selectedItem == selectedOrigin) {
            spnDestination.setSelection(0)
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(date)
        }, year, month, day)

        // Restringir para que no se puedan seleccionar fechas pasadas
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun setMinReturnDate(departureDate: String) {
        // Convertir la fecha de salida a Calendar
        val parts = departureDate.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt() - 1 // Mes en Calendar es 0-indexado
        val year = parts[2].toInt()

        val departureCalendar = Calendar.getInstance()
        departureCalendar.set(year, month, day)

        // Establecer la fecha mínima para la fecha de regreso
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedReturnDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"

            // Comparar fechas
            if (selectedReturnDate == departureDate) {
                Toast.makeText(this, getString(R.string.Fecha_regreso_diferente_salida), Toast.LENGTH_SHORT).show()
                setMinReturnDate(departureDate) // Volver a mostrar el selector de fechas
            } else {
                returnDate = selectedReturnDate
                btnReturnDate.text = returnDate
            }
        }, year, month, day)

        // La fecha mínima de regreso es la misma que la fecha de salida
        datePickerDialog.datePicker.minDate = departureCalendar.timeInMillis + 24 * 60 * 60 * 1000 // +1 día
        datePickerDialog.show()
    }


    private fun validateInput(): Boolean {
        if (TextUtils.isEmpty(etFirstName.text)) {
            etFirstName.error = getString(R.string.no_ha_ingrasdo_nombre)
            return false
        }
        if (TextUtils.isEmpty(etLastName.text)) {
            etLastName.error = getString(R.string.no_ha_ingresado_apellido)
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            etEmail.error = getString(R.string.correo_invalido)
            return false
        }
        if (spnOrigin.selectedItem == spnDestination.selectedItem) {
            Toast.makeText(this, getString(R.string.destino_diferente), Toast.LENGTH_SHORT).show()
            return false
        }
        // Validar que se haya seleccionado una fecha de salida
        if (departureDate.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_se_ha_seleccionado_fecha_salida), Toast.LENGTH_SHORT).show()
            return false
        }
        // Validar que se haya seleccionado una fecha de regreso
        if (returnDate.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_se_ha_seleccionado_fecha_regreso), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun calculateFlightPrice() {
        basePrice = (3000..10000).random().toDouble()  // Generar precio aleatorio
        flightPrice = basePrice
        val frequentFlyerNumber = etFrequentFlyerNumber.text.toString()

        // Si hay número de cliente frecuente válido, aplicar descuento
        if (frequentFlyerNumber.isNotEmpty() && frequentFlyerNumber.length == 8) {
            flightPrice *= 0.85  // Descuento del 15%
        }
    }

    private fun goToReservationSummary() {
        val intent = Intent(this, ReservationSummaryActivity::class.java)
        intent.putExtra("FULL_NAME", "${etFirstName.text} ${etLastName.text}")
        intent.putExtra("ORIGIN", spnOrigin.selectedItem.toString())
        intent.putExtra("DESTINATION", spnDestination.selectedItem.toString())
        intent.putExtra("DEPARTURE_DATE", departureDate)
        intent.putExtra("RETURN_DATE", returnDate)
        intent.putExtra("ORIGINAL_PRICE", basePrice)
        intent.putExtra("FLIGHT_PRICE", flightPrice)
        intent.putExtra("DEPARTURE_TIME", spnDepartureTimes.selectedItem.toString())
        intent.putExtra("RETURN_TIME", spnReturnTimes.selectedItem.toString())
        startActivity(intent)
    }
}
