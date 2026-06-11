package com.sise.mibodega.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

class ListarNuevaVentaAdapter(
    private val context: Context,
    private val lista: ArrayList<DBHelper.Productos>
) : BaseAdapter() {

    // Diccionario para recordar cuántas unidades va seleccionando el usuario por cada ID de producto
    private val cantidadesSeleccionadas = HashMap<Int, Int>()

    override fun getCount() = lista.size

    override fun getItem(position: Int) = lista[position]

    override fun getItemId(position: Int) = position.toLong()

    // Método público para que tu Activity obtenga la cantidad elegida de un producto específico
    fun obtenerCantidad(productoId: Int): Int {
        return cantidadesSeleccionadas[productoId] ?: 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.lista_productos_nueva_venta, parent, false)

        val producto = lista[position]

        // Usamos el ID del producto como clave, si no existe empezamos en 0
        // Nota: Asegúrate de tener el campo id o _id en tu modelo. Si se llama diferente, cámbialo aquí.
        val productoId = position // Como alternativa temporal usamos position, idealmente usa producto.idProducto
        val cantidadActual = cantidadesSeleccionadas[productoId] ?: 0

        vista.findViewById<TextView>(R.id.txtNombre_NuevaVenta).text = producto.nombreProducto

        val txtNumero = vista.findViewById<TextView>(R.id.txtNumero)
        txtNumero.text = cantidadActual.toString()

        vista.findViewById<TextView>(R.id.txtPrecio_NuevaVenta).text = "S/. ${producto.PrecioProducto}"

        val imgProducto = vista.findViewById<ImageView>(R.id.imgLista_ProductoImagen)
        if (!producto.FotoProducto.isNullOrEmpty()) {
            imgProducto.setImageURI(Uri.parse(producto.FotoProducto))
        } else {
            imgProducto.setImageResource(R.drawable.baseline_insert_photo_24)
        }

        // CONTROLADOR DE LOS BOTONES + y -
        val btnMas = vista.findViewById<Button>(R.id.btnMas)
        val btnMenos = vista.findViewById<Button>(R.id.btnMenos)
        val checkBox = vista.findViewById<CheckBox>(R.id.checkBox)

        // Lógica botón Más (+)
        btnMas.setOnClickListener {
            val actual = cantidadesSeleccionadas[productoId] ?: 0
            if (actual < producto.StockProducto) { // Límite: No superar el stock
                val nuevaCantidad = actual + 1
                cantidadesSeleccionadas[productoId] = nuevaCantidad
                txtNumero.text = nuevaCantidad.toString()
                checkBox.isChecked = true // Activa el check si selecciona unidades
            }
        }

        // Lógica botón Menos (-)
        btnMenos.setOnClickListener {
            val actual = cantidadesSeleccionadas[productoId] ?: 0
            if (actual > 0) {
                val nuevaCantidad = actual - 1
                cantidadesSeleccionadas[productoId] = nuevaCantidad
                txtNumero.text = nuevaCantidad.toString()

                if (nuevaCantidad == 0) {
                    checkBox.isChecked = false // Desmarca si llega a 0
                }
            }
        }

        // Evento manual por si marcan/desmarcan el CheckBox directamente
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                cantidadesSeleccionadas[productoId] = 0
                txtNumero.text = "0"
            } else if ((cantidadesSeleccionadas[productoId] ?: 0) == 0 && producto.StockProducto > 0) {
                cantidadesSeleccionadas[productoId] = 1
                txtNumero.text = "1"
            }
        }

        return vista
    }
}