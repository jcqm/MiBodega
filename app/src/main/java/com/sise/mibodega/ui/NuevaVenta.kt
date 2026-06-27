package com.sise.mibodega.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
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
        btnListo = findViewById(R.id.btnListo)
        txtTotalNuevaVenta = findViewById(R.id.txtTotalNuevaVenta)

        val productos = ArrayList<DBHelper.Productos>()
        productos.addAll(dbHelper.ListarStock())
        val recyclerView: RecyclerView = findViewById(R.id.listaResultado)
        val adapter = ListarNuevaVentaAdapter(productos, this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        listaProductos = productos

        btnListo.setOnClickListener {
            // CORRECCIÓN: Filtramos solo productos con cantidad > 0
            val productosInsertar = listaProductos.filter { it.cantidadSeleccionada > 0 }

            if (productosInsertar.isEmpty()) {
                Toast.makeText(this, "Seleccione al menos un producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inputFecha = fechaActual().toString()
            val inputPrecioTotal = calcularResultado()

            // Insertar la venta en la base de datos
            dbHelper.insertarVentaDetalle(
                inputFecha,
                inputPrecioTotal,
                productosInsertar
            )

            // CORRECCIÓN: Resetear cantidades para evitar doble registro
            // si el usuario vuelve atrás con el botón del sistema
            for (producto in listaProductos) {
                producto.cantidadSeleccionada = 0
            }
            txtTotalNuevaVenta.text = "Total: S./ 0.0"

            Toast.makeText(this, "Venta agregada correctamente", Toast.LENGTH_SHORT).show()

            // CORRECCIÓN: Usar finish() + startActivity con FLAG_ACTIVITY_CLEAR_TOP
            // para limpiar el back stack y evitar que el usuario vuelva atrás
            // y registre la misma venta dos veces
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onButtonClick(position: Int, esIncremento: Boolean) {
        calcularResultado()
    }

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
    fun fechaActual(): LocalDate {
        return LocalDate.now()
    }
}