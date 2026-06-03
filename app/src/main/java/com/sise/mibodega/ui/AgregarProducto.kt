package com.sise.mibodega.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

class AgregarProducto : AppCompatActivity() {

    private lateinit var btnGuardarProducto: Button
    private lateinit var txtNombreProducto: EditText
    private lateinit var spCategoria: Spinner
    private lateinit var txtPrecioVenta: EditText
    private lateinit var txtStockInicial: EditText
    private lateinit var dbHelper: DBHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

//        dbHelper = DBHelper(this, null)
        val db = DBHelper(this, null)

        btnGuardarProducto = findViewById(R.id.GuardarProducto)
        txtNombreProducto = findViewById(R.id.txtNombreProducto)
        spCategoria = findViewById(R.id.spCategoria)
        txtPrecioVenta = findViewById(R.id.txtPrecio)
        txtStockInicial = findViewById(R.id.txtStock)

        ///////////////////////////Spinner de por mientras//////////////////////////////////////
        val Categoria = arrayOf(
            "Bebidas",
            "Abarrotes",
            "Básicos",
            "Lácteos",
            "Limpieza del Hogar",
            "Higiene y Cuidado Personal"
        )
        val adapterCategoria = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Categoria
        )
        adapterCategoria.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spCategoria.adapter = adapterCategoria
        /////////////////////////////////////////////////////////////////

        btnGuardarProducto.setOnClickListener {
            val inputNombreProducto = txtNombreProducto.text.toString().trim()
            val inputCategoria = spCategoria.selectedItem.toString().trim()
            val inputPrecioVentaString = txtPrecioVenta.text.toString().trim()
            val inputStockinicialString = txtStockInicial.text.toString().trim()

            val inputCodigoBarras = "no"




            if (inputNombreProducto.isEmpty() || inputCategoria.isEmpty() || inputPrecioVentaString.isEmpty() || inputStockinicialString.isEmpty()) {
                Toast.makeText(this, "Por favor, complete los campos", Toast.LENGTH_SHORT).show()
            } else {
                val inputPrecioVenta = inputPrecioVentaString.toFloat()
                val inputStockinicial = inputStockinicialString.toInt()

                db.insertar_producto(
                    inputNombreProducto,
                    inputCategoria,
                    inputCodigoBarras,
                    inputPrecioVenta,
                    inputStockinicial

                )
                Toast.makeText(
                    this,
                    "Producto $inputNombreProducto correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }
}