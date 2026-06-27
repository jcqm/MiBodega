package com.sise.mibodega.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

// Adapter para el historial de ventas en el fragment de Reportes
class ReporteVentaAdapter(
    private val context: Context,
    private val lista: ArrayList<DBHelper.VentaResumen>
) : BaseAdapter() {

    override fun getCount() = lista.size
    override fun getItem(position: Int) = lista[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_reporte_venta, parent, false)

        val venta = lista[position]

        vista.findViewById<TextView>(R.id.tvReporteVentaId).text = "Venta #${venta.VentaID}"
        vista.findViewById<TextView>(R.id.tvReporteFecha).text = venta.FechaVenta
        vista.findViewById<TextView>(R.id.tvReporteTotal).text = "S/ %.2f".format(venta.TotalVenta)

        return vista
    }
}