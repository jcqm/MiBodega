package com.sise.mibodega.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import org.w3c.dom.Text

class NuevaVenta : AppCompatActivity(), OnItemClickListener {

    private lateinit var dbHelper: DBHelper
    private lateinit var btnListo: Button
    private lateinit var txtTotalNuevaVenta: TextView
    private lateinit var listaProductos: ArrayList<DBHelper.Productos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_venta)

        dbHelper = DBHelper(this, null)
        btnListo = findViewById<Button>(R.id.btnListo)
        txtTotalNuevaVenta = findViewById<TextView>(R.id.txtTotalNuevaVenta)

        ////////////// LISTAR ////////////////////////////////////
        val productos = ArrayList<DBHelper.Productos>()
        productos.addAll(dbHelper.ListarStock())

        val recyclerView: RecyclerView = findViewById(R.id.listaResultado)

        val adapter = ListarNuevaVentaAdapter(productos, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val intent =  this.intent
        if (intent != null){
            val nombreProducto = intent.getStringExtra("txtNombre_NuevaVenta")
            val precio = intent.getFloatExtra("txtNombre_NuevaVenta",0f)
            val cantidadSeleccionada = intent.getIntExtra("txtNumero",0)

            btnListo.setOnClickListener {
                var calculoTotal = 0.0f
                //calcular el precio por la cantidad de todos los items, no solo de uno
                calculoTotal = cantidadSeleccionada * precio
                txtTotalNuevaVenta.setText(calculoTotal.toString())
                //identificar en donde esta cada valor
                //devolver y poder usarlo fuera del adapter para usar en la vista principal
                //


            }
        }



    }

    override fun onButtonClick(position: Int, esIncremento: Boolean) {

    }
}
