package com.sise.mibodega.ui.dashboard_fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import com.sise.mibodega.ui.ReporteVentaAdapter
import com.sise.mibodega.ui.TopProductosAdapter
import com.sise.mibodega.ui.VentasPorFechaAdapter
import java.time.LocalDate

class Reportes : Fragment() {

    private lateinit var dbHelper: DBHelper

    // Cards de resumen
    private lateinit var txtTotalHistorico: TextView
    private lateinit var txtVentasHoy: TextView
    private lateinit var txtTotalFiados: TextView
    private lateinit var txtPersonasDeuda: TextView
    private lateinit var txtProductosBajoStock: TextView

    // Listas
    private lateinit var listaHistorialVentas: ListView
    private lateinit var listaTopProductos: ListView
    private lateinit var listaVentasPorFecha: ListView

    // Textos vacios
    private lateinit var tvSinVentas: TextView
    private lateinit var tvSinTopProductos: TextView
    private lateinit var tvSinVentasFecha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dbHelper = DBHelper(requireContext(), null)
        val view = inflater.inflate(R.layout.fragment_reportes, container, false)

        // Inicializar vistas
        txtTotalHistorico = view.findViewById(R.id.txtTotalHistorico)
        txtVentasHoy = view.findViewById(R.id.txtVentasHoy)
        txtTotalFiados = view.findViewById(R.id.txtTotalFiados)
        txtPersonasDeuda = view.findViewById(R.id.txtPersonasDeuda)
        txtProductosBajoStock = view.findViewById(R.id.txtProductosBajoStock)

        listaHistorialVentas = view.findViewById(R.id.listaHistorialVentas)
        listaTopProductos = view.findViewById(R.id.listaTopProductos)
        listaVentasPorFecha = view.findViewById(R.id.listaVentasPorFecha)

        tvSinVentas = view.findViewById(R.id.tvSinVentas)
        tvSinTopProductos = view.findViewById(R.id.tvSinTopProductos)
        tvSinVentasFecha = view.findViewById(R.id.tvSinVentasFecha)

        cargarResumen()
        cargarHistorialVentas()
        cargarTopProductos()
        cargarVentasPorFecha()

        return view
    }

    // Carga las tarjetas de resumen arriba
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarResumen() {
        val fechaHoy = LocalDate.now().toString()

        // Total historico de ventas
        val c1 = dbHelper.contarVentasTotal()
        if (c1.moveToFirst()) {
            val total = c1.getFloat(0)
            txtTotalHistorico.text = "S/ %.2f".format(total)
        } else {
            txtTotalHistorico.text = "S/ 0.00"
        }
        c1.close()

        // Ventas del dia de hoy
        val c2 = dbHelper.contarVentasPorFecha(fechaHoy)
        if (c2.moveToFirst()) {
            val totalHoy = c2.getFloat(0)
            txtVentasHoy.text = "S/ %.2f".format(totalHoy)
        } else {
            txtVentasHoy.text = "S/ 0.00"
        }
        c2.close()

        // Total fiados pendientes
        val c3 = dbHelper.contarFiado()
        if (c3.moveToFirst()) {
            val totalFiado = c3.getFloat(0)
            txtTotalFiados.text = "S/ %.2f".format(totalFiado)
        } else {
            txtTotalFiados.text = "S/ 0.00"
        }
        c3.close()

        // Personas con deuda
        val c4 = dbHelper.contarPersonasConFiado()
        if (c4.moveToFirst()) {
            val personas = c4.getString(0)
            txtPersonasDeuda.text = "$personas personas"
        } else {
            txtPersonasDeuda.text = "0 personas"
        }
        c4.close()

        // Productos con stock bajo
        val c5 = dbHelper.contarStockBajo()
        if (c5.moveToFirst()) {
            val bajo = c5.getString(0)
            txtProductosBajoStock.text = "$bajo productos"
        } else {
            txtProductosBajoStock.text = "0 productos"
        }
        c5.close()
    }

    // Carga el historial completo de ventas usando ListarVentas() que ya existe en DBHelper
    private fun cargarHistorialVentas() {
        val listaVentas = dbHelper.ListarVentas()

        if (listaVentas.isEmpty()) {
            listaHistorialVentas.visibility = View.GONE
            tvSinVentas.visibility = View.VISIBLE
        } else {
            listaHistorialVentas.visibility = View.VISIBLE
            tvSinVentas.visibility = View.GONE
            val adapter = ReporteVentaAdapter(requireContext(), listaVentas)
            listaHistorialVentas.adapter = adapter
        }
    }

    // Carga los top 5 productos mas vendidos usando la nueva query del DBHelper
    private fun cargarTopProductos() {
        val lista = dbHelper.obtenerProductosMasVendidos()

        if (lista.isEmpty()) {
            listaTopProductos.visibility = View.GONE
            tvSinTopProductos.visibility = View.VISIBLE
        } else {
            listaTopProductos.visibility = View.VISIBLE
            tvSinTopProductos.visibility = View.GONE
            val adapter = TopProductosAdapter(requireContext(), lista)
            listaTopProductos.adapter = adapter
        }
    }

    // Carga las ventas agrupadas por fecha usando la nueva query del DBHelper
    private fun cargarVentasPorFecha() {
        val lista = dbHelper.obtenerVentasAgrupadasPorFecha()

        if (lista.isEmpty()) {
            listaVentasPorFecha.visibility = View.GONE
            tvSinVentasFecha.visibility = View.VISIBLE
        } else {
            listaVentasPorFecha.visibility = View.VISIBLE
            tvSinVentasFecha.visibility = View.GONE
            val adapter = VentasPorFechaAdapter(requireContext(), lista)
            listaVentasPorFecha.adapter = adapter
        }
    }
}