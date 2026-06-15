package com.sise.mibodega.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener {
    fun onButtonClick(position: Int, esIncremento: Boolean)
}

class ListarNuevaVentaAdapter(
    private val dataSet: ArrayList<DBHelper.Productos>,
    private val onButtonClick: OnItemClickListener
) : RecyclerView.Adapter<ListarNuevaVentaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre_NuevaVenta: TextView = view.findViewById(R.id.txtNombre_NuevaVenta)
        val txtPrecio_NuevaVenta: TextView = view.findViewById(R.id.txtPrecio_NuevaVenta)
        val txtNumero: TextView = view.findViewById(R.id.txtNumero)
        val btnMas: Button = view.findViewById(R.id.btnMas)
        val btnMenos: Button = view.findViewById(R.id.btnMenos)
        val imgLista_ProductoImagen: ImageView = view.findViewById(R.id.imgLista_ProductoImagen)

        init {
            // boton mas
            btnMas.setOnClickListener {
                val posicion = bindingAdapterPosition
                if (posicion != RecyclerView.NO_POSITION) {
                    val producto = dataSet[posicion]

                    // Verificamos si aún hay stock disponible para agregar
                    if (producto.cantidadSeleccionada < producto.StockProducto) {
                        producto.cantidadSeleccionada++
                        notifyItemChanged(posicion)
                        onButtonClick.onButtonClick(posicion, true)


                    } else {
                        Toast.makeText(
                            view.context,
                            "Stock máximo alcanzado (${producto.StockProducto})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            // boton menos
            btnMenos.setOnClickListener {
                val posicion = bindingAdapterPosition
                if (posicion != RecyclerView.NO_POSITION) {
                    val producto = dataSet[posicion]

                    if (producto.cantidadSeleccionada > 0) {
                        producto.cantidadSeleccionada--
                        notifyItemChanged(posicion)
                        onButtonClick.onButtonClick(posicion, false)

                    } else {
                        Toast.makeText(
                            view.context,
                            "Seleccione una cantidad correcta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.lista_productos_nueva_venta, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val productoActual = dataSet[position]
        val precioProProducto = productoActual.PrecioProducto * productoActual.cantidadSeleccionada

        viewHolder.txtNombre_NuevaVenta.text = productoActual.nombreProducto
        viewHolder.txtPrecio_NuevaVenta.text = "S/. " + precioProProducto.toString()

        viewHolder.txtNumero.text = productoActual.cantidadSeleccionada.toString()

        if (!productoActual.FotoProducto.isNullOrEmpty()) {
            viewHolder.imgLista_ProductoImagen.setImageURI(Uri.parse(productoActual.FotoProducto))
        } else {
            viewHolder.imgLista_ProductoImagen.setImageResource(R.drawable.baseline_insert_photo_24)
        }

    }




    override fun getItemCount() = dataSet.size
}

