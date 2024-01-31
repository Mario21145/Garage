package com.example.garage.adapter

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.garage.R
import com.example.garage.models.CarDb
import com.example.garage.models.RemoteCarData
import com.example.garage.viewmodels.CarViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class CarAdapter(viewModel: CarViewModel, private val clickListener: (CarDb) -> Unit) :
    ListAdapter<CarDb, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    init {
        submitList(viewModel.carList.value)
    }

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carLogo: ImageView = itemView.findViewById(R.id.CarLogo)
        val carModel: TextView = itemView.findViewById(R.id.CarModel)
        val carYear: TextView = itemView.findViewById(R.id.CarYear)
        val carDescription: TextView = itemView.findViewById(R.id.CarDescription)
        val infoButton: FloatingActionButton = itemView.findViewById(R.id.infoCar)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.fragment_car, parent, false)
        return CarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = getItem(position)
        holder.carLogo.setImageResource(R.drawable.placeholder_car_image)

        holder.carModel.text = car.model
        holder.carYear.text = car.year
        holder.carDescription.text = car.description

        val bmp = car.imageLogo?.let { BitmapFactory.decodeByteArray(car.imageLogo, 0, it.size) }
        holder.carLogo.setImageBitmap(bmp)

        holder.infoButton.setOnClickListener {
            clickListener(car)
        }
    }

    private class CarDiffCallback : DiffUtil.ItemCallback<CarDb>() {
        override fun areItemsTheSame(oldItem: CarDb, newItem: CarDb): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CarDb, newItem: CarDb): Boolean {
            return oldItem == newItem
        }
    }
}






