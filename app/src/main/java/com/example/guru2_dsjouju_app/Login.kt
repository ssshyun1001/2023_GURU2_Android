package com.example.guru2_dsjouju_app

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 버튼을 클릭하면 다이얼로그를 표시합니다.
        findViewById<Button>(R.id.buttonLogin).setOnClickListener {
            showCustomAlertDialog()
        }
    }

    private fun showCustomAlertDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.activity_login, null)
        val editText = dialogView.findViewById<EditText>(R.id.buttonLogin)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("커스텀 다이얼로그")
        builder.setView(dialogView)
        builder.setPositiveButton("확인") { dialog, _ ->
            // "확인" 버튼을 눌렀을 때의 동작을 정의합니다.
            val inputText = editText.text.toString()
            // 입력된 텍스트를 처리하는 로직을 여기에 작성합니다.
            dialog.dismiss()
        }
        builder.setNegativeButton("취소") { dialog, _ ->
            // "취소" 버튼을 눌렀을 때의 동작을 정의합니다.
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }
}
