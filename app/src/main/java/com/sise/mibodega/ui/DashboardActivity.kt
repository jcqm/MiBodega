package com.sise.mibodega.ui

import com.sise.mibodega.data.DBHelper
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sise.mibodega.R
import com.sise.mibodega.databinding.ActivityDashboardBinding
import com.sise.mibodega.ui.dashboard_fragments.Fiado
import com.sise.mibodega.ui.dashboard_fragments.Home
import com.sise.mibodega.ui.dashboard_fragments.Reportes
import com.sise.mibodega.ui.dashboard_fragments.Stock
import com.sise.mibodega.ui.dashboard_fragments.Ventas

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        remplazarFragmento(Home())

        //eventos onclick
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.inicio -> remplazarFragmento(Home())
                R.id.stock -> remplazarFragmento(Stock())
                R.id.ventas -> remplazarFragmento(Ventas())
                R.id.fiados -> remplazarFragmento(Fiado())
                R.id.reportes -> remplazarFragmento(Reportes())

                else -> {}
            }
            true
        }
    }


    //Para remplazar el layout
    private fun remplazarFragmento(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    // NUEVO: Funcion publica que permite que otros fragments cambien el tab activo
    // del menu inferior y muestren otro fragment. Por ejemplo, desde el Home,
    // al tocar "+ Nuevo producto" se llama navegarA(R.id.stock) y la app
    // se mueve al tab de Stock automaticamente, igual que si el usuario lo tocara en el menu.
    fun navegarA(itemId: Int) {
        binding.bottomNavigationView.selectedItemId = itemId
    }

}