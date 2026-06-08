package com.sise.mibodega.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

class ListarFiadoAdapter(
    private val context: Context,
    private val lista: ArrayList<DBHelper.Fiados>
) : BaseAdapter() {

    override fun getCount() = lista.size

    override fun getItem(position: Int) = lista[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // si ya existe la vista se reutiliza
        val vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.lista_fiados, parent, false)

        val fiado = lista[position]

        vista.findViewById<TextView>(R.id.txtLista_FiadosNombre).text = fiado.NombreCliente
        vista.findViewById<TextView>(R.id.txtLista_FiadosFechaFiado).text =
            fiado.FechaFiado
        vista.findViewById<TextView>(R.id.txtLista_FiadosDeuda).text =
            "S/. ${fiado.MontoDeuda}"

        return vista
    }
}