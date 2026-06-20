package com.sise.mibodega.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AgregarFiado : AppCompatActivity() {

    private lateinit var txtCliente: EditText
    private lateinit var txtMontoDeuda: EditText
    private lateinit var spEstado: Spinner
    private lateinit var btnRegistrarFiado: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_fiado)

        val db = DBHelper(this, null)

        txtCliente = findViewById<EditText>(R.id.txtClienteFiado)
        txtMontoDeuda = findViewById<EditText>(R.id.txtMontoDeuda)
        spEstado = findViewById<Spinner>(R.id.spEstado)
        btnRegistrarFiado = findViewById<Button>(R.id.btnRegistrarFiado)

        ///////////////////////////Spinner de por mientras//////////////////////////////////////
        val Estado = arrayOf(
            "Cancelado",
            "Pendiente",
        )
        val adapterCategoria = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, Estado
        )
        adapterCategoria.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spEstado.adapter = adapterCategoria

        //////////////////////////////////////////////////////////////////////////////////////
        btnRegistrarFiado.setOnClickListener {
            val inputNombreCliente = txtCliente.text.toString().trim()
            val inputMontoDeudaString = txtMontoDeuda.text.toString().trim()
            val inputEstadoString = spEstado.selectedItem.toString().trim()

            if (inputNombreCliente.isEmpty() || inputMontoDeudaString.isEmpty() || inputEstadoString.isEmpty()) {
                Toast.makeText(this, "Por favor, complete los campos", Toast.LENGTH_SHORT).show()
            } else {
                val inputMontoDeuda = inputMontoDeudaString.toFloat()
                val fecha = fechaActual().toString()

                db.insertar_fiado(
                    inputNombreCliente,
                    inputMontoDeuda,
                    inputEstadoString,
                    fecha,

                    )
                Toast.makeText(
                    this, "Fiado agregado correctamente", Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)

            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fechaActual(): LocalDate? {
        // Sacar fecha actual
        val fecha = LocalDate.now()

        return fecha
    }


}