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
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.util.Calendar
import kotlin.properties.Delegates
import kotlin.io.readBytes

class AddCarFragment : Fragment() {

    private val sharedViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(
            (activity?.application as DbIstance).database.CarDao(), Application()
        )
    }
    private lateinit var binding: FragmentAddCarBinding

    private lateinit var carBrand: String
    private lateinit var carName: String
    private lateinit var carCubicCapacity: String
    private lateinit var carFuel: String
    private lateinit var carKm: String
    private lateinit var carDescription: String
    private var currentYear by Delegates.notNull<Int>()
    private var selectedYear: Int? = null
    private lateinit var logo: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(false)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()

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



        val carYear: NumberPicker = binding.carYear
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
        selectedYear?.let {
            currentYear = it
        }
        carYear.minValue = currentYear - 50
        carYear.maxValue = currentYear
        val displayValues = Array(51) { (currentYear - 50 + it).toString() }
        carYear.displayedValues = displayValues




        if (savedInstanceState != null) {
            Log.d("State", "Stato e: $savedInstanceState")
            carBrand = savedInstanceState.getString("selectedBrandPosition", "")
            carName = savedInstanceState.getString("carName", "")
            carCubicCapacity = savedInstanceState.getString("cubicCapacity", "")
            carFuel = savedInstanceState.getString("selectedFuelPosition", "")
            carKm = savedInstanceState.getString("carKm", "")
            currentYear = savedInstanceState.getInt("selectedYear")
            val carYear: NumberPicker = binding.carYear
            carYear.value = currentYear
            carDescription = savedInstanceState.getString("carDescription", "")
            logo = savedInstanceState.getString("logo", "")

            val adapterFuels = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                dataSet.typeFuels
            )
            binding.filledExposedDropdownFuel.setAdapter(adapterFuels)

            sharedViewModel.carLogos.observe(viewLifecycleOwner) { carList ->
                val adapterBrand = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    carList.map { it.name }
                )

                binding.filledExposedDropdownBrand.setAdapter(adapterBrand)
            }

        } else {
            carBrand = ""
            carName = ""
            carCubicCapacity = ""
            carFuel = ""
            carKm = ""
            carDescription = ""
            logo = ""
        }





            val adapterFuels = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                dataSet.typeFuels
            )
            binding.filledExposedDropdownFuel.setAdapter(adapterFuels)


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


            val dropdownFuel = binding.filledExposedDropdownFuel
            dropdownFuel.setOnItemClickListener { _, _, position, _ ->
                carFuel = dropdownFuel.adapter.getItem(position).toString()
            }




        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.InsertButton.setOnClickListener {

            carName = binding.carNameLabel.editText?.text.toString()
            carCubicCapacity = binding.carCubicCapacityLabel.editText?.text.toString()
            carKm = binding.carKmLabel.editText?.text.toString()
            carDescription = binding.carDescriptionLabel.editText?.text.toString()

            val carYear: NumberPicker = binding.carYear
            currentYear = Calendar.getInstance().get(Calendar.YEAR)
            selectedYear?.let {
                currentYear = it
            }
            carYear.minValue = currentYear - 50
            carYear.maxValue = currentYear
            val displayValues = Array(51) { (currentYear - 50 + it).toString() }
            carYear.displayedValues = displayValues

            if (carName.isNotEmpty() && carBrand.isNotEmpty() && carCubicCapacity.isNotEmpty() && carFuel.isNotEmpty() && carKm.isNotEmpty() && carDescription.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val car = CarDb(
                        null,
                        carName,
                        carBrand,
                        carCubicCapacity,
                        carFuel,
                        carKm,
                        carDescription,
                        carYear.value.toString(),
                        logo,
                        sharedViewModel.downloadImage(logo)
                    )
                    sharedViewModel.insertCar(car)
                }
                findNavController().navigate(R.id.action_addCarFragment_to_homeFragment)
            } else {
                Log.d("Data" ,"${carName} , ${carBrand} ,${carCubicCapacity} , ${carFuel} , ${carKm} ,${carDescription}" )
                context?.let { it1 ->
                    sharedViewModel.makeToast(
                        it1,
                        getString(R.string.toastErrorFieldEmpty),
                        30
                    )
                }
            }
        }

        //Icons dark Mode
        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isDarkTheme) {
            binding.carModelIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.cubicCapacityIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.carFuelIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.carKmIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.CarDescriptionIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            binding.CarYearIcon.setColorFilter(Color.argb(255, 255, 255, 255))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("State", "Stato e: $outState")
        if (::binding.isInitialized) {
            outState.putString("carName", binding.carNameLabel.editText?.text.toString())
            outState.putString("cubicCapacity", binding.carCubicCapacityLabel.editText?.text.toString())
            outState.putString("carKm", binding.carKmLabel.editText?.text.toString())
            outState.putString("carDescription", binding.carDescriptionLabel.editText?.text.toString())
            outState.putString("selectedFuelPosition", carFuel)
            outState.putString("selectedBrandPosition", carBrand)
            outState.putString("logo", logo)
            val carYearSaved: NumberPicker = binding.carYear
            outState.putInt("selectedYear", carYearSaved.value)
        }
    }




}