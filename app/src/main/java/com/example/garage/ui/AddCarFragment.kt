package com.example.garage.ui

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.garage.DbIstance
import com.example.garage.R
import com.example.garage.databinding.FragmentAddCarBinding
import com.example.garage.datasets.Dataset
import com.example.garage.viewmodels.CarViewModel
import com.example.garage.viewmodels.CarViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCarFragment : Fragment() {

    val sharedViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(
            (activity?.application as DbIstance).database.CarDao(), Application()
        )
    }
    private lateinit var binding: FragmentAddCarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_car, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = this@AddCarFragment
        }

        val dataSet = Dataset()

        val adapterFuels = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            dataSet.typeFuels
        )
        binding.filledExposedDropdownFuel.setAdapter(adapterFuels)

        val carYearLabel = binding.carYearLabel
        val carYear = carYearLabel.editText?.text


        //Text inputs
        lateinit var carBrand : String
        lateinit var carFuel : String
        lateinit var logo : String
        carBrand = ""
        carFuel = ""
        logo = ""


        val carModelLabel = binding.carNameLabel
        val carModel = carModelLabel.editText?.text

        val dropdownBrand = binding.filledExposedDropdownBrand
        sharedViewModel.carLogos.observe(viewLifecycleOwner) { carList ->
            val adapterBrand = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                carList.map { it.name }
            )
            binding.filledExposedDropdownBrand.setAdapter(adapterBrand)

            binding.filledExposedDropdownBrand.setOnItemClickListener { _, _, position, _ ->
                val selectedCar = carList[position]
                logo = selectedCar.logo
                carBrand = dropdownBrand.adapter.getItem(position).toString()
            }
        }

        val carCubicCapacityLabel = binding.carCubicCapacityLabel
        val carCubicCapacity = carCubicCapacityLabel.editText?.text

        val dropdownFuel = binding.filledExposedDropdownFuel
        dropdownFuel.setOnItemClickListener { _, _, position, _ ->
            carFuel = dropdownFuel.adapter.getItem(position).toString()
        }

        val carKmLabel = binding.carKmLabel
        val carKm = carKmLabel.editText?.text

        val carDescriptionLabel = binding.carDescriptionLabel
        val carDescription = carDescriptionLabel.editText?.text


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.InsertButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                sharedViewModel.insertCar(carModel.toString() , carBrand , carCubicCapacity.toString() , carFuel , carKm.toString() , carDescription.toString() , carYear.toString() , logo)
            }
            findNavController().navigate(R.id.action_addCarFragment_to_homeFragment)
        }
        //Icon dark Mode


    }

}