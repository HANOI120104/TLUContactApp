package com.example.tlucontactapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tlucontactapp.models.Contact
import com.google.firebase.firestore.FirebaseFirestore

class ContactDetailActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private var contactId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        db = FirebaseFirestore.getInstance()

        val tvName: TextView = findViewById(R.id.tvName)
        val tvPhone: TextView = findViewById(R.id.tvPhone)
        val tvEmail: TextView = findViewById(R.id.tvEmail)
        val tvAddress: TextView = findViewById(R.id.tvAddress)
        val tvDepartment: TextView = findViewById(R.id.tvDepartment)
        val tvPosition: TextView = findViewById(R.id.tvPosition)
        val btnCall: Button = findViewById(R.id.btnCall)
        val btnBack: Button = findViewById(R.id.btnBack)

        // Nhận ID liên hệ từ Intent
        contactId = intent.getStringExtra("CONTACT_ID")

        if (contactId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID liên hệ", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 🔹 Lấy dữ liệu từ Firestore
        db.collection("Contacts").document(contactId!!).get()
            .addOnSuccessListener { document ->
                val contact = document.toObject(Contact::class.java)
                if (contact != null) {
                    tvName.text = contact.name
                    tvPhone.text = contact.phone
                    tvEmail.text = contact.email
                    tvAddress.text = contact.address
                    tvDepartment.text = contact.department
                    tvPosition.text = contact.position
                } else {
                    Toast.makeText(this, "Không tìm thấy liên hệ!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show()
                finish()
            }

        // 🔹 Xử lý nút gọi điện
        btnCall.setOnClickListener {
            val phone = tvPhone.text.toString()
            if (phone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔹 Xử lý nút quay lại
        btnBack.setOnClickListener {
            finish()
        }
    }
}
