package com.sise.mibodega.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import java.time.LocalDate

class Fiado_Detalles : AppCompatActivity() {

    private lateinit var txtEditarClienteFiado: EditText
    private lateinit var txtEditarMontoDeudaFiado: EditText
    private lateinit var spEditarEstadoFiado: Spinner

    private lateinit var FiadoID: EditText

    private lateinit var btnEditar: Button
    private lateinit var btnEliminar: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fiado_detalles)

        txtEditarClienteFiado = findViewById<EditText>(R.id.txtEditarClienteFiado)
        txtEditarMontoDeudaFiado = findViewById<EditText>(R.id.txtEditarMontoDeudaFiado)
        spEditarEstadoFiado = findViewById<Spinner>(R.id.spEditarEstadoFiado)
        btnEditar = findViewById<Button>(R.id.btnGuardarCambiosProducto)
        btnEliminar = findViewById<Button>(R.id.btnEliminar)
        FiadoID = findViewById<EditText>(R.id.FiadoID)

        val db = DBHelper(this, null)


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
        spEditarEstadoFiado.adapter = adapterCategoria

        ////////////////////////////////// TRAER TODA LA INFO ////////////////////////////////////////////////////
        val intent = this.intent
        if (intent != null) {
            val iDFiado = intent.getIntExtra("idFiado", 0)
            val nombreCliente = intent.getStringExtra("NombreCliente")
            val MontoDeuda = intent.getFloatExtra("MontoDeuda", 0f)
            val EstadoFiado = intent.getStringExtra("Estado")
            val FechaFiado = intent.getStringExtra("FechaFiado")

            val posicionEstado = Estado.indexOf(EstadoFiado)

            txtEditarClienteFiado.setText(nombreCliente)
            txtEditarMontoDeudaFiado.setText(MontoDeuda.toString())
            spEditarEstadoFiado.setSelection(posicionEstado)
            FiadoID.setText(iDFiado.toString())

        }

        ////////////////////////////////// Editar ////////////////////////////////////////////////////

        btnEditar.setOnClickListener {


            val idFiadoString = FiadoID.text.toString().trim()
            val inputNombreCliente = txtEditarClienteFiado.text.toString().trim()
            val inputMontoDeudaString = txtEditarMontoDeudaFiado.text.toString().trim()
            val inputEstadoString = spEditarEstadoFiado.selectedItem.toString().trim()


            if (inputNombreCliente.isEmpty() || inputMontoDeudaString.isEmpty() || inputEstadoString.isEmpty()) {
                Toast.makeText(this, "Por favor, complete los campos", Toast.LENGTH_SHORT).show()
            } else {

                val idFiadoInt = idFiadoString.toInt()
                val inputMontoDeuda = inputMontoDeudaString.toFloat()
                val fecha = fechaActual().toString()


                db.editar_fiado(
                    idFiadoInt,
                    inputNombreCliente,
                    inputMontoDeuda,
                    inputEstadoString,
                    fecha


                )
                Toast.makeText(
                    this,
                    "Fiado $inputNombreCliente editado",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
        }

        ////////////////////////////////// Eliminar ////////////////////////////////////////////////
        btnEliminar.setOnClickListener {
            val idFiadoString = FiadoID.text.toString().trim()
            val inputCliente = txtEditarClienteFiado.text.toString().trim()
            val idFiadoInt = idFiadoString.toInt()

            MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar elemento")
                .setMessage("¿Estás seguro de que deseas eliminar este elemento?")
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Eliminar") { dialog, _ ->


                    db.eliminar_fiado(idFiadoInt)

                    Toast.makeText(
                        this,
                        "Producto $inputCliente eliminado",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()

                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                }
                .show()
        }



    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fechaActual(): LocalDate? {
        // sacar la hora actual
        val fecha = LocalDate.now()

        return fecha
    }
}