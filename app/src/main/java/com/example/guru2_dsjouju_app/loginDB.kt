package com.example.guru2_dsjouju_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class loginDB(
    context: Context?,
    name: String = "userDB",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        // signup 테이블 생성
        db!!.execSQL(
            "CREATE TABLE users (" +
                    "id TEXT PRIMARY KEY, " +
                    "password TEXT, " +
                    "phoneNum TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 테이블 업그레이드
        db!!.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun checkUser(id: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            "users",
            arrayOf("id"),
            "id = ? AND password = ?",
            arrayOf(id, password),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun checkIfIdExists(id: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            "users",
            arrayOf("id"),
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun insertUser(id: String, password: String, phoneNum: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("password", password)
            put("phoneNum", phoneNum)
        }
        val result = db.insert("users", null, values)
        db.close()
        return result != -1L
    }
}

class UserDAO(context: Context) {

    private val dbManager = loginDB(context, "userDB", null, 1)

    // 사용자 정보 삽입 메서드
    fun insertUser(id: String, password: String, phoneNum: String): Boolean {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("password", password)
            put("phoneNum", phoneNum)
        }
        val result = db.insert("users", null, values)
        db.close()
        return result != -1L
    }

    // 아이디 중복 확인 메서드
    fun checkIfIdExists(id: String): Boolean {
        val db = dbManager.readableDatabase
        val cursor: Cursor = db.query(
            "users",
            arrayOf("id"),
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // 사용자 정보 확인 메서드 (로그인 시 필요)
    fun checkUser(id: String, password: String): Boolean {
        val db = dbManager.readableDatabase
        val cursor: Cursor = db.query(
            "users",
            arrayOf("id"),
            "id = ? AND password = ?",
            arrayOf(id, password),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
}
