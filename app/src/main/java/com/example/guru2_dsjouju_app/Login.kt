package com.example.guru2_dsjouju_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    private lateinit var loginID: EditText
    private lateinit var loginPW: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 자동 로그인 체크
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        if (prefs.contains("LOGIN_ID")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        loginID = findViewById(R.id.ID)
        loginPW = findViewById(R.id.PW)
        btnLogin = findViewById(R.id.buttonLogin)
        btnSignup = findViewById(R.id.buttonSignUp)
        userDAO = UserDAO(this)

        btnLogin.setOnClickListener {
            val id = loginID.text.toString().trim()
            val password = loginPW.text.toString().trim()

            if (userDAO.checkUser(id, password)) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                // 로그인 ID를 SharedPreferences에 저장
                saveLoginID(id)

                // LOGIN_ID를 MainActivity로 전달
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("LOGIN_ID", id)
                startActivity(intent)
                finish()  // 현재 Login 액티비티 종료

            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        btnSignup.setOnClickListener { showSignupDialog() }
    }

    private fun saveLoginID(loginID: String) {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("LOGIN_ID", loginID).apply()
    }

    private fun showSignupDialog() {
        val builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.dialog_signup, null)
        builder.setView(view)

        val etSignupId = view.findViewById<EditText>(R.id.signupID)
        val btnCheckId = view.findViewById<Button>(R.id.checkID)
        val etSignupPassword = view.findViewById<EditText>(R.id.signupPW)
        val etSignupPhone = view.findViewById<EditText>(R.id.textPhone)
        val btnSignupSubmit = view.findViewById<Button>(R.id.signUp)
        val backButton = view.findViewById<Button>(R.id.signup_back_btn)

        var isIdChecked = false  // 중복 확인 여부를 저장하는 변수

        // 아이디 입력 필드에 TextWatcher 추가
        etSignupId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val idLength = s?.length ?: 0
                if (idLength < 8) {
                    etSignupId.error = "자릿수를 만족하는 아이디를 작성하시오."
                }
                isIdChecked = false  // 아이디를 수정하면 다시 중복 확인이 필요함
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 12) {
                    s.delete(12, s.length)
                }
            }
        })

        // 비밀번호 입력 필드에 TextWatcher 추가
        etSignupPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pwLength = s?.length ?: 0
                if (pwLength < 8) {
                    etSignupPassword.error = "비밀번호는 8자리 이상이어야 합니다."
                } else if (pwLength > 12) {
                    etSignupPassword.error = "비밀번호는 최대 12자리까지 가능합니다."
                } else {
                    etSignupPassword.error = null // 조건을 만족하면 에러 메시지를 제거
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 12) {
                    s.delete(12, s.length)
                }
            }
        })

        // 전화번호 형식 검사
        etSignupPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val phoneRegex = Regex("^\\d{11}\$")
                val phoneNumber = s?.toString() ?: ""
                if (!phoneRegex.matches(phoneNumber)) {
                    etSignupPhone.error = "전화번호는 11자리 숫자로만 이루어져야 합니다."
                } else {
                    etSignupPhone.error = null // 형식이 올바르면 에러 메시지 제거
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnCheckId.setOnClickListener {
            val id = etSignupId.text.toString().trim()
            if (userDAO.checkIfIdExists(id)) {
                Toast.makeText(this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
                isIdChecked = false  // 중복 확인 실패
            } else {
                Toast.makeText(this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
                isIdChecked = true  // 중복 확인 성공
            }
        }

        val dialog = builder.create()

        backButton.setOnClickListener {
            dialog.dismiss()
        }

        btnSignupSubmit.setOnClickListener {
            val id = etSignupId.text.toString().trim()
            val password = etSignupPassword.text.toString().trim()
            val phone = etSignupPhone.text.toString().trim()
            if (id.isNotEmpty() && password.isNotEmpty() && phone.isNotEmpty()) {
                if (!isIdChecked) {
                    Toast.makeText(this, "아이디 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show()
                } else if (id.length in 8..12 && password.length in 8..12) {
                    // 전화번호 형식 검사
                    val phoneRegex = Regex("^\\d{11}\$")
                    if (!phoneRegex.matches(phone)) {
                        Toast.makeText(this, "전화번호는 01012345678 형식으로 11자리 숫자여야 합니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        if (userDAO.insertUser(id, password, phone)) {
                            Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "아이디와 비밀번호는 8자리 이상 12자리 이하로 입력해 주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "모든 필드를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
