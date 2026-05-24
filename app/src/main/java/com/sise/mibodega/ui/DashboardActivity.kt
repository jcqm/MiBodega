package com.sise.mibodega.ui

import  com.sise.mibodega.data.DBHelper
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import com.sise.mibodega.R
import com.sise.mibodega.databinding.ActivityDashboardBinding
import com.sise.mibodega.ui.dashboard_fragments.Fiado
import com.sise.mibodega.ui.dashboard_fragments.Home
import com.sise.mibodega.ui.dashboard_fragments.Reportes
import com.sise.mibodega.ui.dashboard_fragments.Stock
import com.sise.mibodega.ui.dashboard_fragments.Ventas
import androidx.fragment.app.Fragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var tvNombre: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_dashboard)
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

                else -> {
                }
            }
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    //Para remplazar el layout
    private fun remplazarFragmento(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()


    }

}
