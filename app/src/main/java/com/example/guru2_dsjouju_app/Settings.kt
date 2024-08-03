package com.example.guru2_dsjouju_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class Settings : AppCompatActivity() {

    lateinit var setOptionMenuBtn: ImageButton

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var addContactButton: Button
    private lateinit var deleteContactButton: Button
    private lateinit var contactListLayout: LinearLayout
    private lateinit var editTextContact: EditText

    private lateinit var contactsDAO: ContactsDAO

    private lateinit var radioGroup: RadioGroup
    private lateinit var applyButton: Button
    private lateinit var testButton: Button

    private lateinit var spinner: Spinner

    private lateinit var editTextSosMessage: EditText
    private lateinit var sosMessageTextView: TextView
    private lateinit var sosInitButton: Button
    private lateinit var sosEditButton: Button
    private lateinit var sosSaveButton: Button

    private lateinit var logoutButton: Button

    private var isEditing = false
    private var originalSosMessage: String = ""

    private lateinit var loginID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)

        loginID = intent.getStringExtra("LOGIN_ID") ?: ""

        // Check if loginID is empty or null
        if (loginID.isNotEmpty()) {
            sharedPreferences.edit()
                .putString("loginID_save", loginID)
                .apply()
        } else {
            loginID = sharedPreferences.getString("loginID_save", "") ?: ""
        }

        initializeUI()
        initializeDatabase()
        initializePreferences()
        setupUIListeners()
        loadData()
    }

    private fun initializeUI() {
        setOptionMenuBtn = findViewById(R.id.set_option_menu_btn)

        addContactButton = findViewById(R.id.add_contact_button)
        deleteContactButton = findViewById(R.id.delete_contact_button)
        contactListLayout = findViewById(R.id.contact_list)
        editTextContact = findViewById(R.id.edit_text_contact)

        radioGroup = findViewById(R.id.radio_group_siren)
        applyButton = findViewById(R.id.siren_apply_button)
        testButton = findViewById(R.id.siren_test_button)

        spinner = findViewById(R.id.spinner_location_update_frequency)

        editTextSosMessage = findViewById(R.id.edit_text_sosmessage)
        sosMessageTextView = findViewById(R.id.sos_message_text_view)
        sosInitButton = findViewById(R.id.sos_init_button)
        sosEditButton = findViewById(R.id.sos_edit_button)
        sosSaveButton = findViewById(R.id.sos_save_button)

        logoutButton = findViewById(R.id.logout_button)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFrequency = parent.getItemAtPosition(position).toString()
                saveSpinnerSelection(selectedFrequency)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        editTextSosMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val byteLength = s?.toString()?.toByteArray(Charsets.UTF_8)?.size ?: 0
                if (byteLength > 125) {
                    // 바이트 길이가 125자를 초과하면 현재 바이트 수와 최대 바이트 수를 포함한 경고 메시지 표시
                    editTextSosMessage.error = "메시지 길이 제한 : ${byteLength}/140 byte"
                } else {
                    editTextSosMessage.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 바이트 길이가 140자를 초과할 경우 초과된 부분을 삭제
                if (s != null && s.toString().toByteArray(Charsets.UTF_8).size >= 140) {
                    val maxLength = 140
                    for (i in 0 until s.length) {
                        val byteLength = s.subSequence(0, i + 1).toString().toByteArray(Charsets.UTF_8).size
                        if (byteLength > maxLength) {
                            s.delete(i, s.length)
                            break
                        }
                    }
                }
            }
        })
    }

    private fun initializeDatabase() {
        contactsDAO = ContactsDAO(this, loginID)
    }

    private fun initializePreferences() {
        sharedPreferences = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
    }

    private fun setupUIListeners() {
        setOptionMenuBtn.setOnClickListener { showPopupMenu(it) }
        applyButton.setOnClickListener { saveRadioButtonState() }
        testButton.setOnClickListener { testSelectedSiren() }
        addContactButton.setOnClickListener { addContact() }
        deleteContactButton.setOnClickListener { deleteContact() }
        sosInitButton.setOnClickListener { resetSosMessage() }
        sosEditButton.setOnClickListener { toggleEditMode() }
        sosSaveButton.setOnClickListener { saveSosMessage() }
        logoutButton.setOnClickListener { logout() }
    }

    private fun loadData() {
        updateContactList()
        loadRadioButtonState()
        loadSosMessage()
        setupSpinner()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_settings, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.action_tutorial -> {
                    startActivity(Intent(this, Tutorial::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun updateContactList() {
        contactListLayout.removeAllViews()
        val contacts = contactsDAO.getContactsById()
        contacts.forEach { contact ->
            val textView = TextView(this).apply {
                "ID: ${contact.id}, Phone: ${contact.phone}".also { text = it }
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }
            contactListLayout.addView(textView)
        }
    }

    private fun addContact() {
        val phone = editTextContact.text.toString().trim()

        // 전화번호가 숫자로만 이루어졌는지, 그리고 11자리인지 확인
        val phoneRegex = Regex("^\\d{11}\$")  // 11자리 숫자만 허용
        if (phone.isNotEmpty()) {
            if (!phoneRegex.matches(phone)) {
                Toast.makeText(this, "전화번호는 11자리 숫자로만 이루어져야 합니다.", Toast.LENGTH_SHORT).show()
            } else {
                val exists = contactsDAO.getContactByPhone(phone)
                if (exists) {
                    Toast.makeText(this, "이미 존재하는 번호입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    contactsDAO.insertContact(phone)
                    editTextContact.text.clear()
                    updateContactList()
                }
            }
        } else {
            Toast.makeText(this, "연락처를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteContact() {
        val phone = editTextContact.text.toString()
        if (phone.isNotEmpty()) {
            val rowsDeleted = contactsDAO.deleteContact(phone)
            if (rowsDeleted > 0) {
                updateContactList()
            } else {
                Toast.makeText(this, "존재하지 않는 전화번호입니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRadioButtonState() {
        val selectedId = radioGroup.checkedRadioButtonId
        sharedPreferences.edit()
            .putInt("selected_siren", selectedId)
            .apply()
    }

    private fun loadRadioButtonState() {
        val savedId = sharedPreferences.getInt("selected_siren", R.id.radio_siren1)
        if (savedId != -1) {
            radioGroup.check(savedId)
        }
    }

    private fun testSelectedSiren() {
        val selectedId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        val sirenText = selectedRadioButton?.text.toString()

        val sirenType = when (selectedId) {
            R.id.radio_siren1 -> R.raw.police_siren
            R.id.radio_siren2 -> R.raw.civil_defense_siren
            R.id.radio_siren3 -> R.raw.ambulance_siren
            else -> return
        }

        // MediaPlayer를 사용하여 사이렌 소리 재생
        val mediaPlayer = MediaPlayer.create(this, sirenType)
        try {
            mediaPlayer.start()
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer.stop()
                mediaPlayer.release()
            }, 3000)
        } finally {
            mediaPlayer.release()
        }

        Toast.makeText(this, "현재 사이렌 소리: $sirenText", Toast.LENGTH_SHORT).show()
    }

    private fun setupSpinner() {
        val frequencies = listOf("1분", "2분", "3분", "4분", "5분")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val savedSelection = sharedPreferences.getString("spinner_selection", "1분")
        savedSelection?.let {
            val position = adapter.getPosition(it)
            spinner.setSelection(position)
        }
    }

    private fun saveSpinnerSelection(selection: String) {
        sharedPreferences.edit()
            .putString("spinner_selection", selection)
            .apply()
    }

    private fun resetSosMessage() {
        val defaultMessage = "SOS 메시지 : 지금 사용자가 위험한 상황이에요. 도와주세요!"
        sosMessageTextView.text = defaultMessage
        editTextSosMessage.setText(defaultMessage)
        sharedPreferences.edit()
            .putString("sos_message", defaultMessage)
            .apply()
    }

    private fun toggleEditMode() {
        if (isEditing) {
            sosMessageTextView.visibility = View.VISIBLE
            editTextSosMessage.visibility = View.GONE
            sosSaveButton.visibility = View.GONE
            sosEditButton.text = "수정"
            editTextSosMessage.setText(originalSosMessage)
            editTextSosMessage.setText(sosMessageTextView.text.toString())
        } else {
            originalSosMessage = sosMessageTextView.text.toString()
            sosMessageTextView.visibility = View.GONE
            editTextSosMessage.visibility = View.VISIBLE
            sosSaveButton.visibility = View.VISIBLE
            sosEditButton.text = "취소"
        }
        isEditing = !isEditing
    }

    private fun saveSosMessage() {
        val newMessage = editTextSosMessage.text.toString()
        if (newMessage.isNotBlank()) {
            sosMessageTextView.text = newMessage
            originalSosMessage = newMessage
            sharedPreferences.edit()
                .putString("sos_message", newMessage)
                .apply()
            toggleEditMode()
        } else {
            Toast.makeText(this, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSosMessage() {
        val defaultMessage = "SOS 메시지 : 지금 사용자가 위험한 상황이에요. 도와주세요!"
        val savedMessage = sharedPreferences.getString("sos_message", defaultMessage)
        sosMessageTextView.text = savedMessage
        editTextSosMessage.setText(savedMessage)
    }

    private fun logout() {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        prefs.edit().remove("LOGIN_ID").apply()

        startActivity(Intent(this, Login::class.java))
        finish()
    }
}

