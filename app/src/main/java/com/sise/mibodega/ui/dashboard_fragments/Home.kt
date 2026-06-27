package com.sise.mibodega.ui.dashboard_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import com.sise.mibodega.ui.AgregarFiado
import com.sise.mibodega.ui.DashboardActivity
import com.sise.mibodega.ui.NuevaVenta

class Home : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var saludo: TextView

    private lateinit var btnNuevaVenta: Button
    private lateinit var txtVentasHome: TextView
    private lateinit var txtMostrarStock: TextView
    private lateinit var txtFiadosPendientes: TextView
    private lateinit var txtStockBajo: TextView

    // NUEVO: Botones de acceso rapido desde el inicio
    private lateinit var btnIrAStock: Button
    private lateinit var btnIrAFiado: Button
    private lateinit var btnIrAReportes: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // inicializar
        saludo = view.findViewById(R.id.tvSaludo)
        dbHelper = DBHelper(requireContext(), null)
        btnNuevaVenta = view.findViewById<Button>(R.id.btnNuevaVenta)
        txtVentasHome = view.findViewById<TextView>(R.id.txtVentasHoy)
        txtMostrarStock = view.findViewById<TextView>(R.id.txtMostrarProductos)
        txtFiadosPendientes = view.findViewById<TextView>(R.id.txtFiadosPendientes)
        txtStockBajo = view.findViewById<TextView>(R.id.txtStockBajo)

        // NUEVO: Inicializar los botones de acceso rapido
        // Los IDs vienen del fragment_home.xml
        btnIrAStock = view.findViewById(R.id.btnNuevoProducto)
        btnIrAFiado = view.findViewById(R.id.btnFiados)
        btnIrAReportes = view.findViewById(R.id.btnReporte)

        // obtener datos

        // mostrar stock
        // CORRECCIÓN: Antes los cursores no se cerraban, eso puede causar que la app
        // use mas memoria de la necesaria (memory leak). Ahora se cierran con .close()
        val cursorCantidadStock = dbHelper.contarStock()
        if (cursorCantidadStock.moveToFirst()) {
            val cantidad = cursorCantidadStock.getString(0) ?: "0"
            txtMostrarStock.text = cantidad
        }
        cursorCantidadStock.close()

        // mostrar fiados pendiente
        val cursorCantidadFiadoPendiente = dbHelper.contarPersonasConFiado()
        if (cursorCantidadFiadoPendiente.moveToFirst()) {
            val cantidad = cursorCantidadFiadoPendiente.getString(0) ?: "0"
            txtFiadosPendientes.text = cantidad
        }
        cursorCantidadFiadoPendiente.close()

        // Stock bajo
        val cursorStockBajo = dbHelper.contarStockBajo()
        if (cursorStockBajo.moveToFirst()) {
            val cantidad = cursorStockBajo.getString(0) ?: "0"
            txtStockBajo.text = cantidad
        }
        cursorStockBajo.close()

        // mostrar nombre del usuario en el saludo
        val cursor = dbHelper.mostrarNombre()
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            saludo.text = "Buenos dias $nombre"
        }
        cursor.close()


        /// BOTON NUEVA VENTA //
        btnNuevaVenta.setOnClickListener {
            val intent = Intent(requireActivity(), NuevaVenta::class.java)
            startActivity(intent)
        }

        // NUEVO: Boton "+ Nuevo producto" te lleva directo al tab de Stock en el Dashboard
        btnIrAStock.setOnClickListener {
            val dashboard = requireActivity() as DashboardActivity
            dashboard.navegarA(R.id.stock)
        }

        // NUEVO: Boton "+ Nuevo fiado" te lleva directo al tab de Fiados en el Dashboard
        btnIrAFiado.setOnClickListener {
            val dashboard = requireActivity() as DashboardActivity
            dashboard.navegarA(R.id.fiados)
        }

        // NUEVO: Boton "+ Reportes" te lleva directo al tab de Reportes en el Dashboard
        btnIrAReportes.setOnClickListener {
            val dashboard = requireActivity() as DashboardActivity
            dashboard.navegarA(R.id.reportes)
        }

        return view
    }


}