package com.example.guru2_dsjouju_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class Settings : AppCompatActivity() {

    private lateinit var setOptionMenuBtn: ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var addContactButton: Button
    private lateinit var deleteContactButton: Button
    private lateinit var contactListLayout: LinearLayout
    private lateinit var editTextContact: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var applyButton: Button
    private lateinit var testButton: Button
    private lateinit var spinner: Spinner
    private lateinit var editTextSosMessage: EditText
    private lateinit var SOSListLayout : LinearLayout
    private lateinit var sosMessageTextView: TextView
    private lateinit var sosResetButton: Button
    private lateinit var sosSaveButton: Button
    private lateinit var sosResearchButton: Button // 추가된 버튼
    private lateinit var logoutButton: Button
    private lateinit var loginID: String
    private lateinit var contactsDAO: ContactsDAO
    private lateinit var messagesDAO: MessagesDAO
    private var isEditing = false
    private var originalSosMessage: String = "SOS 메시지: 지금 사용자가 위험한 상황이에요. 도와주세요!"



    private fun getLoginID(): String? {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return prefs.getString("LOGIN_ID", null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Intent에서 loginID를 가져옴
        loginID = intent.getStringExtra("LOGIN_ID") ?: ""

        // loginID가 비어있으면 SharedPreferences에서 가져옴
        if (loginID.isEmpty()) {
            loginID = getLoginID() ?: ""
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
        SOSListLayout = findViewById(R.id.sos_message_list)
        sosMessageTextView = findViewById(R.id.edit_text_sosmessage) // 올바른 ID 확인 필요
        sosResetButton = findViewById(R.id.reset_sos_message_button)
        sosSaveButton = findViewById(R.id.save_sos_message_button)
        sosResearchButton = findViewById(R.id.research_sos_message_button) // 올바른 ID 확인 필요
        logoutButton = findViewById(R.id.logout_button)
    }

    private fun initializeDatabase() {
        contactsDAO = ContactsDAO(this, loginID)
        messagesDAO = MessagesDAO(this, loginID)
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
        sosSaveButton.setOnClickListener { saveSosMessage() }
        sosResetButton.setOnClickListener { resetSosMessage() }
        sosResearchButton.setOnClickListener { loadSosMessage() } // 추가된 부분
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
        val sirenType = when (selectedId) {
            R.id.radio_siren1 -> R.raw.police_siren
            R.id.radio_siren2 -> R.raw.fire_trucks_siren
            R.id.radio_siren3 -> R.raw.ambulance_siren
            else -> return
        }

        val mediaPlayer = MediaPlayer.create(this, sirenType)
        mediaPlayer.start()

        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.stop()
            mediaPlayer.release()
        }, 3000)

        val sirenText = findViewById<RadioButton>(selectedId)?.text.toString()
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

    private fun saveSosMessage() {
        val newMessage = editTextSosMessage.text.toString().trim()
        if (newMessage.isNotEmpty()) {
            messagesDAO.updateMessage(newMessage)
            sosMessageTextView.text = newMessage
            Toast.makeText(this, "SOS 메시지가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            originalSosMessage = newMessage
        } else {
            Toast.makeText(this, "메시지가 입력되지 않았습니다. 메시지를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetSosMessage() {
        messagesDAO.resetMessages()
        loadSosMessage()
    }

    private fun loadSosMessage() {
        val messages = messagesDAO.getMessagesById()
        val latestMessage = messages.lastOrNull()?.message ?: "SOS 메시지: 지금 사용자가 위험한 상황이에요. 도와주세요!"
        editTextSosMessage.setText(latestMessage)
        sosMessageTextView.text = latestMessage
        originalSosMessage = latestMessage
    }

    private fun saveMessage(message: String) {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("SAVED_MESSAGE", message).apply()
    }



    private fun logout() {

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        prefs.edit().remove("LOGIN_ID").apply()

        startActivity(Intent(this, Login::class.java))
        finish()
    }
}


