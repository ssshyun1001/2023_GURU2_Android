package com.example.guru2_dsjouju_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        // contacts 테이블 생성
        db!!.execSQL(
            "CREATE TABLE contacts (" +
                    "id TEXT, " +
                    "phone TEXT, " +
                    "PRIMARY KEY (id, phone))"
        )
        // messages 테이블 생성
        db.execSQL(
            "CREATE TABLE messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "message TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 테이블 업그레이드
        db!!.execSQL("DROP TABLE IF EXISTS contacts")
        db.execSQL("DROP TABLE IF EXISTS messages")
        onCreate(db)
    }
}


class ContactsDAO(context: Context) {

    private val dbManager = DBManager(context, "contactsDB", null, 1)

    // 연락처 추가 메서드
    fun insertContact(id: String, phone: String) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("phone", phone)
        }
        db.insert("contacts", null, values)
        db.close()
    }

    // 연락처 삭제 메서드
    fun deleteContact(id: String, phone: String) {
        val db = dbManager.writableDatabase
        db.delete("contacts", "id = ? AND phone = ?", arrayOf(id, phone))
        db.close()
    }

    // 연락처 수정 메서드
    fun updateContact(id: String, oldPhone: String, newPhone: String) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put("phone", newPhone)
        }
        db.update("contacts", values, "id = ? AND phone = ?", arrayOf(id, oldPhone))
        db.close()
    }

    // 모든 연락처 조회 메서드
    fun getAllContacts(): List<Contact> {
        val db = dbManager.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM contacts", null)
        val contacts = mutableListOf<Contact>()
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
            contacts.add(Contact(id, phone))
        }
        cursor.close()
        db.close()
        return contacts
    }

    // 특정 ID에 해당하는 연락처 조회 메서드
    fun getContactsById(id: String): List<Contact> {
        val db = dbManager.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM contacts WHERE id = ?", arrayOf(id))
        val contacts = mutableListOf<Contact>()
        while (cursor.moveToNext()) {
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
            contacts.add(Contact(id, phone))
        }
        cursor.close()
        db.close()
        return contacts
    }

    // 연락처 클래스
    data class Contact(val id: String, val phone: String)
}



class MessagesDAO(context: Context) {

    private val dbManager = DBManager(context, "messagesDB", null, 1)

    // 메시지 추가 메서드
    fun insertMessage(message: String) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put("message", message)
        }
        db.insert("messages", null, values)
        db.close()
    }

    // 메시지 삭제 메서드
    fun deleteMessage(id: Int) {
        val db = dbManager.writableDatabase
        db.delete("messages", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    // 메시지 수정 메서드
    fun updateMessage(id: Int, newMessage: String) {
        val db = dbManager.writableDatabase
        val values = ContentValues().apply {
            put("message", newMessage)
        }
        db.update("messages", values, "id = ?", arrayOf(id.toString()))
        db.close()
    }

    // 모든 메시지 조회 메서드
    fun getAllMessages(): List<Message> {
        val db = dbManager.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM messages", null)
        val messages = mutableListOf<Message>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val message = cursor.getString(cursor.getColumnIndexOrThrow("message"))
            messages.add(Message(id, message))
        }
        cursor.close()
        db.close()
        return messages
    }

    // 메시지 클래스
    data class Message(val id: Int, val message: String)
}