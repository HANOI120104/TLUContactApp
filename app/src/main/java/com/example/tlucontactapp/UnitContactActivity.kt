package com.example.tlucontactapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.adapters.UnitAdapter
import com.example.tlucontactapp.models.Unit  // Đúng package
import com.google.firebase.firestore.FirebaseFirestore

class UnitContactActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var unitAdapter: UnitAdapter
    private val db = FirebaseFirestore.getInstance()
    private val unitList = mutableListOf<Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_contact)

        recyclerView = findViewById(R.id.recyclerViewUnits)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Khởi tạo Adapter trước khi tải dữ liệu
        unitAdapter = UnitAdapter(unitList)
        recyclerView.adapter = unitAdapter

        loadUnits()
    }

    private fun loadUnits() {
        db.collection("Units")
            .get()
            .addOnSuccessListener { result ->
                val newList = mutableListOf<Unit>()
                for (document in result) {
                    val unit = document.toObject(Unit::class.java).copy(id = document.id)
                    newList.add(unit)
                }
                unitAdapter.updateList(newList) // Cập nhật danh sách
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Lỗi khi tải dữ liệu", e)
            }
    }
}
