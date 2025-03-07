package com.fortune.kotlinappcalculadora.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseSqlite(context: Context, databaseName: String, factory: CursorFactory?, version: Int) : SQLiteOpenHelper(context, databaseName, factory, version) {
    companion object {
        // Table products
        val user = "CREATE TABLE user (id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR(255), password VARCHAR(255) NOT NULL)"
        val drop_user = "DROP TABLE IF EXISTS user"

        val mountain = "CREATE TABLE mountain (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255), height DOUBLE NOT NULL, user VARCHAR(255) NOT NULL)"
        val drop_mountain = "DROP TABLE IF EXISTS mountain"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(user)
        db.execSQL(mountain)

        injectDefaultUsers(db)
    }

    private fun injectDefaultUsers(db: SQLiteDatabase) {
        val uMe = ContentValues().apply {
            put("username", "iyan")
            put("password", "iyan")
        }

        val uAdmin = ContentValues().apply {
            put("username", "admin")
            put("password", "admin")
        }

        val uGuest = ContentValues().apply {
            put("username", "invitado")
            put("password", "guest")
        }

        db.insert("user", null, uMe)
        db.insert("user", null, uAdmin)
        db.insert("user", null, uGuest)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(drop_user)
        db.execSQL(drop_mountain)
        this.onCreate(db)
    }
}