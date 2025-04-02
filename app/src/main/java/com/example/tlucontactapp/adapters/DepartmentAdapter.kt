package com.example.tlucontactapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tlucontactapp.R
import com.example.tlucontactapp.models.Department

class DepartmentAdapter(
    private var departmentList: List<Department>,
    private val onItemClick: (Department) -> Unit
) : RecyclerView.Adapter<DepartmentAdapter.UnitViewHolder>() {

    class UnitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUnitName: TextView = view.findViewById(R.id.tvUnitName)
        val tvUnitAddress: TextView = view.findViewById(R.id.tvUnitAddress)
        val tvUnitPhone: TextView = view.findViewById(R.id.tvUnitPhone)
        val imgLogo: ImageView = view.findViewById(R.id.imgUnitLogo)

        fun bind(department: Department, onItemClick: (Department) -> Unit) {
            tvUnitName.text = department.departmentName
            tvUnitAddress.text = department.departmentAddress
            tvUnitPhone.text = department.departmentPhone

            if (!department.logoURL.isNullOrEmpty()) {
                Glide.with(itemView.context).load(department.logoURL).into(imgLogo)
            } else {
                imgLogo.setImageResource(R.drawable.avatar1) // Ảnh mặc định
            }
            itemView.setOnClickListener { onItemClick(department) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_department, parent, false)
        return UnitViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.bind(departmentList[position], onItemClick)
    }

    override fun getItemCount(): Int = departmentList.size

    fun updateList(newList: List<Department>) {
        departmentList = newList
        notifyDataSetChanged()
    }
}


