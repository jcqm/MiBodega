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
import  com.sise.mibodega.data.DBHelper

class Home : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var saludo: TextView

    private lateinit var btnNuevaVenta: Button
    private lateinit var btnAgregarProducto: Button

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

        // obtener datos
        val cursor = dbHelper.mostrarNombre()

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)

            saludo.text = "Buenos dias " + nombre
        }

        cursor.close()


        /// BOTON NUEVA VENTA //

        btnNuevaVenta.setOnClickListener {

            val intent = Intent(
                requireActivity(), com.sise.mibodega.ui.NuevaVenta::class.java
            )

            startActivity(intent)
        }



        return view
    }


}


