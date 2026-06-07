package com.sise.mibodega.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
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
    private lateinit var btnGuardarEditar: Button
    private lateinit var btnEliminar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_detalles)

        val db = DBHelper(this, null)

        txtNombre = findViewById<EditText>(R.id.txtEditarNombreProducto)
        txtCantidad = findViewById<EditText>(R.id.txtEditarStock)
        txtPrecio = findViewById<EditText>(R.id.txtEditarPrecioProducto)
        spCategoria = findViewById<Spinner>(R.id.spEditarCategoria)
        imgImagenDetalleProducto = findViewById<ImageView>(R.id.imgImagenDetallesProducto)
        btnGuardarEditar = findViewById<Button>(R.id.btnGuardarCambiosProducto)
        btnEliminar = findViewById<Button>(R.id.btnEliminar)

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

        btnGuardarEditar.setOnClickListener {
            val inputNombreProducto = txtNombre.text.toString().trim()
            val inputCategoria = spCategoria.selectedItem.toString().trim()
            val inputPrecioVentaString = txtPrecio.text.toString().trim()
            val inputStockinicialString = txtCantidad.text.toString().trim()

            val inputCodigoBarras = "no"
            val inputFoto = "no"


            if (inputNombreProducto.isEmpty() || inputCategoria.isEmpty() || inputPrecioVentaString.isEmpty() || inputStockinicialString.isEmpty()) {
                Toast.makeText(this, "Por favor, complete los campos", Toast.LENGTH_SHORT).show()
            } else {
                val inputPrecioVenta = inputPrecioVentaString.toFloat()
                val inputStockinicial = inputStockinicialString.toInt()


                db.editar_producto(
                    2,
                    inputNombreProducto,
                    inputCategoria,
                    inputCodigoBarras,
                    inputPrecioVenta,
                    inputStockinicial,
                    inputFoto

                )
                Toast.makeText(
                    this,
                    "Producto $inputNombreProducto editado",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)


            }
        }

    }


}
