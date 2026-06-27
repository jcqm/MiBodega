package com.sise.mibodega.data

// CORRECCIÓN: Se eliminó el import de "android.R" que estaba de mas y no se usaba
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

        // NUEVO: Tabla categoria - guarda las categorias que el usuario crea
        // Antes las categorias estaban hardcodeadas en el codigo (Bebidas, Abarrotes, etc.)
        // Ahora se guardan en la base de datos y el usuario puede agregar las que quiera
        val crearCategoria = """
        CREATE TABLE $Tabla_categoria (
            $Tabla_CategoriaID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_NombreCategoria TEXT NOT NULL UNIQUE
        )
    """.trimIndent()
        db.execSQL(crearCategoria)

        // NUEVO: Insertamos las categorias por defecto para que la app no arranque vacia
        val categoriasPorDefecto = listOf(
            "Bebidas", "Abarrotes", "Básicos", "Lácteos",
            "Limpieza del Hogar", "Higiene y Cuidado Personal"
        )
        for (nombre in categoriasPorDefecto) {
            val valores = ContentValues().apply {
                put(Tabla_NombreCategoria, nombre)
            }
            db.insert(Tabla_categoria, null, valores)
        }

        // Tabla producto
        // CORRECCIÓN: "TEX NOT NULL" estaba mal escrito, causaba que la base de datos
        // no se creara correctamente. Se corrigió a "TEXT NOT NULL"
        val crearProducto = """
        CREATE TABLE $Tabla_producto (
            $Tabla_ProductoID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_TiendaID INTEGER NOT NULL,
            $Tabla_NombreProducto TEXT NOT NULL,
            $Tabla_CategoriaProducto TEXT,
            $Tabla_CodigoBarras TEXT,
            $Tabla_PrecioProducto REAL NOT NULL,
            $Tabla_StockProducto TEXT NOT NULL,
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
        // CORRECCIÓN: Se agregó la columna Cantidad en detalle_venta, que faltaba
        // y es necesaria para registrar cuantas unidades se vendieron de cada producto
        val crearDetalleVenta = """
        CREATE TABLE $Tabla_detalleVenta (
            $Tabla_DetalleVentaID INTEGER PRIMARY KEY AUTOINCREMENT,
            $Tabla_VentaID INTEGER NOT NULL,
            $Tabla_ProductoID INTEGER NOT NULL,
            $Tabla_CantidadDetalleVenta INTEGER NOT NULL,
            $Tabla_PrecioUnitario REAL NOT NULL,
            FOREIGN KEY($Tabla_VentaID) REFERENCES $Tabla_venta($Tabla_VentaID),
            FOREIGN KEY($Tabla_ProductoID) REFERENCES $Tabla_producto($Tabla_ProductoID)
        )
    """.trimIndent()
        db.execSQL(crearDetalleVenta)

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
        db.execSQL("DROP TABLE IF EXISTS $Tabla_detalleVenta")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_fiado")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_venta")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_producto")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_categoria")
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

    // mostrar nombre en el dashboard
    fun mostrarNombre(): Cursor {
        return readableDatabase.rawQuery("SELECT $Tabla_Usuario_Nombres FROM $Tabla_usuario", null)
    }

    //Una vez el usuario se haya registrado, quiero que ya no aparezca el login tomando en cuenta si hay un usuario ya creado
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
        // CORRECCIÓN: Se eliminó db.close() aqui porque cerrar la base de datos manualmente
        // puede causar crashes si otras partes del codigo intentan usarla despues.
        // SQLiteOpenHelper maneja el cierre automaticamente.

        return listaProducto
    }

    //Funcion para poder sacar cualquier informacion de una tabla
    // CORRECCIÓN: Antes esta funcion solo funcionaba si habia exactamente 1 fila en la tabla.
    // Ahora toma el primer resultado, lo cual es correcto para sacar el TiendaID
    fun obtenerDeBD(
        nombreTabla: String,
        select: String, // aca pones lo que quieres sacar
    ): String {
        val db = this.readableDatabase
        var resultado = "Error"
        val c: Cursor = db.query(nombreTabla, arrayOf(select), null, null, null, null, null)
        if (c.moveToFirst()) {  // CORRECCIÓN: era "c.count == 1", ahora usa moveToFirst() directamente
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

            // CORRECCIÓN: La variable se llamaba "ContentValues" igual que la clase de Android,
            // eso es confuso y puede causar errores. Se renombro a "valores"
            val valores = ContentValues().apply {
                put(Tabla_TiendaID, tiendaID.toInt())
                put(Tabla_NombreProducto, nombreProducto)
                put(Tabla_CategoriaProducto, categoria)
                put(Tabla_CodigoBarras, codigoBarras)
                put(Tabla_PrecioProducto, precio)
                put(Tabla_StockProducto, stock)
                put(Tabla_FotoProducto, foto)
            }

            val whereClause = "ProductoID = ?"
            val whereArgs = arrayOf(idProducto.toString())
            db.update(Tabla_producto, valores, whereClause, whereArgs)
        }
    }

    //ELIMINAR PRODUCTO//
    fun eliminar_producto(
        idProducto: Int,
    ) {
        writableDatabase.use { db ->
            // CORRECCIÓN: Antes habia dos arrays identicos (valoresProducto y whereArgs).
            // Se simplificó a uno solo
            val whereClause = "ProductoID = ?"
            val whereArgs = arrayOf(idProducto.toString())
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

    /////////////////// PARA REPORTES ////////////////

    // Para listar el historial de ventas
    data class VentaResumen(
        val VentaID: Int,
        val FechaVenta: String,
        val TotalVenta: Float
    )

    fun ListarVentas(): ArrayList<VentaResumen> {
        val listaVentas = ArrayList<VentaResumen>()
        val db = this.readableDatabase

        val consulta = db.rawQuery(
            "SELECT $Tabla_VentaID, $Tabla_FechaVenta, $Tabla_TotalVenta FROM $Tabla_venta ORDER BY $Tabla_VentaID DESC",
            null
        )

        if (consulta.moveToFirst()) {
            do {
                val ventaId = consulta.getInt(consulta.getColumnIndexOrThrow(Tabla_VentaID))
                val fechaVenta =
                    consulta.getString(consulta.getColumnIndexOrThrow(Tabla_FechaVenta))
                val totalVenta =
                    consulta.getFloat(consulta.getColumnIndexOrThrow(Tabla_TotalVenta))
                listaVentas.add(VentaResumen(ventaId, fechaVenta, totalVenta))
            } while (consulta.moveToNext())
        }
        consulta.close()

        return listaVentas
    }

    // Total vendido en una fecha especifica (la usamos para "ventas de hoy")
    fun contarVentasPorFecha(fecha: String): Cursor {
        return readableDatabase.rawQuery(
            "SELECT SUM($Tabla_TotalVenta) FROM $Tabla_venta WHERE $Tabla_FechaVenta = ?",
            arrayOf(fecha)
        )
    }

    // Total vendido historico (todas las ventas desde siempre)
    fun contarVentasTotal(): Cursor {
        return readableDatabase.rawQuery(
            "SELECT SUM($Tabla_TotalVenta) FROM $Tabla_venta",
            null
        )
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
        // CORRECCIÓN: Se eliminó db.close() por la misma razon que en ListarStock()

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

    ///// EDITAR FIADO ///
    fun editar_fiado(
        idFiado: Int,
        nombreCliente: String,
        montoDeuda: Float,
        estado: String,
        fechaFiado: String
    ) {
        writableDatabase.use { db ->

            // CORRECCIÓN: La variable se llamaba "ContentValues" igual que la clase de Android.
            // Se renombro a "valores" para evitar confusion
            val valores = ContentValues().apply {
                put(Tabla_NombreCliente, nombreCliente)
                put(Tabla_MontoDeuda, montoDeuda)
                put(Tabla_EstadoFiado, estado)
                put(Tabla_FechaFiado, fechaFiado)
            }

            val whereClause = "FiadoID = ?"
            val whereArgs = arrayOf(idFiado.toString())
            db.update(Tabla_fiado, valores, whereClause, whereArgs)
        }
    }

    ///////////////////////////////// Eliminar FIADOS /////////////////////////////////////////////////////
    fun eliminar_fiado(
        idFiado: Int,
    ) {
        writableDatabase.use { db ->
            // CORRECCIÓN: Igual que en eliminar_producto, habia dos arrays identicos.
            // Se simplificó a uno solo
            val whereClause = "FiadoID = ?"
            val whereArgs = arrayOf(idFiado.toString())
            db.delete(Tabla_fiado, whereClause, whereArgs)
        }
    }


    ///// VENTAS /////////

    //INSERTAR Venta//////
    fun insertarVentaDetalle(
        fechaVenta: String,
        totalVenta: Float,
        productosSeleccionados: List<Productos>
    ) {
        writableDatabase.use { db ->

            val tiendaID: String = obtenerDeBD(
                Tabla_tienda,
                Tabla_TiendaID,
            )
            val venta = ContentValues().apply {
                put(Tabla_TiendaID, tiendaID)
                put(Tabla_FechaVenta, fechaVenta)
                put(Tabla_TotalVenta, totalVenta)
            }

            val ventaId = db.insert(Tabla_venta, null, venta)

            for (producto in productosSeleccionados) {
                // CORRECCIÓN: El subtotal de cada producto es precio x cantidad
                // Antes solo se guardaba el precio unitario sin multiplicar
                val subtotal = producto.PrecioProducto * producto.cantidadSeleccionada

                val detalleVenta = ContentValues().apply {
                    put(Tabla_VentaID, ventaId)
                    put(Tabla_ProductoID, producto.IdProducto)
                    put(Tabla_CantidadDetalleVenta, producto.cantidadSeleccionada)
                    put(Tabla_PrecioUnitario, producto.PrecioProducto)
                    put(Tabla_Subtotal, subtotal)
                }
                db.insert(Tabla_detalleVenta, null, detalleVenta)

                // CORRECCIÓN: Se agrego la actualizacion del stock al momento de vender.
                // Antes se registraba la venta pero el stock del producto nunca bajaba.
                // Ahora restamos la cantidad vendida del stock disponible en la tabla producto.
                val nuevoStock = producto.StockProducto - producto.cantidadSeleccionada
                val actualizarStock = ContentValues().apply {
                    put(Tabla_StockProducto, nuevoStock)
                }
                db.update(
                    Tabla_producto,
                    actualizarStock,
                    "$Tabla_ProductoID = ?",
                    arrayOf(producto.IdProducto.toString())
                )
            }
        }
    }

    /////////////////// NUEVAS QUERIES PARA REPORTES ////////////////

    // Data class para productos mas vendidos
    data class ProductoMasVendido(
        val NombreProducto: String,
        val VecesVendido: Int,
        val TotalGenerado: Float
    )

    // Data class para ventas agrupadas por fecha
    data class VentaPorFecha(
        val Fecha: String,
        val TotalDia: Float,
        val CantidadVentas: Int
    )

    // Top 5 productos mas vendidos (JOIN detalle_venta con producto)
    fun obtenerProductosMasVendidos(): ArrayList<ProductoMasVendido> {
        val lista = ArrayList<ProductoMasVendido>()
        val db = this.readableDatabase

        val consulta = db.rawQuery(
            """
        SELECT p.$Tabla_NombreProducto, 
               SUM(dv.$Tabla_CantidadDetalleVenta) as VecesVendido,
               SUM(dv.$Tabla_PrecioUnitario * dv.$Tabla_CantidadDetalleVenta) as TotalGenerado
        FROM $Tabla_detalleVenta dv
        INNER JOIN $Tabla_producto p ON dv.$Tabla_ProductoID = p.$Tabla_ProductoID
        GROUP BY dv.$Tabla_ProductoID
        ORDER BY VecesVendido DESC
        LIMIT 5
        """.trimIndent(),
            // CORRECCIÓN: Antes usaba COUNT() que contaba filas de detalle, no unidades vendidas.
            // Y SUM(PrecioUnitario) no multiplicaba por la cantidad.
            // Ahora: VecesVendido = SUM(Cantidad) para contar unidades reales vendidas
            //        TotalGenerado = SUM(Precio * Cantidad) para el total correcto
            null
        )

        if (consulta.moveToFirst()) {
            do {
                val nombre = consulta.getString(consulta.getColumnIndexOrThrow(Tabla_NombreProducto))
                val veces = consulta.getInt(consulta.getColumnIndexOrThrow("VecesVendido"))
                val total = consulta.getFloat(consulta.getColumnIndexOrThrow("TotalGenerado"))
                lista.add(ProductoMasVendido(nombre, veces, total))
            } while (consulta.moveToNext())
        }
        consulta.close()
        return lista
    }

    // Ventas agrupadas por fecha (para ver dias con mas ventas)
    fun obtenerVentasAgrupadasPorFecha(): ArrayList<VentaPorFecha> {
        val lista = ArrayList<VentaPorFecha>()
        val db = this.readableDatabase

        val consulta = db.rawQuery(
            """
        SELECT $Tabla_FechaVenta, 
               SUM($Tabla_TotalVenta) as TotalDia, 
               COUNT($Tabla_VentaID) as CantidadVentas
        FROM $Tabla_venta
        GROUP BY $Tabla_FechaVenta
        ORDER BY $Tabla_FechaVenta DESC
        """.trimIndent(),
            null
        )

        if (consulta.moveToFirst()) {
            do {
                val fecha = consulta.getString(consulta.getColumnIndexOrThrow(Tabla_FechaVenta))
                val total = consulta.getFloat(consulta.getColumnIndexOrThrow("TotalDia"))
                val cantidad = consulta.getInt(consulta.getColumnIndexOrThrow("CantidadVentas"))
                lista.add(VentaPorFecha(fecha, total, cantidad))
            } while (consulta.moveToNext())
        }
        consulta.close()
        return lista
    }

    /////////////////// CRUD CATEGORIAS ////////////////

    // NUEVO: Modelo de datos para una categoria
    data class Categoria(
        val idCategoria: Int,
        val nombreCategoria: String
    )

    // NUEVO: Listar todas las categorias guardadas en la base de datos
    // Esta funcion la usan los Spinners de AgregarProducto y Productos_detalles
    fun listarCategorias(): ArrayList<Categoria> {
        val lista = ArrayList<Categoria>()
        val db = this.readableDatabase

        val consulta = db.rawQuery(
            "SELECT $Tabla_CategoriaID, $Tabla_NombreCategoria FROM $Tabla_categoria ORDER BY $Tabla_NombreCategoria ASC",
            null
        )

        if (consulta.moveToFirst()) {
            do {
                val id = consulta.getInt(consulta.getColumnIndexOrThrow(Tabla_CategoriaID))
                val nombre = consulta.getString(consulta.getColumnIndexOrThrow(Tabla_NombreCategoria))
                lista.add(Categoria(id, nombre))
            } while (consulta.moveToNext())
        }
        consulta.close()
        return lista
    }

    // NUEVO: Devuelve solo los nombres de las categorias, para cargar directamente en el Spinner
    fun listarNombresCategorias(): ArrayList<String> {
        val nombres = ArrayList<String>()
        for (cat in listarCategorias()) {
            nombres.add(cat.nombreCategoria)
        }
        return nombres
    }

    // NUEVO: Insertar una categoria nueva
    fun insertar_categoria(nombreCategoria: String): Boolean {
        return try {
            writableDatabase.use { db ->
                val valores = ContentValues().apply {
                    put(Tabla_NombreCategoria, nombreCategoria)
                }
                db.insert(Tabla_categoria, null, valores)
            }
            true
        } catch (e: Exception) {
            // Si el nombre ya existe (UNIQUE), devuelve false en vez de crashear
            false
        }
    }

    // NUEVO: Editar el nombre de una categoria existente
    fun editar_categoria(idCategoria: Int, nuevoNombre: String): Boolean {
        return try {
            writableDatabase.use { db ->
                val valores = ContentValues().apply {
                    put(Tabla_NombreCategoria, nuevoNombre)
                }
                val whereClause = "$Tabla_CategoriaID = ?"
                val whereArgs = arrayOf(idCategoria.toString())
                db.update(Tabla_categoria, valores, whereClause, whereArgs)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // NUEVO: Eliminar una categoria
    fun eliminar_categoria(idCategoria: Int) {
        writableDatabase.use { db ->
            val whereClause = "$Tabla_CategoriaID = ?"
            val whereArgs = arrayOf(idCategoria.toString())
            db.delete(Tabla_categoria, whereClause, whereArgs)
        }
    }

    //Declarando constantes y variables para crear la base de datos y sus tablas
    companion object {
        //nombre y version de la base de datos
        private const val DATABASE_NAME = "MiBodega"
        // CORRECCIÓN: Se subio la version a 10 para que Android detecte el cambio
        // y vuelva a crear las tablas con todas las correcciones aplicadas
        private const val DATABASE_VERSION = 10

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

        // NUEVO: Tabla categoria
        const val Tabla_categoria = "categoria"
        const val Tabla_CategoriaID = "CategoriaID"
        const val Tabla_NombreCategoria = "NombreCategoria"

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