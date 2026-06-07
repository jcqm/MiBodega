package com.sise.mibodega.ui.dashboard_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.Spinner
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import com.sise.mibodega.ui.ListarProductoAdapter
import com.sise.mibodega.ui.Productos_detalles


class Fiado : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var txtCliente: EditText
    private lateinit var txtMonto: EditText
    private lateinit var spEstado: Spinner
    private lateinit var btnRegistrarFiado: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        dbHelper = DBHelper(requireContext(), null)
        val view = inflater.inflate(R.layout.fragment_fiado, container, false)

        btnRegistrarFiado = view.findViewById<Button>(R.id.btnRegistrarNuevoFiado)

        btnRegistrarFiado.setOnClickListener {

            val intent = Intent(
                requireActivity(), com.sise.mibodega.ui.AgregarFiado::class.java
            )

            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return view
    }


}