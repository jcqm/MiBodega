package com.sise.mibodega.data;

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        //Para crear la tabla usuario
        val crearUsuario = ("CREATE TABLE " + Tabla_usuario + " ("
                + Tabla_UsuarioID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Tabla_Usuario_Nombres + " TEXT NOT NULL," +
                Tabla_Usuarios_Apellidos + " TEXT NOT NULL" + ")")
        db.execSQL(crearUsuario)

        //Crear tabla tienda
        val crearTienda = ("CREATE TABLE " + Tabla_tienda + " ("
                + Tabla_TiendaID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Tabla_UsuarioID + " INTEGER NOT NULL, "
                + Tabla_Nombre_Tienda + " TEXT NOT NULL, "
                + Tabla_Direccion_Tienda + " TEXT NOT NULL" + ")")
        db.execSQL(crearTienda)

    }

    // Se llama cuando la base de datos tiene que ser usada
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $Tabla_usuario")
        db.execSQL("DROP TABLE IF EXISTS $Tabla_tienda")

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

    companion object {
        //nombre y version de la base de datos
        private const val DATABASE_NAME = "MiBodega"
        private const val DATABASE_VERSION = 1

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

    }
}
