package com.sise.mibodega.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

class DashboardActivity : AppCompatActivity() {
    private lateinit var tvNombre: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Inicializar los componentes de la ui
        tvNombre = findViewById(R.id.tvNombre)

        val db = DBHelper(this, null)
        tvNombre.text=""

        val cursor = db.mostrarNombre()

        cursor.use {
            if (cursor.moveToFirst()) {

                val nombre = cursor.getString(
                    cursor.getColumnIndexOrThrow(DBHelper.Tabla_Usuario_Nombres)
                )

                tvNombre.text = nombre
            }
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}