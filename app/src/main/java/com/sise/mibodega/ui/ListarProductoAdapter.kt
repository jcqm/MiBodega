package com.sise.mibodega.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sise.mibodega.R
import java.text.FieldPosition

class ListProducto(
    var nombre: String,
    var categoria: String,
    var precio: Float,
    var unidades: Int,
    var image: Int
)

class ListarProductoAdapter(context: Context,private val numberList: List<ListProducto>):
    ArrayAdapter<ListProducto>(context,0,numberList) {









}