package com.sise.mibodega.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AgregarProducto : AppCompatActivity() {

    private lateinit var btnGuardarProducto: Button
    private lateinit var txtNombreProducto: EditText
    private lateinit var spCategoria: Spinner
    private lateinit var txtPrecioVenta: EditText
    private lateinit var txtStockInicial: EditText

    private lateinit var btnTomarFoto: Button
    private lateinit var previewView: PreviewView

    // NUEVO: Boton para ir a gestionar categorias desde esta pantalla
    private lateinit var btnGestionarCategorias: Button

    //CameraX
    private var rutaFoto: String = ""
    private var imageCapture: ImageCapture? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        val db = DBHelper(this, null)

        btnGuardarProducto = findViewById(R.id.GuardarProducto)
        txtNombreProducto = findViewById(R.id.txtNombreProducto)
        spCategoria = findViewById(R.id.spCategoria)
        txtPrecioVenta = findViewById(R.id.txtPrecio)
        txtStockInicial = findViewById(R.id.txtStock)
        //FOTO
        btnTomarFoto = findViewById(R.id.btnFotoProducto)
        previewView = findViewById(R.id.previewView)
        // NUEVO: boton de gestionar categorias
        btnGestionarCategorias = findViewById(R.id.btnGestionarCategorias)

        ///////////////////////////Spinner de categorias//////////////////////////////////////
        // CAMBIO: Antes las categorias eran una lista fija en el codigo.
        // Ahora se cargan desde la base de datos para que el usuario pueda
        // agregar las suyas propias desde la pantalla de Gestion de Categorias
        cargarCategoriasEnSpinner(db)

        ///////////////////////////FOTO//////////////////////////////////////

        //Nuevo con cameraX
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        btnTomarFoto.setOnClickListener {
            captureImage()
        }

        //////////////////////////////////////////////////////////////////////

        // NUEVO: Al tocar "Gestionar Categorias" abre la pantalla de CRUD de categorias
        // Cuando regrese, recarga el spinner para mostrar los cambios
        btnGestionarCategorias.setOnClickListener {
            val intent = Intent(this, GestionCategoriasActivity::class.java)
            startActivity(intent)
        }

        /////////////////////////////////////////////////////////////////

        btnGuardarProducto.setOnClickListener {
            val inputNombreProducto = txtNombreProducto.text.toString().trim()
            val inputCategoria = spCategoria.selectedItem.toString().trim()
            val inputPrecioVentaString = txtPrecioVenta.text.toString().trim()
            val inputStockinicialString = txtStockInicial.text.toString().trim()

            val inputCodigoBarras = "no"


            if (inputNombreProducto.isEmpty() || inputCategoria.isEmpty() || inputPrecioVentaString.isEmpty() || inputStockinicialString.isEmpty()) {
                Toast.makeText(this, "Por favor, complete los campos", Toast.LENGTH_SHORT).show()
            } else {
                val inputPrecioVenta = inputPrecioVentaString.toFloat()
                val inputStockinicial = inputStockinicialString.toInt()


                db.insertar_producto(
                    inputNombreProducto,
                    inputCategoria,
                    inputCodigoBarras,
                    inputPrecioVenta,
                    inputStockinicial,
                    rutaFoto
                )
                Toast.makeText(
                    this,
                    "Producto $inputNombreProducto guardado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // NUEVO: Funcion que carga las categorias desde la base de datos y las pone en el Spinner
    // Se llama al abrir la pantalla y al volver de Gestion de Categorias
    private fun cargarCategoriasEnSpinner(db: DBHelper) {
        val nombresCategoria = db.listarNombresCategorias()

        // Si no hay categorias en la BD, ponemos un aviso en el spinner
        if (nombresCategoria.isEmpty()) {
            nombresCategoria.add("Sin categorías - Agrega una")
        }

        val adapterCategoria = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombresCategoria
        )
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria.adapter = adapterCategoria
    }

    // Al volver de GestionCategoriasActivity, recargamos el spinner con las categorias actualizadas
    override fun onResume() {
        super.onResume()
        val db = DBHelper(this, null)
        cargarCategoriasEnSpinner(db)
    }

    //FUNCIONES PARA LA CAMARA

    // Inicializa y usa la cameraX
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // CameraX Preview
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // CameraX Image Capture Use Case
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA  // Select back camera

            try {
                // Unbind previous use cases and bind new ones
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        // Carpeta FotosProductos dentro de la app
        val directorio = File(getExternalFilesDir(null), "FotosProductos")

        if (!directorio.exists()) {
            directorio.mkdirs()
        }

        val archivoFoto = File(directorio, "IMG_$timestamp.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(archivoFoto).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    rutaFoto = archivoFoto.absolutePath
                    Toast.makeText(
                        this@AgregarProducto,
                        "\n${archivoFoto.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@AgregarProducto,
                        "Error al guardar imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    // Revisa si todos los permisos son dados
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Maneja los resultados de los permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        public const val REQUEST_CODE_PERMISSIONS = 10
        public val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).toTypedArray()
    }
}