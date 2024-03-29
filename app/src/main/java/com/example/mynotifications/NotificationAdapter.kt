package com.example.mynotifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotifications.databinding.ItemNotiDataBinding

class NotificationAdapter(private val notiList: MutableList<NotificationData>?) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemNoti =
            ItemNotiDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemNoti)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (notiList != null) {
            val currentNoti = notiList[position]
            holder.binding.apply {
                appName.text = currentNoti.notiTitle
                notiDesc.text = currentNoti.notiContent
                notiDate.text = currentNoti.notiDate
                appPckg.text = currentNoti.notiPackage
            }
        }
    }

    override fun getItemCount(): Int {
        return notiList?.size ?: 0
    }

    class ViewHolder(view: ItemNotiDataBinding) : RecyclerView.ViewHolder(view.root) {
        val binding = view
    }

}