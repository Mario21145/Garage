package com.example.garage.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.garage.R
import com.example.garage.models.CarDb
import com.example.garage.models.NotificationDb
import com.example.garage.viewmodels.CarViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NotificationAdapter(viewModel: CarViewModel) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    var notifications : List<NotificationDb> = viewModel.notifications.value ?: emptyList()
    var viewModel = viewModel

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carNameNotification : TextView = itemView.findViewById(R.id.carNameNotification)
        val carDescription : TextView = itemView.findViewById(R.id.descriptionNotification)
        val deleteButton : ImageButton = itemView.findViewById(R.id.deleteNotification)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.notification_item , parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.carNameNotification.text = notification.carName
        holder.carDescription.text = notification.description

        val notificationDb = NotificationDb(notification.carName , notification.description)
        holder.deleteButton.setOnClickListener{
            viewModel.deleteNotification(notificationDb)
            viewModel.getNotifications()
        }

    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}





