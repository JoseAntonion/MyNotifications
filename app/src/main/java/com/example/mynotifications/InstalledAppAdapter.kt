package com.example.mynotifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotifications.databinding.ItemAppBinding

class InstalledAppAdapter(private val apps: MutableList<String>?) :
    RecyclerView.Adapter<InstalledAppAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemNoti = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemNoti)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (apps != null) {
            val currentNoti = apps[position]
            holder.binding.apply {
                appNameText.text = currentNoti
            }
        }
    }

    override fun getItemCount(): Int {
        return apps?.size ?: 0
    }

    class ViewHolder(view: ItemAppBinding) : RecyclerView.ViewHolder(view.root) {
        val binding = view
    }

}