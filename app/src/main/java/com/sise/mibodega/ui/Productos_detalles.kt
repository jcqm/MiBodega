package com.sise.mibodega.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

class Productos_detalles : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtCantidad: EditText
    private lateinit var txtPrecio: EditText
    private lateinit var spCategoria: Spinner
    private lateinit var imgImagenDetalleProducto: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_detalles)

        val db = DBHelper(this, null)

        txtNombre = findViewById<EditText>(R.id.txtEditarNombreProducto)
        txtCantidad = findViewById<EditText>(R.id.txtEditarStock)
        txtPrecio = findViewById<EditText>(R.id.txtEditarPrecioProducto)
        spCategoria = findViewById<Spinner>(R.id.spEditarCategoria)
        imgImagenDetalleProducto = findViewById<ImageView>(R.id.imgImagenDetallesProducto)

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




        val intent = this.intent
        if (intent != null) {
            val nombreProducto = intent.getStringExtra("nombreProducto")
            val CategoriaProducto = intent.getStringExtra("CategoriaProducto")
            val precioProducto = intent.getFloatExtra("PrecioProducto", 0f)
            val stockProducto = intent.getIntExtra("StockProducto", 0)
            val FotoProducto = intent.getStringExtra("FotoProducto")

            val posicionCategoria = Categoria.indexOf(CategoriaProducto)

            txtNombre.setText(nombreProducto)
            txtPrecio.setText(precioProducto.toString())
            spCategoria.setSelection(posicionCategoria)
            txtCantidad.setText(stockProducto.toString())
            imgImagenDetalleProducto.setImageURI(FotoProducto?.toUri())

        }






    }
}