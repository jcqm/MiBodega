package com.sise.mibodega.ui.dashboard_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.sise.mibodega.R
import com.sise.mibodega.ui.DashboardActivity

class Stock : Fragment() {

    private lateinit var btnAgregarProducto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_stock, container, false)


        btnAgregarProducto = view.findViewById(R.id.btnAgregarProducto)

        btnAgregarProducto.setOnClickListener {

            val intent = Intent(
                requireActivity(),
                com.sise.mibodega.ui.AgregarProducto::class.java
            )

            startActivity(intent)
        }

        return view
    }


}