package com.sise.mibodega.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import java.text.FieldPosition


class ListarProductoAdapter(
    private val context: Context,
    private val lista: ArrayList<DBHelper.Productos>
) : BaseAdapter() {

    override fun getCount() = lista.size

    override fun getItem(position: Int) = lista[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val vista = LayoutInflater.from(context)
            .inflate(R.layout.lista_productos, parent, false)

        val producto = lista[position]

        vista.findViewById<TextView>(R.id.txtLista_ProductoNombre).text = producto.nombreProducto
        vista.findViewById<TextView>(R.id.txtLista_ProductoCategoria).text =
            producto.CategoriaProducto
        vista.findViewById<TextView>(R.id.txtLista_ProductoPrecio).text =
            "S/. ${producto.PrecioProducto}"
        vista.findViewById<TextView>(R.id.txtLista_ProductoCantidad).text =
            "${producto.StockProducto} Ud."

        return vista
    }


}