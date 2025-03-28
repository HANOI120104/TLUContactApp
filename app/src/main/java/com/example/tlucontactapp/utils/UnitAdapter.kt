package com.example.tlucontactapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.R
import com.example.tlucontactapp.models.Unit

class UnitAdapter(private var unitList: List<Unit>) :
    RecyclerView.Adapter<UnitAdapter.UnitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unit, parent, false)
        return UnitViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        val unit = unitList[position]
        holder.tvUnitName.text = unit.name
    }

    override fun getItemCount(): Int = unitList.size

    fun updateList(newList: List<Unit>) {
        unitList = newList
        notifyDataSetChanged()
    }

    class UnitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUnitName: TextView = itemView.findViewById(R.id.tvUnitName)
    }
}
