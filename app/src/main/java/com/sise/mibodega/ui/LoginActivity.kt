package com.sise.mibodega.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import android.content.Intent
class LoginActivity : AppCompatActivity() {

    private lateinit var btnAceptar: Button
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etNombreTienda: EditText
    private lateinit var etDireccionTienda: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inicializar los componentes de la ui

        btnAceptar = findViewById(R.id.btnAceptar)
        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        etNombreTienda = findViewById(R.id.etNombreTienda)
        etDireccionTienda = findViewById(R.id.etDireccionTienda)

        // creo la instancia del database helper
        val db = DBHelper(this, null)

        // agrego la informacion a la base de datos con el boton
        btnAceptar.setOnClickListener {
            val inputNombre = etNombre.text.toString().trim()
            val inputApellidos = etApellidos.text.toString().trim()
            val inputNombreTienda = etNombreTienda.text.toString().trim()
            val inputDireccionTienda = etDireccionTienda.text.toString().trim()



            //Validacion
            if (inputDireccionTienda.isEmpty() || inputNombreTienda.isEmpty() || inputApellidos.isEmpty() || inputNombre.isEmpty()) {
                //mostramos mensaje si esta vacio
                Toast.makeText(this, "Por favor, complete los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Si funciona
                db.insertar_usuario(
                    inputNombre,
                    inputApellidos,
                    inputNombreTienda,
                    inputDireccionTienda
                )
                Toast.makeText(this, "Bienvenido, $inputNombre", Toast.LENGTH_SHORT).show()

                //aca que lo jale a otra vista
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)

            }


        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}