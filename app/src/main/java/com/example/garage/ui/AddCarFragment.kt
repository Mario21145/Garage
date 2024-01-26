package com.example.garage.ui

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.garage.DbIstance
import com.example.garage.R
import com.example.garage.databinding.FragmentAddCarBinding
import com.example.garage.datasets.Dataset
import com.example.garage.models.CarDb
import com.example.garage.viewmodels.CarViewModel
import com.example.garage.viewmodels.CarViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddCarFragment : Fragment() {

    private val sharedViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(
            (activity?.application as DbIstance).database.CarDao(), Application()
        )
    }
    private lateinit var binding: FragmentAddCarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(false)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_car, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("State" , "OnViewCreated")

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

        val carYear: NumberPicker = binding.carYear
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        carYear.minValue = currentYear - 50
        carYear.maxValue = currentYear
        val displayValues = Array(51) { (currentYear - 50 + it).toString() }
        carYear.displayedValues = displayValues
        carYear.value = currentYear

        //Text inputs
        lateinit var carBrand : String
        lateinit var carFuel : String
        lateinit var logo : String
        carBrand = ""
        carFuel = ""
        logo = ""


        val carModelLabel = binding.carNameLabel
        var carName = carModelLabel.editText?.text

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

            if(carName!!.isNotEmpty() && carBrand.isNotEmpty() && carCubicCapacity!!.isNotEmpty() && carFuel.isNotEmpty() && carKm!!.isNotEmpty() && carDescription!!.isNotEmpty() ){
                lifecycleScope.launch(Dispatchers.IO) {
                    val car = CarDb(null ,carName.toString() , carBrand , carCubicCapacity.toString() , carFuel , carKm.toString() , carDescription.toString() , currentYear.toString() , logo)
                    sharedViewModel.insertCar(car)
                }
                findNavController().navigate(R.id.action_addCarFragment_to_homeFragment)
            } else {
                context?.let { it1 -> sharedViewModel.makeToast(it1, getString(R.string.toastErrorFieldEmpty) , 30 ) }
            }
        }

        //Icons dark Mode
        val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if(isDarkTheme){
            binding.carModelIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.cubicCapacityIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.carFuelIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.carKmIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.CarDescriptionIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.CarYearIcon.setColorFilter(Color.argb(255, 255, 255, 255))
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

}