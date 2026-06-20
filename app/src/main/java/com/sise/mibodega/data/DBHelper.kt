package com.sise.mibodega.data;

import android.R
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
            
            
            $Tabla_ProductoID INTEGER NOT NULL,
            $Tabla_PrecioUnitario REAL NOT NULL,

            
            FOREIGN KEY($Tabla_TiendaID) REFERENCES $Tabla_tienda($Tabla_TiendaID),
            FOREIGN KEY($Tabla_ProductoID) REFERENCES $Tabla_producto($Tabla_ProductoID)

        )
    """.trimIndent()
        db.execSQL(crearVenta)

        // Tabla detalle venta - Quiero unificar ambos, no veo la necesidad de tener detalle venta, solo sera venta
//        val crearDetalleVenta = """
//        CREATE TABLE $Tabla_detalleVenta (
//            $Tabla_DetalleVentaID INTEGER PRIMARY KEY AUTOINCREMENT,
//            $Tabla_VentaID INTEGER NOT NULL,
//            $Tabla_ProductoID INTEGER NOT NULL,
//            $Tabla_CantidadDetalleVenta INTEGER NOT NULL,
//            $Tabla_PrecioUnitario REAL NOT NULL,
//            $Tabla_Subtotal REAL NOT NULL,
//            FOREIGN KEY($Tabla_VentaID) REFERENCES $Tabla_venta($Tabla_VentaID),
//            FOREIGN KEY($Tabla_ProductoID) REFERENCES $Tabla_producto($Tabla_ProductoID)
//        )
//    """.trimIndent()
//        db.execSQL(crearDetalleVenta)

        // Tabla fiado
        val crearFiado = """
        CREATE TABLE $Tabla_fiado (
            $Tabla_FiadoID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_NombreCliente TEXT NOT NULL,
            $Tabla_MontoDeuda REAL NOT NULL,
            $Tabla_EstadoFiado TEXT NOT NULL,
            $Tabla_FechaFiado TEXT NOT NULL
        )
    """.trimIndent()
        db.execSQL(crearFiado)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS $Tabla_detalleVenta")
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

    // Para saber que stock esta bajo
    fun contarStockBajo(): Cursor {
        return readableDatabase.rawQuery(
            "SELECT COUNT(Stock) FROM $Tabla_producto WHERE $Tabla_StockProducto < 5",
            null
        )
    }


    //PARA LISTAR, primero defino el modelo que quiero usar, con las variable que entraran
    data class Productos(
        val IdProducto: Int,
        val nombreProducto: String,
        val CategoriaProducto: String,
        var PrecioProducto: Float,
        val StockProducto: Int,
        val FotoProducto: String,
        var cantidadSeleccionada: Int = 0, // Para contar la cantidad de items seleccionados en la venta
        var totalVendido: Float = 0.0f // para poder calcular el valor total vendido
    )


    //Creo la funcion que devuelva una array list
    fun ListarStock(): ArrayList<Productos> {
        val listaProducto = ArrayList<Productos>()
        val db = this.readableDatabase

        val consulta = db.rawQuery(
            "SELECT DISTINCT $Tabla_ProductoID, $Tabla_NombreProducto, $Tabla_CategoriaProducto, $Tabla_PrecioProducto,$Tabla_StockProducto, $Tabla_FotoProducto FROM $Tabla_producto",
            null
        )

        if (consulta.moveToFirst()) {
            do {
                val idProducto =
                    consulta.getInt(consulta.getColumnIndexOrThrow(Tabla_ProductoID))
                val nombreProducto =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_NombreProducto))
                val CategoriaProducto =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_CategoriaProducto))
                val PrecioProducto =
                    consulta.getFloat(consulta.getColumnIndexOrThrow(Tabla_PrecioProducto))
                val StockProducto =
                    consulta.getInt(consulta.getColumnIndexOrThrow(Tabla_StockProducto))
                val FotoProducto =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_FotoProducto))
                listaProducto.add(
                    Productos(
                        idProducto,
                        nombreProducto,
                        CategoriaProducto,
                        PrecioProducto,
                        StockProducto,
                        FotoProducto
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
    fun editar_producto(
        idProducto: Int,
        nombreProducto: String,
        categoria: String,
        codigoBarras: String,
        precio: Float,
        stock: Int,
        foto: String,

        ) {
        writableDatabase.use { db ->

            //Sacar id de tienda
            val tiendaID: String = obtenerDeBD(
                Tabla_tienda,
                Tabla_TiendaID,
            )

            val ContentValues = ContentValues().apply {
                put(Tabla_TiendaID, tiendaID.toInt())
                put(Tabla_NombreProducto, nombreProducto)
                put(Tabla_CategoriaProducto, categoria)
                put(Tabla_CodigoBarras, codigoBarras)
                put(Tabla_PrecioProducto, precio)
                put(Tabla_StockProducto, stock)
                put(Tabla_FotoProducto, foto)
            }

            val valoresProducto = arrayOf(idProducto.toString())

            val whereClause = "ProductoID = ?"
            db.update(Tabla_producto, ContentValues, whereClause, valoresProducto)
        }
    }

    //ELIMINAR PRODUCTO//
    fun eliminar_producto(
        idProducto: Int,
    ) {
        writableDatabase.use { db ->

            val valoresProducto = arrayOf(idProducto.toString())
            val whereArgs = arrayOf(idProducto.toString())

            val whereClause = "ProductoID = ?"
            db.delete(Tabla_producto, whereClause, whereArgs)
        }
    }
    //BUSCAR

    fun buscar_producto(nombreProducto: String): ArrayList<Productos> {
        val listaProducto = ArrayList<Productos>()
        this.readableDatabase.use { db ->
            // el ? como place holder
            val query =
                "SELECT DISTINCT $Tabla_ProductoID, $Tabla_NombreProducto, $Tabla_CategoriaProducto, $Tabla_PrecioProducto, $Tabla_StockProducto, $Tabla_FotoProducto " +
                        "FROM $Tabla_producto WHERE $Tabla_NombreProducto LIKE ?"

            // use el % para que encuentra con las primeras palabras tambien
            val argumentos = arrayOf("%$nombreProducto%")

            val consulta = db.rawQuery(query, argumentos)

            consulta.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getInt(cursor.getColumnIndexOrThrow(Tabla_ProductoID))
                        val nombre =
                            cursor.getString(cursor.getColumnIndexOrThrow(Tabla_NombreProducto))
                        val categoria =
                            cursor.getString(cursor.getColumnIndexOrThrow(Tabla_CategoriaProducto))
                        val precio =
                            cursor.getFloat(cursor.getColumnIndexOrThrow(Tabla_PrecioProducto))
                        val stock = cursor.getInt(cursor.getColumnIndexOrThrow(Tabla_StockProducto))
                        val foto =
                            cursor.getString(cursor.getColumnIndexOrThrow(Tabla_FotoProducto))

                        listaProducto.add(
                            Productos(id, nombre, categoria, precio, stock, foto)
                        )
                    } while (cursor.moveToNext())
                }
            }
        }

        return listaProducto
    }

    /////////////////// PARA FIADOS ////////////////

    /// Contar cuantos fiados pendientes


    //INSERTAR FIADO//////
    fun insertar_fiado(
        nombreCliente: String,
        montoDeuda: Float,
        estadoFiado: String,
        fechaFiado: String

    ) {
        writableDatabase.use { db ->

            val valoresFiado = ContentValues().apply {
                put(Tabla_NombreCliente, nombreCliente)
                put(Tabla_MontoDeuda, montoDeuda)
                put(Tabla_EstadoFiado, estadoFiado)
                put(Tabla_FechaFiado, fechaFiado)
            }

            db.insert(Tabla_fiado, null, valoresFiado)
        }
    }

    //////////////////LISTAR FIADOS //////////////////
    //PARA LISTAR, primero defino el modelo que quiero usar, con las variable que entraran
    data class Fiados(
        val IdFiado: Int,
        val NombreCliente: String,
        val MontoDeuda: Float,
        val EstadoFiado: String,
        val FechaFiado: String

    )

    //LISTAR/////
    fun ListarFiado(): ArrayList<Fiados> {
        val listaFiado = ArrayList<Fiados>()
        val db = this.readableDatabase

        val consulta = db.rawQuery(
            "SELECT DISTINCT $Tabla_FiadoID, $Tabla_NombreCliente, $Tabla_MontoDeuda, $Tabla_EstadoFiado,$Tabla_FechaFiado FROM $Tabla_fiado",
            null
        )

        if (consulta.moveToFirst()) {
            do {
                val IdFiado =
                    consulta.getInt(consulta.getColumnIndexOrThrow(Tabla_FiadoID))
                val NombreCliente =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_NombreCliente))
                val MontoDeuda =
                    consulta.getFloat(consulta.getColumnIndexOrThrow(Tabla_MontoDeuda))
                val EstadoFiado =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_EstadoFiado))
                val FechaFiado =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_FechaFiado))
                listaFiado.add(
                    Fiados(
                        IdFiado,
                        NombreCliente,
                        MontoDeuda,
                        EstadoFiado,
                        FechaFiado
                    )
                )
            } while (consulta.moveToNext())
        }
        consulta.close()
        db.close()

        return listaFiado
    }

    //para que devuela el numero total de la deuda
    fun contarFiado(): Cursor {
        val estado = "Pendiente"
        return readableDatabase.rawQuery(
            "SELECT SUM(MontoDeuda) FROM $Tabla_fiado WHERE $Tabla_EstadoFiado LIKE ?",
            arrayOf(estado)
        )
    }

    //para que devuela el numero total de personas que tiene deudas
    fun contarPersonasConFiado(): Cursor {
        val estado = "Pendiente"
        return readableDatabase.rawQuery(
            "SELECT COUNT(FiadoID) FROM $Tabla_fiado WHERE $Tabla_EstadoFiado LIKE ?",
            arrayOf(estado)
        )
    }

    ///// EDITAR ///
    fun editar_fiado(
        idFiado: Int,
        nombreCliente: String,
        montoDeuda: Float,
        estado: String,
        fechaFiado: String

    ) {
        writableDatabase.use { db ->


            val ContentValues = ContentValues().apply {
                put(Tabla_NombreCliente, nombreCliente)
                put(Tabla_MontoDeuda, montoDeuda)
                put(Tabla_EstadoFiado, estado)
                put(Tabla_FechaFiado, fechaFiado)
            }

            val valoresFiado = arrayOf(idFiado.toString())

            val whereClause = "FiadoID = ?"
            db.update(Tabla_fiado, ContentValues, whereClause, valoresFiado)
        }
    }

    ///////////////////////////////// Eliminar FIADOS /////////////////////////////////////////////////////
    fun eliminar_fiado(
        idFiado: Int,
    ) {
        writableDatabase.use { db ->

            val valoresFiado = arrayOf(idFiado.toString())
            val whereArgs = arrayOf(idFiado.toString())

            val whereClause = "FiadoID = ?"
            db.delete(Tabla_fiado, whereClause, whereArgs)
        }
    }


    ///// VENTAS /////////

    //INSERTAR Venta//////
    fun insertarVenta(
        productoID: Int,
        fechaVenta: String,
        precioUnitario: Float,
        totalVenta: Float

    ) {
        writableDatabase.use { db ->

            val valoresVenta = ContentValues().apply {
                put(Tabla_ProductoID, productoID)
                put(Tabla_FechaVenta, fechaVenta)
                put(Tabla_PrecioUnitario, precioUnitario)
                put(Tabla_TotalVenta, totalVenta)
            }

            db.insert(Tabla_venta, null, valoresVenta)
        }
    }


    //Declarando constantes y variables para crear la base de datos y sus tablas
    companion object {
        //nombre y version de la base de datos
        private const val DATABASE_NAME = "MiBodega"
        private const val DATABASE_VERSION = 6

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

        //tabla detalle Venta - Haciendo cambios
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
