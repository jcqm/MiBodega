package com.sise.mibodega.ui.dashboard_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import com.sise.mibodega.ui.DashboardActivity

class Stock : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var btnAgregarProducto: Button

    lateinit var listaResultado: ListView
    lateinit var lblCantidadItems: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        dbHelper = DBHelper(requireContext(), null)

        val view = inflater.inflate(R.layout.fragment_stock, container, false)

        //Inicializando
        btnAgregarProducto = view.findViewById(R.id.btnAgregarProducto)
        listaResultado = view.findViewById(R.id.listaResultado)
        lblCantidadItems = view.findViewById(R.id.lblitems)


        //Listar stock
        val productos = dbHelper.ListarStock()
        val datos = ArrayList<String>()

        for (p in productos){
            datos.add("${p.nombreProducto} - ${p.CategoriaProducto} - S/. ${p.PrecioProducto}   -   ${p.StockProducto} Unidades")
        }

        val adaptador = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            datos
        )

        listaResultado.adapter = adaptador

        // Mostrar cantidad
        val cursorCantidad = dbHelper.contarStock()

        if (cursorCantidad.moveToFirst()) {
            val cantidad = cursorCantidad.getString(0)
            lblCantidadItems.text = cantidad + " Unidades"
        }







        //para dirigirse a agregar producto
        btnAgregarProducto.setOnClickListener {

            val intent = Intent(
                requireActivity(), com.sise.mibodega.ui.AgregarProducto::class.java
            )

            startActivity(intent)
        }

        return view
    }


}