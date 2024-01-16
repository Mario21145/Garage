package com.example.garage.ui

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.garage.DbIstance
import com.example.garage.R
import com.example.garage.databinding.FragmentCarDetailsBinding
import com.example.garage.databinding.FragmentHomeBinding
import com.example.garage.viewmodels.CarViewModel
import com.example.garage.viewmodels.CarViewModelFactory


class CarDetailsFragment : Fragment() {

    val sharedViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(
            (activity?.application as DbIstance).database.CarDao() , Application()
        )
    }
    private lateinit var binding: FragmentCarDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(false)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("ListData" , "${sharedViewModel.currentCar}")

        val currentCar = sharedViewModel.currentCar

        binding.carBrandDetails.text = currentCar[0].Brand
        binding.carCubicCapacityDetails.text = currentCar[0].cubicCapacity
        binding.carFuelDetails.text = currentCar[0].powerSupply
        binding.carKmDetails.text = currentCar[0].km
        binding.carDescriptionDetails.text = currentCar[0].description
        binding.carYearDetails.text  =currentCar[0].year


        binding.carModelDetails.text = currentCar[0].model
        binding.carLogoDetails.load(currentCar[0].logo) {
            crossfade(true)
            placeholder(R.drawable.loading)
            error(R.drawable.pictures)
        }



        binding.backArrow.setOnClickListener{
            sharedViewModel.clearCurrentCar()
            Log.d("StateCurrentCar" , "From details fragment: ${sharedViewModel.currentCar}")
            findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
        }



    }



}