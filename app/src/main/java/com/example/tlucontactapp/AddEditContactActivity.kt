package com.example.tlucontactapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tlucontactapp.models.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddEditContactActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etDepartment: EditText
    private lateinit var etPosition: EditText
    private lateinit var etType: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private var contactId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_contact)

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        etDepartment = findViewById(R.id.etDepartment)
        etPosition = findViewById(R.id.etPosition)
        etType = findViewById(R.id.etType)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Nhận ID liên hệ (nếu đang sửa)
        contactId = intent.getStringExtra("CONTACT_ID")

        // Nếu có contactId, load dữ liệu
        if (contactId != null) {
            loadContactData(contactId!!)
        }

        btnSave.setOnClickListener {
            saveContact()
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun loadContactData(contactId: String) {
        db.collection("Contacts").document(contactId).get()
            .addOnSuccessListener { document ->
                val contact = document.toObject(Contact::class.java)
                contact?.let {
                    etName.setText(it.name)
                    etPhone.setText(it.phone)
                    etEmail.setText(it.email)
                    etAddress.setText(it.address)
                    etDepartment.setText(it.department)
                    etPosition.setText(it.position)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveContact() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val department = etDepartment.text.toString().trim()
        val position = etPosition.text.toString().trim()
        val type = etType.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và số điện thoại!", Toast.LENGTH_SHORT).show()
            return
        }

        if (contactId == null) {
            // 🔹 Tạo liên hệ mới
            val newContact = Contact("", name, email, phone, address, department, position, type)

            db.collection("contacts").add(newContact)
                .addOnSuccessListener { documentReference ->
                    db.collection("contacts").document(documentReference.id)
                        .update("id", documentReference.id)  // 🔹 Cập nhật `id`
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // 🔹 Cập nhật liên hệ hiện tại
            val updatedContact = Contact(contactId!!, name, email, phone, address, department, position, type)

            db.collection("Contacts").document(contactId!!)
                .set(updatedContact)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
