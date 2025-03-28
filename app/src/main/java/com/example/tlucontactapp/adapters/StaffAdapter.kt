package com.example.tlucontactapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tlucontactapp.R
import com.example.tlucontactapp.models.Staff

class StaffAdapter(
    private var staffList: List<Staff>,
    private val onItemClick: (Staff) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    class StaffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        val tvPosition: TextView = view.findViewById(R.id.tvPosition)
        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)

        fun bind(staff: Staff, onItemClick: (Staff) -> Unit) {
            tvName.text = staff.fullName
            tvEmail.text = staff.email
            tvPhone.text = staff.phone
            tvPosition.text = staff.position

            if (staff.photoURL.isNotEmpty()) {
                Glide.with(itemView.context).load(staff.photoURL).into(imgProfile)
            } else {
                imgProfile.setImageResource(R.drawable.avatar1) // Ảnh mặc định
            }

            itemView.setOnClickListener { onItemClick(staff) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        holder.bind(staffList[position], onItemClick)
    }

    override fun getItemCount() = staffList.size

    fun updateList(newList: List<Staff>) {
        staffList = newList
        notifyDataSetChanged()
    }
}
