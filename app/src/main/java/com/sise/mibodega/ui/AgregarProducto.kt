package com.sise.mibodega.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import androidx.core.net.toUri
import java.io.File
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
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

    //CameraX
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

        ///////////////////////////Spinner de por mientras//////////////////////////////////////
        val Categoria = arrayOf(
            "Bebidas",
            "Abarrotes",
            "Básicos",
            "Lácteos",
            "Limpieza del Hogar",
            "Higiene y Cuidado Personal"
        )
        val adapterCategoria = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Categoria
        )
        adapterCategoria.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spCategoria.adapter = adapterCategoria


        ///////////////////////////FOTO//////////////////////////////////////

        //Nuevo con cameraX
        if (allPermissionsGranted()){
            startCamera()
        }else{
            ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS,REQUEST_CODE_PERMISSIONS)
        }
        btnTomarFoto.setOnClickListener {
            captureImage()
        }

        //////////////////////////////////////////////////////////////////////


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
                    inputStockinicial

                )
                Toast.makeText(
                    this,
                    "Producto $inputNombreProducto correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    //FUNCIONES PARA LA CAMARA

    // Inicializa y usa la cameraX
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // CameraX Preview Use Case
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

        val timestamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.US
        ).format(Date())

        // Carpeta FotosProductos dentro de la app
//        val directorio = File(filesDir, "FotosProducto")
        val directorio = File(
            getExternalFilesDir(null),
            "FotosProductos"
        )

        if (!directorio.exists()) {
            directorio.mkdirs()
        }

        val archivoFoto = File(
            directorio,
            "IMG_$timestamp.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            archivoFoto
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    outputFileResults: ImageCapture.OutputFileResults
                ) {
                    Toast.makeText(
                        this@AgregarProducto,
                        "\n${archivoFoto.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(
                    exception: ImageCaptureException
                ) {
                    Toast.makeText(
                        this@AgregarProducto,
                        "Error al guardar imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    // Checks if all required permissions are granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Handles the result of permission requests
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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
        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).toTypedArray()
    }




}











