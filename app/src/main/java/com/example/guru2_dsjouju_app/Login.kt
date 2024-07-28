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

        // 자동 로그인 체크
        checkLogin()

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // 로그인 검증 로직
            if (validateLogin(username, password)) {
                // 로그인 성공 시 SharedPreferences에 저장
                editor.putString("username", username)
                editor.putString("password", password)
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                // MainScreen Activity로 이동
                val intent = Intent(this, MainScreen::class.java)
                startActivity(intent)
                finish() // 로그인 Activity를 종료합니다.
            } else {
                // 로그인 실패 시 처리 로직 추가 (예: 에러 메시지 표시)
            }
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // 여기에 실제 로그인 검증 로직을 추가하세요.
        // 예를 들어, 사용자 이름과 비밀번호가 맞는지 확인하는 작업을 수행합니다.
        // 임시로 모든 입력을 통과시키도록 설정합니다.
        return username.isNotEmpty() && password.isNotEmpty()
    }

    private fun checkLogin() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
            finish() // 로그인 Activity를 종료합니다.
        }
    }
}


