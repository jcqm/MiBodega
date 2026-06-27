package com.sise.mibodega.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper

// Esta pantalla permite ver, agregar, editar y eliminar categorias de productos.
// Se abre desde el fragment de Stock con el boton "Gestionar Categorias"
class GestionCategoriasActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var txtNuevaCategoria: EditText
    private lateinit var btnAgregarCategoria: Button
    private lateinit var listaCategorias: ListView

    // Lista que mantiene las categorias cargadas de la base de datos
    private lateinit var categorias: ArrayList<DBHelper.Categoria>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_categorias)

        db = DBHelper(this, null)

        // Inicializar vistas
        txtNuevaCategoria = findViewById(R.id.txtNuevaCategoria)
        btnAgregarCategoria = findViewById(R.id.btnAgregarCategoria)
        listaCategorias = findViewById(R.id.listaCategorias)

        // Cargar categorias al abrir la pantalla
        cargarCategorias()

        // AGREGAR: al tocar el boton, guarda la nueva categoria en la base de datos
        btnAgregarCategoria.setOnClickListener {
            val nombre = txtNuevaCategoria.text.toString().trim().uppercase()

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Escribe un nombre para la categoria", Toast.LENGTH_SHORT).show()
            } else {
                val exitoso = db.insertar_categoria(nombre)
                if (exitoso) {
                    Toast.makeText(this, "Categoria '$nombre' agregada", Toast.LENGTH_SHORT).show()
                    txtNuevaCategoria.text.clear()
                    cargarCategorias() // Refresca la lista
                } else {
                    Toast.makeText(this, "Esa categoria ya existe", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // EDITAR / ELIMINAR: al tocar una categoria de la lista, aparece un dialogo
        // con opciones para editar o eliminar esa categoria
        listaCategorias.setOnItemClickListener { _, _, position, _ ->
            val categoriaSeleccionada = categorias[position]
            mostrarOpcionesCategoria(categoriaSeleccionada)
        }
    }

    // Carga las categorias de la BD y las muestra en la lista
    private fun cargarCategorias() {
        categorias = db.listarCategorias()

        // Sacamos solo los nombres para mostrarlo en el ListView simple
        val nombres = categorias.map { it.nombreCategoria }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listaCategorias.adapter = adapter
    }

    // Muestra un dialogo con las opciones: Editar o Eliminar
    private fun mostrarOpcionesCategoria(categoria: DBHelper.Categoria) {
        val opciones = arrayOf("Editar", "Eliminar")

        MaterialAlertDialogBuilder(this)
            .setTitle(categoria.nombreCategoria)
            .setItems(opciones) { _, opcion ->
                when (opcion) {
                    0 -> mostrarDialogoEditar(categoria)  // Editar
                    1 -> mostrarDialogoEliminar(categoria) // Eliminar
                }
            }
            .show()
    }

    // Dialogo para editar el nombre de una categoria
    private fun mostrarDialogoEditar(categoria: DBHelper.Categoria) {
        // Creamos un EditText dinamico para que el usuario escriba el nuevo nombre
        // agregue uppercase() para que el texto sea guardado en mayusculas por defecto
        val input = EditText(this)
        input.setText(categoria.nombreCategoria)

        MaterialAlertDialogBuilder(this)
            .setTitle("Editar categoria")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = input.text.toString().trim().uppercase()
                if (nuevoNombre.isEmpty()) {
                    Toast.makeText(this, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show()
                } else {
                    val exitoso = db.editar_categoria(categoria.idCategoria, nuevoNombre)
                    if (exitoso) {
                        Toast.makeText(this, "Categoria actualizada", Toast.LENGTH_SHORT).show()
                        cargarCategorias() // Refresca la lista
                    } else {
                        Toast.makeText(this, "Ese nombre ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Dialogo de confirmacion para eliminar una categoria
    private fun mostrarDialogoEliminar(categoria: DBHelper.Categoria) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar categoria")
            .setMessage("¿Seguro que deseas eliminar '${categoria.nombreCategoria}'?\nLos productos que la usan no se borran, solo quedan sin categoria.")
            .setPositiveButton("Eliminar") { _, _ ->
                db.eliminar_categoria(categoria.idCategoria)
                Toast.makeText(this, "Categoria eliminada", Toast.LENGTH_SHORT).show()
                cargarCategorias() // Refresca la lista
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}