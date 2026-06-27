package com.sise.mibodega.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

// Adapter para las ventas agrupadas por fecha
class VentasPorFechaAdapter(
    private val context: Context,
    private val lista: ArrayList<DBHelper.VentaPorFecha>
) : BaseAdapter() {

    override fun getCount() = lista.size
    override fun getItem(position: Int) = lista[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_venta_fecha, parent, false)

        val item = lista[position]

        vista.findViewById<TextView>(R.id.tvFechaAgrupada).text = item.Fecha
        vista.findViewById<TextView>(R.id.tvTotalDia).text = "S/ %.2f".format(item.TotalDia)
        vista.findViewById<TextView>(R.id.tvCantidadVentasDia).text = "${item.CantidadVentas} ventas"

        return vista
    }
}