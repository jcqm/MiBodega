package com.sise.mibodega.data;

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    //Para activar las llaves foraneas
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {

        // Tabla usuario
        val crearUsuario = """
        CREATE TABLE $Tabla_usuario (
            $Tabla_UsuarioID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_Usuario_Nombres TEXT NOT NULL,
            $Tabla_Usuarios_Apellidos TEXT NOT NULL
        )
    """.trimIndent()
        db.execSQL(crearUsuario)

        // Tabla tienda
        val crearTienda = """
        CREATE TABLE $Tabla_tienda (
            $Tabla_TiendaID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_UsuarioID INTEGER NOT NULL,
            $Tabla_Nombre_Tienda TEXT NOT NULL,
            $Tabla_Direccion_Tienda TEXT NOT NULL,
            FOREIGN KEY($Tabla_UsuarioID) REFERENCES $Tabla_usuario($Tabla_UsuarioID)
        )
    """.trimIndent()
        db.execSQL(crearTienda)

        // Tabla producto
        val crearProducto = """
        CREATE TABLE $Tabla_producto (
            $Tabla_ProductoID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_TiendaID INTEGER NOT NULL,
            $Tabla_NombreProducto TEXT NOT NULL,
            $Tabla_CategoriaProducto TEXT,
            $Tabla_CodigoBarras TEXT,
            $Tabla_PrecioProducto REAL NOT NULL,
            
            $Tabla_StockProducto TEX NOT NULL,
            $Tabla_FotoProducto TEXT,
            FOREIGN KEY($Tabla_TiendaID) REFERENCES $Tabla_tienda($Tabla_TiendaID)
        )
    """.trimIndent()
        db.execSQL(crearProducto)

        // Tabla venta
        val crearVenta = """
        CREATE TABLE $Tabla_venta (
            $Tabla_VentaID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_TiendaID INTEGER NOT NULL,
            $Tabla_FechaVenta TEXT NOT NULL,
            $Tabla_TotalVenta REAL NOT NULL,
            FOREIGN KEY($Tabla_TiendaID) REFERENCES $Tabla_tienda($Tabla_TiendaID)
        )
    """.trimIndent()
        db.execSQL(crearVenta)

        // Tabla detalle venta
        val crearDetalleVenta = """
        CREATE TABLE $Tabla_detalleVenta (
            $Tabla_DetalleVentaID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_VentaID INTEGER NOT NULL,
            $Tabla_ProductoID INTEGER NOT NULL,
            $Tabla_CantidadDetalleVenta INTEGER NOT NULL,
            $Tabla_PrecioUnitario REAL NOT NULL,
            $Tabla_Subtotal REAL NOT NULL,
            FOREIGN KEY($Tabla_VentaID) REFERENCES $Tabla_venta($Tabla_VentaID),
            FOREIGN KEY($Tabla_ProductoID) REFERENCES $Tabla_producto($Tabla_ProductoID)
        )
    """.trimIndent()
        db.execSQL(crearDetalleVenta)

        // Tabla fiado
        val crearFiado = """
        CREATE TABLE $Tabla_fiado (
            $Tabla_FiadoID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_VentaID INTEGER NOT NULL,
            $Tabla_NombreCliente TEXT NOT NULL,
            $Tabla_MontoDeuda REAL NOT NULL,
            $Tabla_EstadoFiado TEXT NOT NULL,
            $Tabla_FechaFiado TEXT NOT NULL,
            FOREIGN KEY($Tabla_VentaID) REFERENCES $Tabla_venta($Tabla_VentaID)
        )
    """.trimIndent()
        db.execSQL(crearFiado)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $Tabla_detalleVenta")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_fiado")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_venta")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_producto")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_tienda")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_usuario")

        onCreate(db)
    }

    // Insertar Usuario, en la vista LoginActivity
    fun insertar_usuario(
        nombre: String,
        apellidos: String,
        nombre_tienda: String,
        direccion_tienda: String
    ) {
        writableDatabase.use { db ->

            // insertar usuario
            val valoresUsuario = ContentValues().apply {
                put(Tabla_Usuario_Nombres, nombre)
                put(Tabla_Usuarios_Apellidos, apellidos)
            }

            // aca jale la info de usuario id, para poder usarla en insertar tienda
            val usuarioId = db.insert(Tabla_usuario, null, valoresUsuario)

            // insertar tienda
            val valoresTienda = ContentValues().apply {
                put(Tabla_UsuarioID, usuarioId)
                put(Tabla_Nombre_Tienda, nombre_tienda)
                put(Tabla_Direccion_Tienda, direccion_tienda)
            }

            db.insert(Tabla_tienda, null, valoresTienda)
        }
    }

    // mostrar nombre en el dashboar
    fun mostrarNombre(): Cursor {
        return readableDatabase.rawQuery("SELECT $Tabla_Usuario_Nombres FROM $Tabla_usuario", null)
    }

    //Una vez el usuario se haya registrado, quiero que que ya no aparezca el login tomando en cuenta si hay un usuario ya creado
    fun ExisteUsuario(): Boolean {
        val db = this.readableDatabase
        val consulta = db.rawQuery("SELECT EXISTS(SELECT 1 FROM $Tabla_usuario LIMIT 1 )", null)
        var existe = false

        if (consulta.moveToFirst()) {
            existe = consulta.getInt(0) > 0
        }
        consulta.close()

        return existe
    }

    //Consultas para el stock, listar, agregar, eliminar, editar, no se, lo que tenga que ver con el stock

    //para que devuela el numero total de items en el stock
    fun contarStock(): Cursor {
        return readableDatabase.rawQuery("SELECT SUM(Stock) FROM $Tabla_producto", null)
    }

    //PARA LISTAR, primero defino el modelo que quiero usar, con las variable que entraran
    data class Productos(
        val nombreProducto: String,
        val CategoriaProducto: String,
        val PrecioProducto: Float,
        val StockProducto: Int,

    )

    //Creo la funcion que devuelva una array list
    fun ListarStock(): ArrayList<Productos> {
        val listaProducto = ArrayList<Productos>()
        val db = this.readableDatabase

        val consulta = db.rawQuery(
            "SELECT DISTINCT $Tabla_NombreProducto, $Tabla_CategoriaProducto, $Tabla_PrecioProducto,$Tabla_StockProducto FROM $Tabla_producto",
            null
        )

        if (consulta.moveToFirst()) {
            do {
                val nombreProducto =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_NombreProducto))
                val CategoriaProducto =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_CategoriaProducto))
                val PrecioProducto =
                    consulta.getFloat(consulta.getColumnIndexOrThrow(Tabla_PrecioProducto))
                val StockProducto =
                    consulta.getInt(consulta.getColumnIndexOrThrow(Tabla_StockProducto))
                listaProducto.add(
                    Productos(
                        nombreProducto,
                        CategoriaProducto,
                        PrecioProducto,
                        StockProducto
                    )
                )
            } while (consulta.moveToNext())
        }
        consulta.close()
        db.close()

        return listaProducto
    }


    //Funcion para poder sacar cualquier informacion de una tabla
    fun obtenerDeBD(
        nombreTabla: String,
        select: String, // aca pones lo que quieres sacaar
//        selectBy: String,
//        selectNombre: String
    ): String {
        val db = this.readableDatabase
        var resultado = "Error"
        val c: Cursor = db.query(nombreTabla, arrayOf(select), null, null, null, null, null)
        if (c.count == 1) {
            c.moveToFirst()
            resultado = c.getString(c.getColumnIndexOrThrow(select))
        }
        c.close()
        return resultado
    }


    ////////// CRUD STOCK/PRODUCTOS ////////////

    //INSERTAR PRODUCTO//////
    fun insertar_producto(
        nombreProducto: String,
        categoria: String,
        codigoBarras: String,
        precio: Float,
        stock: Int,

        foto: String //NUEVO

    ) {
        writableDatabase.use { db ->

            //Sacar id de tienda
            val tiendaID: String = obtenerDeBD(
                Tabla_tienda,
                Tabla_TiendaID,
            )

            val valoresProducto = ContentValues().apply {
                put(Tabla_TiendaID, tiendaID.toInt())
                put(Tabla_NombreProducto, nombreProducto)
                put(Tabla_CategoriaProducto, categoria)
                put(Tabla_CodigoBarras, codigoBarras)
                put(Tabla_PrecioProducto, precio)
                put(Tabla_StockProducto, stock)
                put(Tabla_FotoProducto, foto)//NUEVO
            }

            db.insert(Tabla_producto, null, valoresProducto)
        }
    }

    //EDITAR PRODUCTO//




    //Declarando constantes y variables para crear la base de datos y sus tablas
    companion object {
        //nombre y version de la base de datos
        private const val DATABASE_NAME = "MiBodega"
        private const val DATABASE_VERSION = 2

        //Tabla usuario
        const val Tabla_usuario = "usuario"
        const val Tabla_UsuarioID = "UsuarioID"
        const val Tabla_Usuario_Nombres = "Nombre"
        const val Tabla_Usuarios_Apellidos = "Apellidos"

        //Tabla tienda
        const val Tabla_tienda = "tienda"
        const val Tabla_TiendaID = "TiendaID"
        const val Tabla_Nombre_Tienda = "Nombre_Tienda"
        const val Tabla_Direccion_Tienda = "DireccionTienda"

        //tabla producto
        const val Tabla_producto = "producto"
        const val Tabla_ProductoID = "ProductoID"
        const val Tabla_NombreProducto = "NombreProducto"
        const val Tabla_CategoriaProducto = "Categoria"
        const val Tabla_CodigoBarras = "CodigoBarras"
        const val Tabla_PrecioProducto = "Precio"
        const val Tabla_StockProducto = "Stock"
        const val Tabla_FotoProducto = "FotoProducto"

        //Tabla Venta
        const val Tabla_venta = "venta"
        const val Tabla_VentaID = "VentaID"
        const val Tabla_FechaVenta = "FechaVenta"
        const val Tabla_TotalVenta = "Total"

        //tabla detalle Venta
        const val Tabla_detalleVenta = "detalle_venta"
        const val Tabla_DetalleVentaID = "DetalleVentaID"
        const val Tabla_CantidadDetalleVenta = "Cantidad"
        const val Tabla_PrecioUnitario = "PrecioUnitario"
        const val Tabla_Subtotal = "Subtotal"

        //tabla fiado
        const val Tabla_fiado = "fiado"
        const val Tabla_FiadoID = "FiadoID"
        const val Tabla_NombreCliente = "NombreCliente"
        const val Tabla_MontoDeuda = "MontoDeuda"
        const val Tabla_EstadoFiado = "Estado"
        const val Tabla_FechaFiado = "FechaFiado"
    }


}
