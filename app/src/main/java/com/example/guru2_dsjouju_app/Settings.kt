package com.example.guru2_dsjouju_app

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class Settings : AppCompatActivity() {

    lateinit var setoptionmenubtn: Button

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var addContactButton: Button
    private lateinit var contactListLayout: LinearLayout

    private lateinit var radioGroup: RadioGroup
    private lateinit var applyButton: Button
    private lateinit var testButton: Button

    private lateinit var spinner: Spinner

    private lateinit var editTextSosMessage: EditText
    private lateinit var sosMessageTextView: TextView
    private lateinit var sosInitButton: Button
    private lateinit var sosEditButton: Button
    private lateinit var sosSaveButton: Button

    private var isEditing = false
    private var originalSosMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // SharedPreferences(:설정 상태 저장) 초기화
        sharedPreferences = getSharedPreferences("SirenPrefs", Context.MODE_PRIVATE)

        // UI 요소 초기화
        setoptionmenubtn = findViewById(R.id.set_option_menu_btn)

        addContactButton = findViewById(R.id.add_contact_button)
        contactListLayout = findViewById(R.id.contact_list)

        radioGroup = findViewById(R.id.radio_group_siren)
        applyButton = findViewById(R.id.siren_apply_button)
        testButton = findViewById(R.id.siren_act_button)

        spinner = findViewById(R.id.spinner_location_update_frequency)

        editTextSosMessage = findViewById(R.id.edit_text_sosmessage)
        sosMessageTextView = findViewById(R.id.sos_message_text_view)
        sosInitButton = findViewById(R.id.sos_init_button)
        sosEditButton = findViewById(R.id.sos_edit_button)
        sosSaveButton = findViewById(R.id.sos_save_button)

        // 저장된 상태 로드
        loadContactNumbers()
        loadRadioButtonState()
        loadSosMessage()

        // Spinner 설정
        setupSpinner()

        // 버튼 관련 listener 설정
        setoptionmenubtn.setOnClickListener { showPopupMenu(it) }
        addContactButton.setOnClickListener { showAddContactDialog() }
        applyButton.setOnClickListener { saveRadioButtonState() }
        testButton.setOnClickListener { testSelectedSiren() }

        sosInitButton.setOnClickListener { resetSosMessage() }
        sosEditButton.setOnClickListener { toggleEditMode() }
        sosSaveButton.setOnClickListener { saveSosMessage() }
    }

    // 팝업 메뉴
    private fun showPopupMenu(view: android.view.View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_settings, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    // 홈 항목 클릭 시 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_tutorial -> {
                    // 사용법 항목 클릭 시 Tutorial로 이동
                    val intent = Intent(this, Tutorial::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    // 연락처 추가 팝업 표시
    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.setting_add_contact, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
            .setTitle("연락처 추가")
            .setPositiveButton("추가") { dialog, _ ->
                val editText: EditText = dialogView.findViewById(R.id.edit_text_contact)
                val contact = editText.text.toString()
                addContactToList(contact)
                saveContactNumber()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    // 연락처 추가 및 리니어 레이어 표시 제어
    private fun addContactToList(contact: String) {
        if (contactListLayout.visibility == View.GONE) {
            contactListLayout.visibility = View.VISIBLE
        }

        val contactItemView = LayoutInflater.from(this).inflate(R.layout.setting_contact_item, contactListLayout, false)
        val contactTextView: TextView = contactItemView.findViewById(R.id.contact_text)
        val removeButton: Button = contactItemView.findViewById(R.id.remove_contact_button)

        contactTextView.text = contact

        removeButton.setOnClickListener {
            contactListLayout.removeView(contactItemView)
            if (contactListLayout.childCount == 0) {
                contactListLayout.visibility = View.GONE
            }
            saveContactNumber()  // 연락처 삭제 후에도 저장
        }

        contactListLayout.addView(contactItemView)
    }

    // 연락처 상태 SharedPreferences 통해 저장
    private fun saveContactNumber() {
        val contactList = mutableListOf<String>()

        for (i in 0 until contactListLayout.childCount) {
            val contactItemView = contactListLayout.getChildAt(i)
            val contactTextView: TextView = contactItemView.findViewById(R.id.contact_text)
            contactList.add(contactTextView.text.toString())
        }

        val sharedPreferences = getSharedPreferences("ContactPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("contact_list", contactList.toSet())
        editor.apply()
    }

    // SharedPreferences 통해 저장한 연락처 리스트 로드
    private fun loadContactNumbers() {
        val sharedPreferences = getSharedPreferences("ContactPrefs", Context.MODE_PRIVATE)
        val savedContacts = sharedPreferences.getStringSet("contact_list", emptySet()) ?: emptySet()

        savedContacts.forEach { contact ->
            addContactToList(contact)
        }
    }

    // 사이렌 상태 SharedPreferences 통해 저장
    private fun saveRadioButtonState() {
        val selectedId = radioGroup.checkedRadioButtonId
        val editor = sharedPreferences.edit()
        editor.putInt("selected_siren", selectedId)
        editor.apply()
    }

    // SharedPreferences 통해 저장한 라디오 버튼 ID 로드
    private fun loadRadioButtonState() {
        val savedId = sharedPreferences.getInt("selected_siren", R.id.radio_siren1)
        if (savedId != -1) {
            radioGroup.check(savedId)
        }
    }

    // 테스트 시에 현재 출력 되는 사이렌 소리를 토스트 메세지로 표기
    private fun testSelectedSiren() {
        val selectedId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        val sirenText = selectedRadioButton?.text.toString()

        Toast.makeText(this, "현재 사이렌 소리: $sirenText", Toast.LENGTH_SHORT).show()
    }

    // 스피너 항목 설정 및 변경값 저장 실행
    private fun setupSpinner() {
        val frequencies = listOf("1분", "2분", "3분", "4분", "5분")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Spinner 기본값 설정
        val savedSelection = sharedPreferences.getString("spinner_selection", "5분")
        savedSelection?.let {
            val position = adapter.getPosition(it)
            spinner.setSelection(position)
        }

        // Spinner 선택이 변경될 때 저장
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                saveSpinnerSelection(frequencies[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 것도 선택 되지 않음
            }
        }
    }

    // 위치 기록 주기 상태 SharedPreferences 통해 저장
    private fun saveSpinnerSelection(selection: String) {
        val editor = sharedPreferences.edit()
        editor.putString("spinner_selection", selection)
        editor.apply()
    }

    // SOS 메세지 초기화
    private fun resetSosMessage() {
        val defaultMessage = "SOS 메시지 : 지금 사용자가 위험한 상황이에요. 도와주세요!"
        sosMessageTextView.text = defaultMessage
        editTextSosMessage.setText(defaultMessage)

        val editor = sharedPreferences.edit()
        editor.putString("sos_message", defaultMessage)
        editor.apply()
    }

    private fun toggleEditMode() {
        if (isEditing) {
            // 취소 버튼을 눌렀을 때
            sosMessageTextView.visibility = View.VISIBLE
            editTextSosMessage.visibility = View.GONE
            sosSaveButton.visibility = View.GONE
            sosEditButton.text = "수정"

            // 수정 전 메세지로 돌아가기
            editTextSosMessage.setText(originalSosMessage)
        } else {
            // 수정 버튼을 눌렀을 때
            originalSosMessage = sosMessageTextView.text.toString()
            sosMessageTextView.visibility = View.GONE
            editTextSosMessage.visibility = View.VISIBLE
            sosSaveButton.visibility = View.VISIBLE
            sosEditButton.text = "취소"
        }
        isEditing = !isEditing
    }

    // 사용자 입력 SOS 메세지 SharedPreferences 통해 저장
    private fun saveSosMessage() {
        val sosMessage = editTextSosMessage.text.toString()

        val editor = sharedPreferences.edit()
        editor.putString("sos_message", sosMessage)
        editor.apply()

        Toast.makeText(this, "SOS 메시지가 저장되었습니다.", Toast.LENGTH_SHORT).show()

        // 변경 사항 저장 후 텍스트 뷰로 돌아가기
        sosMessageTextView.text = sosMessage

        // 저장 후 originalSosMessage 업데이트
        originalSosMessage = sosMessage

        toggleEditMode()
    }

    // SharedPreferences 통해 저장한 메세지 내용 로드
    private fun loadSosMessage() {
        val savedMessage = sharedPreferences.getString("sos_message", "SOS 메시지 : 지금 사용자가 위험한 상황이에요. 도와주세요!")
        sosMessageTextView.text = savedMessage
        editTextSosMessage.setText(savedMessage)
    }
}