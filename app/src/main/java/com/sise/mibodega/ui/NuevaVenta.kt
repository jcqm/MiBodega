package com.sise.mibodega.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps3d.model.popoverStyle
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import org.w3c.dom.Text
import com.sise.mibodega.ui.ListarNuevaVentaAdapter

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
        listaProductos = productos
        calcularResultado()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


    }

    override fun onButtonClick(position: Int, esIncremento: Boolean) {
        calcularResultado()
    }


    fun calcularResultado(): Float {
        var total = 0.0f

        for (totalProducto in listaProductos) {
            val totalIndividual = totalProducto.totalVendido
            total += totalIndividual
        }

        txtTotalNuevaVenta.text = "Total: S./ " + total.toString()
        return total
    }



}
