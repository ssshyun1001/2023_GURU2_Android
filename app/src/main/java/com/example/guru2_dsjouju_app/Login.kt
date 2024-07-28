package com.example.guru2_dsjouju_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val loginButton: Button = findViewById(R.id.buttonLogin)
        val usernameEditText: EditText = findViewById(R.id.ID)
        val passwordEditText: EditText = findViewById(R.id.PW)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // 로그인 검증 로직을 여기에 추가합니다. (예: 사용자 이름과 비밀번호 확인)
            if (validateLogin(username, password)) {
                editor.putString("username", username)
                editor.putString("password", password)
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                finish() // 로그인 Activity를 종료합니다.
            } else {
                // 로그인 실패 시 처리 로직 추가
            }
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // 여기에 실제 로그인 검증 로직을 추가하세요.
        return username.isNotEmpty() && password.isNotEmpty()
    }
}
