package com.example.tlucontactapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tlucontactapp.R
import com.example.tlucontactapp.models.Student

class StudentAdapter(
    private var studentList: List<Student>,
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        val tvClassName: TextView = view.findViewById(R.id.tvClassName)
        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)

        fun bind(student: Student, onItemClick: (Student) -> Unit) {
            tvName.text = student.fullName
            tvEmail.text = student.email
            tvPhone.text = student.phone
            tvClassName.text = student.classId

            if (student.photoURL.isNotEmpty()) {
                Glide.with(itemView.context).load(student.photoURL).into(imgProfile)
            } else {
                imgProfile.setImageResource(R.drawable.avatar1) // Ảnh mặc định
            }

            itemView.setOnClickListener { onItemClick(student) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(studentList[position], onItemClick)
    }

    override fun getItemCount() = studentList.size

    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }
}
