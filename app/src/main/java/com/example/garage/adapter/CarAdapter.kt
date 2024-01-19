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
import com.example.garage.models.CarDb
import com.example.garage.models.RemoteCarData
import com.example.garage.viewmodels.CarViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class CarAdapter(viewModel: CarViewModel ,  private val clickListener: (CarDb) -> Unit) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    var cars : List<CarDb> = viewModel.carList.value ?: emptyList()

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carLogo : ImageView = itemView.findViewById(R.id.CarLogo)
        val carModel : TextView = itemView.findViewById(R.id.CarModel)
        val carYear : TextView = itemView.findViewById(R.id.CarYear)
        val carDescription : TextView = itemView.findViewById(R.id.CarDescription)
        val infoButton : FloatingActionButton = itemView.findViewById(R.id.infoCar)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.fragment_car , parent, false)
        return CarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        holder.carLogo.setImageResource(R.drawable.placeholder_car_image)

        holder.carModel.text = car.model
        holder.carYear.text = car.year
        holder.carDescription.text = car.description

        holder.carLogo.load(car.logo) {
            crossfade(true)
            placeholder(R.drawable.loading)
            error(R.drawable.pictures)
        }

        holder.infoButton.setOnClickListener{
            clickListener(car)
        }

    }

    override fun getItemCount(): Int {
        return cars.size
    }
}





