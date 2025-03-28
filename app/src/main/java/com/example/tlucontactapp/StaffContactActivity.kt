package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.adapters.ContactAdapter
import com.example.tlucontactapp.models.Contact
import com.google.firebase.firestore.FirebaseFirestore

class StaffContactActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private val db = FirebaseFirestore.getInstance()
    private val contactList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_contact)

        recyclerView = findViewById(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Khởi tạo Adapter trước khi tải dữ liệu
        contactAdapter = ContactAdapter(contactList) { selectedContact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra("CONTACT_ID", selectedContact.id) // Truyền ID để xem chi tiết
            startActivity(intent)
        }
        recyclerView.adapter = contactAdapter

        loadStaffContacts()
    }

    private fun loadStaffContacts() {
        db.collection("Contacts").whereEqualTo("type", "CBGV")
            .get()
            .addOnSuccessListener { result ->
                val newList = mutableListOf<Contact>()
                for (document in result) {
                    val contact = document.toObject(Contact::class.java).copy(id = document.id)
                    newList.add(contact)
                }
                contactAdapter.updateList(newList) // Cập nhật danh sách mới
            }
            .addOnFailureListener {
                // Xử lý lỗi
            }
    }
}
