package com.sise.mibodega.ui.dashboard_fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputBinding
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import com.sise.mibodega.databinding.ActivityDashboardBinding
import com.sise.mibodega.databinding.FragmentStockBinding
import com.sise.mibodega.ui.DashboardActivity
import com.sise.mibodega.ui.ListarProductoAdapter
import com.sise.mibodega.ui.Productos_detalles


class Stock : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var btnAgregarProducto: Button
    lateinit var listaResultado: ListView
    lateinit var lblCantidadItems: TextView
    lateinit var barraBuscadora: SearchView


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
        listaResultado = view.findViewById<ListView>(R.id.listaResultado)
        barraBuscadora = view.findViewById<SearchView>(R.id.barraBuscadoraProductos)


        ////////////// LISTAR ////////////////////////////////////
        // Tuve que crear un adapter personalizado


        val productos = ArrayList<DBHelper.Productos>()
        productos.addAll(dbHelper.ListarStock())

        // Se inicializa el adaptador con esa lista
        val adapter = ListarProductoAdapter(requireContext(), productos)
        listaResultado.adapter = adapter

        // para mostrar la catidad
        actualizarContadorTexto(productos.size)


        // Para usar el search view, esta primera parte es un escuchador para la barr

        barraBuscadora.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                actualizarLista(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                actualizarLista(newText.orEmpty())
                return true
            }

            // para actualizar la lista del adapter
            private fun actualizarLista(texto: String) {
                productos.clear() // Vaciamos los productos actuales
                if (texto.isEmpty()) {
                    productos.addAll(dbHelper.ListarStock()) // si esta vacio mostramos
                } else {
                    productos.addAll(dbHelper.buscar_producto(texto)) // Si busca, filtramos con SQLite
                }

                // Actualiza el TextView de items segun el resultado
                actualizarContadorTexto(productos.size)

                adapter.notifyDataSetChanged()
            }
        })

        // al dar click lo jala a una vista detallada donde puede elminar y editar
        listaResultado.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val productoSeleccionado = productos[position]

            val intent = Intent(requireContext(), Productos_detalles::class.java).apply {
                putExtra("idProducto", productoSeleccionado.IdProducto)
                putExtra("nombreProducto", productoSeleccionado.nombreProducto)
                putExtra("CategoriaProducto", productoSeleccionado.CategoriaProducto)
                putExtra("PrecioProducto", productoSeleccionado.PrecioProducto)
                putExtra("StockProducto", productoSeleccionado.StockProducto)
                putExtra("FotoProducto", productoSeleccionado.FotoProducto)
            }
            startActivity(intent)
        }

        // ANTIGUO, LO DEJO AQUI POR SI ACASO LO NECESITO
        //        val productos = dbHelper.ListarStock()
//
//        val adapter = ListarProductoAdapter(requireContext(), productos)
//        listaResultado.adapter = adapter
//
//        listaResultado.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            // Obtener el producto  usando la posición seleccionada
//            val productoSeleccionado = productos[position]
//
//            // Creo el Intent apuntando a la actividad de detalles
//            val intent = Intent(requireContext(), Productos_detalles::class.java)
//
//            intent.putExtra("idProducto", productoSeleccionado.IdProducto)
//            intent.putExtra("nombreProducto", productoSeleccionado.nombreProducto)
//            intent.putExtra("CategoriaProducto", productoSeleccionado.CategoriaProducto)
//            intent.putExtra("PrecioProducto", productoSeleccionado.PrecioProducto)
//            intent.putExtra("StockProducto", productoSeleccionado.StockProducto)
//            intent.putExtra("FotoProducto", productoSeleccionado.FotoProducto)
//
//            startActivity(intent)
//        }


        ///////////// Mostrar cantidad////////////////
        val cursorCantidad = dbHelper.contarStock()

        if (cursorCantidad.moveToFirst()) {
            val cantidad = cursorCantidad.getString(0)
            lblCantidadItems.text = cantidad + " Unidades"
        }
        //////////////////////////////////////////////////////

        //para dirigirse a agregar producto
        btnAgregarProducto.setOnClickListener {

            val intent = Intent(
                requireActivity(), com.sise.mibodega.ui.AgregarProducto::class.java
            )

            startActivity(intent)
        }

        return view
    }

    //funcion para mostrar la cantidad
    private fun actualizarContadorTexto(cantidad: Int) {
        lblCantidadItems.text = "$cantidad Unidades"
    }


}