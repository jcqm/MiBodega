package com.sise.mibodega.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import java.time.LocalDate

class NuevaVenta : AppCompatActivity(), OnItemClickListener {

    private lateinit var dbHelper: DBHelper
    private lateinit var btnListo: Button
    private lateinit var txtTotalNuevaVenta: TextView
    private lateinit var listaProductos: ArrayList<DBHelper.Productos>

    @RequiresApi(Build.VERSION_CODES.O)
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

        listaProductos = productos

        var inputProductoID = 0
        var inputPrecioUnitario = 0.0f
        var inputPrecioTotal = 0.0f

        btnListo.setOnClickListener {
            for (producto in listaProductos) {
                inputProductoID = producto.IdProducto
                inputPrecioUnitario = producto.cantidadSeleccionada * producto.PrecioProducto
                inputPrecioTotal = calcularResultado()
            }

            val inputFecha = fechaActual().toString()

            dbHelper.insertarVenta(
                inputProductoID,
                inputFecha,
                inputPrecioUnitario,
                inputPrecioTotal
            )
            Toast.makeText(
                this, "Venta agregada correctamente", Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)

        }

    }

    override fun onButtonClick(position: Int, esIncremento: Boolean) {
        calcularResultado()
    }

    //Lo use para calcular el resultado de cantidad por precio, luego se lo pase al onButtonClick
    fun calcularResultado(): Float {
        var total = 0.0f

        for (totalProducto in listaProductos) {
            val totalIndividual = totalProducto.cantidadSeleccionada * totalProducto.PrecioProducto
            total += totalIndividual
        }


        txtTotalNuevaVenta.text = "Total: S./ " + total.toString()
        return total
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fechaActual(): LocalDate? {
        // Sacar fecha actual
        val fecha = LocalDate.now()

        return fecha
    }


}
