package com.sise.mibodega.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

class NuevaVenta : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var btnListo: Button
    private lateinit var listaResultado: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_venta)
        dbHelper = DBHelper(this, null)
        listaResultado = findViewById<ListView>(R.id.listaResultado)

        btnListo = findViewById<Button>(R.id.btnListo)

        ////////////// LISTAR ////////////////////////////////////

        val productos = ArrayList<DBHelper.Productos>()
        productos.addAll(dbHelper.ListarStock())

        val adapter = ListarNuevaVentaAdapter(this, productos)
        listaResultado.adapter = adapter

        /////// contador ////









    }



}