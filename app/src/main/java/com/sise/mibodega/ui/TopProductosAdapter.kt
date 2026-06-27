package com.sise.mibodega.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

// Adapter para el top 5 de productos mas vendidos
class TopProductosAdapter(
    private val context: Context,
    private val lista: ArrayList<DBHelper.ProductoMasVendido>
) : BaseAdapter() {

    override fun getCount() = lista.size
    override fun getItem(position: Int) = lista[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_top_producto, parent, false)

        val item = lista[position]

        // Posicion del ranking (#1, #2, etc.)
        vista.findViewById<TextView>(R.id.tvTopPosicion).text = "#${position + 1}"
        vista.findViewById<TextView>(R.id.tvTopNombreProducto).text = item.NombreProducto
        vista.findViewById<TextView>(R.id.tvTopVecesVendido).text = "Vendido ${item.VecesVendido} veces"
        vista.findViewById<TextView>(R.id.tvTopTotalGenerado).text = "S/ %.2f".format(item.TotalGenerado)

        return vista
    }
}