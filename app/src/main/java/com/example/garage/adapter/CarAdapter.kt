package com.example.garage.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.garage.R
import com.example.garage.models.RemoteCarData
import com.example.garage.viewmodels.CarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CarAdapter(viewModel: CarViewModel ,  private val clickListener: (RemoteCarData) -> Unit) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var plantImage: ImageView = itemView.findViewById(R.id.PlantImage)
//        var plantName: TextView = itemView.findViewById(R.id.name)
//        var plantType: TextView = itemView.findViewById(R.id.type)
//        var plantDescription: TextView = itemView.findViewById(R.id.description)
//        var plantSchedule: TextView = itemView.findViewById(R.id.schedule)
//        var switch: Switch = itemView.findViewById(R.id.choiceNotification)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.fragment_car , parent, false)
        return CarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarAdapter.CarViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}
