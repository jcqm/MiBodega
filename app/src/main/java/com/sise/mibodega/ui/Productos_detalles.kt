package com.sise.mibodega.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sise.mibodega.R
import com.sise.mibodega.data.DBHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Productos_detalles : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtCantidad: EditText
    private lateinit var txtPrecio: EditText
    private lateinit var spCategoria: Spinner
    private lateinit var imgImagenDetalleProducto: ImageView
    private lateinit var btnGuardarEditar: Button
    private lateinit var btnEliminar: Button
    private lateinit var ProductoID: EditText

    private lateinit var btnTomarFoto: Button
    private lateinit var previewView: PreviewView

    //CameraX
    private var rutaFoto: String = ""
    private var imageCapture: ImageCapture? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_detalles)

        val db = DBHelper(this, null)

        txtNombre = findViewById<EditText>(R.id.txtEditarNombreProducto)
        txtCantidad = findViewById<EditText>(R.id.txtEditarStock)
        txtPrecio = findViewById<EditText>(R.id.txtEditarPrecioProducto)
        spCategoria = findViewById<Spinner>(R.id.spEditarCategoria)
        imgImagenDetalleProducto = findViewById<ImageView>(R.id.imgImagenDetallesProducto)
        btnGuardarEditar = findViewById<Button>(R.id.btnGuardarCambiosProducto)
        btnEliminar = findViewById<Button>(R.id.btnEliminar)
        ProductoID = findViewById<EditText>(R.id.ProductoID)
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

        ///////////////////////////FOTO//////////////////////////////////////

        //Nuevo con cameraX
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                AgregarProducto.Companion.REQUIRED_PERMISSIONS,
                AgregarProducto.Companion.REQUEST_CODE_PERMISSIONS
            )
        }
        btnTomarFoto.setOnClickListener {
            captureImage()
        }

        //////////////////////////////////////////////////////////////////////


        val adapterCategoria = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Categoria
        )
        adapterCategoria.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spCategoria.adapter = adapterCategoria


        val intent = this.intent
        if (intent != null) {
            val iDProducto = intent.getIntExtra("idProducto", 0)
            val nombreProducto = intent.getStringExtra("nombreProducto")
            val CategoriaProducto = intent.getStringExtra("CategoriaProducto")
            val precioProducto = intent.getFloatExtra("PrecioProducto", 0f)
            val stockProducto = intent.getIntExtra("StockProducto", 0)
            val FotoProducto = intent.getStringExtra("FotoProducto")

            val posicionCategoria = Categoria.indexOf(CategoriaProducto)

            txtNombre.setText(nombreProducto)
            txtPrecio.setText(precioProducto.toString())
            spCategoria.setSelection(posicionCategoria)
            txtCantidad.setText(stockProducto.toString())
            imgImagenDetalleProducto.setImageURI(FotoProducto?.toUri())
            ProductoID.setText(iDProducto.toString())


        }

        btnGuardarEditar.setOnClickListener {


            val idProductoString = ProductoID.text.toString().trim()
            val inputNombreProducto = txtNombre.text.toString().trim()
            val inputCategoria = spCategoria.selectedItem.toString().trim()
            val inputPrecioVentaString = txtPrecio.text.toString().trim()
            val inputStockinicialString = txtCantidad.text.toString().trim()

            val inputCodigoBarras = "no"


            if (inputNombreProducto.isEmpty() || inputCategoria.isEmpty() || inputPrecioVentaString.isEmpty() || inputStockinicialString.isEmpty()) {
                Toast.makeText(this, "Por favor, complete los campos", Toast.LENGTH_SHORT).show()
            } else {

                val idProductoInt = idProductoString.toInt()
                val inputPrecioVenta = inputPrecioVentaString.toFloat()
                val inputStockinicial = inputStockinicialString.toInt()


                db.editar_producto(
                    idProductoInt,
                    inputNombreProducto,
                    inputCategoria,
                    inputCodigoBarras,
                    inputPrecioVenta,
                    inputStockinicial,
                    rutaFoto

                )
                Toast.makeText(
                    this,
                    "Producto $inputNombreProducto editado",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)


            }
        }

        btnEliminar.setOnClickListener {
            val idProductoString = ProductoID.text.toString().trim()
            val inputNombreProducto = txtNombre.text.toString().trim()
            val idProductoInt = idProductoString.toInt()

            db.eliminar_producto(idProductoInt)

            Toast.makeText(
                this,
                "Producto $inputNombreProducto eliminado",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)

        }

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


        val timestamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.US
        ).format(Date())

        // Carpeta FotosProductos dentro de la app
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
                    rutaFoto = archivoFoto.absolutePath

                    Toast.makeText(
                        this@Productos_detalles,
                        "\n${archivoFoto.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(
                    exception: ImageCaptureException
                ) {
                    Toast.makeText(
                        this@Productos_detalles,
                        "Error al guardar imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )


    }

    // Revisa si todos los permison son dados
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
        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).toTypedArray()
    }


}
