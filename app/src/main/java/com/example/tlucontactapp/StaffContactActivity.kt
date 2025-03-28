package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.adapters.StaffAdapter
import com.example.tlucontactapp.models.Staff
import com.google.firebase.firestore.FirebaseFirestore

class StaffContactActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var staffAdapter: StaffAdapter
    private val db = FirebaseFirestore.getInstance()
    private val staffList = mutableListOf<Staff>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_contact)

        recyclerView = findViewById(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Khởi tạo Adapter
        staffAdapter = StaffAdapter(staffList) { selectedStaff ->
            val intent = Intent(this, StaffDetailActivity::class.java)
            intent.putExtra("STAFF_ID", selectedStaff.staffId) // Truyền ID để xem chi tiết
            startActivity(intent)
        }
        recyclerView.adapter = staffAdapter

        loadStaffContacts()
    }

    private fun loadStaffContacts() {
        db.collection("staffs") // Lấy dữ liệu từ "staffs"
            .get()
            .addOnSuccessListener { result ->
                val newList = mutableListOf<Staff>()
                for (document in result) {
                    val staff = document.toObject(Staff::class.java).copy(staffId = document.id)
                    newList.add(staff)
                }
                staffAdapter.updateList(newList) // Cập nhật danh sách mới
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Lỗi khi lấy dữ liệu", e)
            }
    }
}
