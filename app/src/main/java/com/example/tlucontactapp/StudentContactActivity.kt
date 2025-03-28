package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.adapters.ContactAdapter
import com.example.tlucontactapp.models.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentContactActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private val db = FirebaseFirestore.getInstance()
    private val contactList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_contact)

        recyclerView = findViewById(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Truyền callback khi click vào item
        contactAdapter = ContactAdapter(contactList) { selectedContact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra("CONTACT_ID", selectedContact.id) // Truyền ID để xem chi tiết
            startActivity(intent)
        }
        recyclerView.adapter = contactAdapter

        loadStudentContacts()
    }

    private fun loadStudentContacts() {
        db.collection("Contacts").whereEqualTo("type", "SV")
            .get()
            .addOnSuccessListener { result ->
                val newList = mutableListOf<Contact>()
                for (document in result) {
                    val contact = document.toObject(Contact::class.java)
                    Log.d("FirestoreData", "Contact: ${document.id} - ${contact.name} - ${contact.phone}")
                    newList.add(contact)
                }
                contactAdapter.updateList(newList)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Lỗi khi lấy dữ liệu", e)
            }
    }

}
