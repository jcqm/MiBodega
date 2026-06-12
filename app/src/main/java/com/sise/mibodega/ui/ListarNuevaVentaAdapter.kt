package com.sise.mibodega.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import java.text.FieldPosition
import kotlin.math.max
import kotlin.math.min


class ListarNuevaVentaAdapter(
    private val context: Context, private val lista: ArrayList<DBHelper.Productos>
) : BaseAdapter() {

    override fun getCount() = lista.size

    override fun getItem(position: Int) = lista[position]

    override fun getItemId(position: Int) = position.toLong()

    private lateinit var btnMas: Button
    private lateinit var btmMenos: Button
    private lateinit var txtNumero: TextView
    private lateinit var checkBox: CheckBox

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // si ya existe la vista se reutiliza
        val vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.lista_productos_nueva_venta, parent, false)

        val producto = lista[position]


        val minimo = 0
        val maximo = producto.StockProducto
        var contador = 0
        var precioActualizado = 0.0f
        var precio = producto.PrecioProducto

        btnMas = vista.findViewById<Button>(R.id.btnMas)
        btmMenos = vista.findViewById<Button>(R.id.btnMenos)
//        txtNumero = vista.findViewById<TextView>(R.id.txtNumero)

        checkBox = vista.findViewById<CheckBox>(R.id.checkBox)
        vista.findViewById<TextView>(R.id.txtNombre_NuevaVenta).text = producto.nombreProducto
//        vista.findViewById<TextView>(R.id.txtPrecio_NuevaVenta).text =
//            "S/. ${producto.PrecioProducto}"

        // se convierte la ruta a URI para mostrarla en el ImageView
        val imgProducto = vista.findViewById<ImageView>(R.id.imgLista_ProductoImagen)

        if (!producto.FotoProducto.isNullOrEmpty()) {
            imgProducto.setImageURI(Uri.parse(producto.FotoProducto))
        } else {

            // placeholdre por si no tiene foto
            imgProducto.setImageResource(R.drawable.baseline_insert_photo_24)
        }

        btnMas.setOnClickListener {
            //para stock
            var numero = vista.findViewById<TextView>(R.id.txtNumero)
            var numeroActual = numero.text.toString()
            val numeroGuardadInt = numeroActual.toInt()

            if (numeroGuardadInt >= maximo) {
                Toast.makeText(context, "Stock de $maximo alcanzado", Toast.LENGTH_SHORT)
                    .show()
            } else {
                contador += 1
                precioActualizado = (numeroGuardadInt + 1) * precio


                vista.findViewById<TextView>(R.id.txtNumero).text = contador.toString()
                vista.findViewById<TextView>(R.id.txtPrecio_NuevaVenta).text =
                    "S/. ${precioActualizado.toString()}"

            }

        }

        btmMenos.setOnClickListener {
            var numero = vista.findViewById<TextView>(R.id.txtNumero)
            var numeroActual = numero.text.toString()
            val numeroGuardadInt = numeroActual.toInt()

            // para precio

            var precioBoton = vista.findViewById<TextView>(R.id.txtPrecio_NuevaVenta)
            var precioActual = precioBoton.text.toString()
            val precioGuardadoInt = precioActual.removeRange(0, 3).toFloat()

            if (numeroGuardadInt <= 0) {
                Toast.makeText(context, "Seleccione un numero valido", Toast.LENGTH_SHORT)
                    .show()
            } else {
                contador -= 1
                vista.findViewById<TextView>(R.id.txtNumero).text = contador.toString()
                precioActualizado = precioGuardadoInt - precio
                vista.findViewById<TextView>(R.id.txtPrecio_NuevaVenta).text =
                    "S/. ${precioActualizado}"

            }
        }







        return vista
    }


}
