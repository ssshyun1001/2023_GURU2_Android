package com.example.guru2_dsjouju_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
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

        // loginID를 인텐트에서 추출
        loginID = intent.getStringExtra("LOGIN_ID") ?: ""

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
    }

    private fun initializeDatabase() {
        contactsDAO = ContactsDAO(this, loginID)
    }

    private fun initializePreferences() {
        sharedPreferences = getSharedPreferences("SirenPrefs", Context.MODE_PRIVATE)
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
                text = "ID: ${contact.id}, Phone: ${contact.phone}"
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }
            contactListLayout.addView(textView)
        }
    }

    private fun addContact() {
        val phone = editTextContact.text.toString().trim()
        if (phone.isNotEmpty()) {
            contactsDAO.insertContact(phone)
            editTextContact.text.clear()
            updateContactList()
        } else {
            Toast.makeText(this, "연락처를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteContact() {
        val phone = editTextContact.text.toString()
        if (phone.isNotEmpty()) {
            val rowsDeleted = contactsDAO.deleteContact(phone) // 삭제된 레코드 수를 반환 받음
            if (rowsDeleted > 0) {
                // 삭제가 성공한 경우
                updateContactList()
            } else {
                // 삭제된 레코드가 없는 경우, 즉 전화번호가 존재하지 않는 경우
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
            R.id.radio_siren2 -> R.raw.fire_trucks_siren
            R.id.radio_siren3 -> R.raw.ambulance_siren
            else -> return
        }

        // MediaPlayer를 사용하여 사이렌 소리 재생
        val mediaPlayer = MediaPlayer.create(this, sirenType)
        mediaPlayer.start()

        // 3초 후에 소리 멈추기
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.stop()
            mediaPlayer.release()
        }, 3000)

        Toast.makeText(this, "현재 사이렌 소리: $sirenText", Toast.LENGTH_SHORT).show()
    }

    private fun setupSpinner() {
        val frequencies = listOf("1분", "2분", "3분", "4분", "5분")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val savedSelection = sharedPreferences.getString("spinner_selection", "5분")
        savedSelection?.let {
            val position = adapter.getPosition(it)
            spinner.setSelection(position)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                saveSpinnerSelection(frequencies[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 것도 선택되지 않음
            }
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
        // 로그인 화면으로 이동
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()  // 현재 Settings 액티비티 종료
    }
}

