package com.example.garage.ui

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.garage.DbIstance
import com.example.garage.R
import com.example.garage.databinding.FragmentCarDetailsBinding
import com.example.garage.datasets.Dataset
import com.example.garage.models.CarDb
import com.example.garage.models.NotificationDb
import com.example.garage.viewmodels.CarViewModel
import com.example.garage.viewmodels.CarViewModelFactory
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CarDetailsFragment : Fragment() {

    val sharedViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(
            (activity?.application as DbIstance).database.CarDao(), Application()
        )
    }
    private lateinit var binding: FragmentCarDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(false)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val currentCar = sharedViewModel.updatedCar

        sharedViewModel.carList.observe(viewLifecycleOwner) {
                if(sharedViewModel.carList.value.isNullOrEmpty()){
                    binding.root.visibility = GONE
                } else {
                    binding.root.visibility = VISIBLE
                    if (it.isNotEmpty()) {
                        binding.carBrandDetails.text = getString(R.string.model, it[0].model)
                        binding.carCubicCapacityDetails.text = getString(R.string.cubicCapacity, it[0].cubicCapacity)
                        binding.carFuelDetails.text = getString(R.string.fuel, it[0].powerSupply)
                        binding.carKmDetails.text = getString(R.string.km, it[0].km)
                        binding.carDescriptionDetails.text = getString(R.string.description, it[0].description)
                        binding.carYearDetails.text = getString(R.string.year, it[0].year)
                        binding.carModelDetails?.text = it[0].model
                        binding.carLogoDetails?.load(it[0].logo) {
                            crossfade(true)
                            placeholder(R.drawable.loading)
                            error(R.drawable.pictures)
                        }
                    }
                }
        }


        sharedViewModel.updatedCar.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.carBrandDetails.text = getString(R.string.model, it[0].model)
                binding.carCubicCapacityDetails.text = getString(R.string.cubicCapacity, it[0].cubicCapacity)
                binding.carFuelDetails.text = getString(R.string.fuel, it[0].powerSupply)
                binding.carKmDetails.text = getString(R.string.km, it[0].km)
                binding.carDescriptionDetails.text = getString(R.string.description, it[0].description)
                binding.carYearDetails.text = getString(R.string.year, it[0].year)

                binding.carModelDetails?.text = it[0].Brand

                binding.carLogoDetails?.load(it[0].logo) {
                    crossfade(true)
                    placeholder(R.drawable.loading)
                    error(R.drawable.pictures)
                }
            }
        }


        binding.backArrow?.setOnClickListener {
            if (binding.deleteCar.visibility == INVISIBLE && binding.editCar.visibility == INVISIBLE) {
                binding.deleteCar.visibility = VISIBLE
                binding.editCar.visibility = VISIBLE
                binding.confirmCar.visibility = INVISIBLE

                setEditTextVisibility(false, currentCar)
                setTextViewVisibility(true)
            } else {
                findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
            }
        }

        binding.deleteCar.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
                .setTitle(R.string.deleteDialogTitle)
                .setPositiveButton("OK") { dialog, which ->
                    if (resources.configuration.screenWidthDp > 600) {
                        if (!currentCar.value.isNullOrEmpty()) {
                            sharedViewModel.deleteCar(currentCar.value!![0])
                            sharedViewModel.clearCurrentCar()
                            sharedViewModel.getCars()
                        }
                    } else {
                        sharedViewModel.deleteCar(currentCar.value!![0])
                        sharedViewModel.clearCurrentCar()
                        findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
                    }
                }
                .setNegativeButton("NO") { dialog, which ->
                    dialog.dismiss()
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        binding.editCar.setOnClickListener {
            binding.deleteCar.visibility = INVISIBLE
            binding.editCar.visibility = INVISIBLE
            binding.confirmCar.visibility = VISIBLE

            setEditTextVisibility(true, currentCar)
            setTextViewVisibility(false)
        }

        binding.confirmCar.setOnClickListener {
            if (binding.carBrand.text?.isNotEmpty() == true && binding.carCubicCapacity.text?.isNotEmpty() == true &&
                binding.filledExposedDropdownFuel.text?.isNotEmpty() == true && binding.carKm.text?.isNotEmpty() == true &&
                binding.carDescription.text?.isNotEmpty() == true && binding.carYear.text?.isNotEmpty() == true
            ) {
                val model = binding.carBrand.text.toString()
                val cubicCapacity = binding.carCubicCapacity.text.toString()
                val fuel = binding.filledExposedDropdownFuel.text.toString()
                val km = binding.carKm.text.toString()
                val description = binding.carDescription.text.toString()
                val year = binding.carYear.text.toString()

                val carUpdated = CarDb(
                    currentCar.value?.get(0)?.id,
                    model,
                    currentCar.value?.get(0)?.Brand ?: "",
                    cubicCapacity,
                    fuel,
                    km,
                    description,
                    year,
                    currentCar.value?.get(0)?.logo ?: ""
                )

                binding.confirmCar.visibility = INVISIBLE
                binding.deleteCar.visibility = VISIBLE
                binding.editCar.visibility = VISIBLE

                setEditTextVisibility(false, currentCar)
                setTextViewVisibility(true)

                val k = currentCar.value!![0].km
                sharedViewModel.clearCurrentCar()
                sharedViewModel.setCurrentCar(carUpdated)

                lifecycleScope.launch(Dispatchers.IO) {
                    sharedViewModel.scheduleReminder(requireContext() , carUpdated , k)
                    sharedViewModel.updateCar(carUpdated)
                }

            }
        }

    }


    fun setEditTextVisibility(isVisible: Boolean, currentCar: MutableLiveData<List<CarDb>>) {
        val editTextLayouts: List<TextInputLayout> = listOf(
            binding.carBrandLabel,
            binding.carCubicCapacityLabel,
            binding.carFuelLabel,
            binding.carKmLabel,
            binding.carDescriptionLabel,
            binding.carYearLabel,
        )

        val editTexts: List<EditText> = listOf(
            binding.carBrand,
            binding.carCubicCapacity,
            binding.filledExposedDropdownFuel,
            binding.carKm,
            binding.carDescription,
            binding.carYear,
        )

        for ((index, element) in editTextLayouts.withIndex()) {
            val editText = editTexts[index]
            if (isVisible) {
                element.visibility = VISIBLE
                when (editText.id) {
                    R.id.car_brand -> editText.setText(currentCar.value?.get(0)?.model ?: "")
                    R.id.car_cubic_capacity -> editText.setText(
                        currentCar.value?.get(0)?.cubicCapacity ?: ""
                    )

                    R.id.filled_exposed_dropdown_fuel -> {
                        val dataSet = Dataset()
                        val dropdown = binding.filledExposedDropdownFuel

                        val adapterFuels = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            dataSet.typeFuels
                        )

                        dropdown.setAdapter(adapterFuels)

                        val fuelIndex = dataSet.typeFuels.indexOf(currentCar.value?.get(0)?.powerSupply ?: "")
                        dropdown.setText(adapterFuels.getItem(fuelIndex), false)
                    }

                    R.id.car_km -> editText.setText(currentCar.value?.get(0)?.km ?: "")
                    R.id.car_description -> editText.setText(
                        currentCar.value?.get(0)?.description ?: ""
                    )

                    R.id.car_year -> editText.setText(currentCar.value?.get(0)?.year ?: "")
                }
            } else {
                element.visibility = INVISIBLE
                editText.text.clear()
            }
        }
    }


    fun setTextViewVisibility(isVisible: Boolean) {
        val textViewList: List<TextView> = listOf(
            binding.carBrandDetails,
            binding.carCubicCapacityDetails,
            binding.carFuelDetails,
            binding.carFuelDetails,
            binding.carKmDetails,
            binding.carDescriptionDetails,
            binding.carYearDetails,
        )

        for (textView in textViewList) {
            textView.visibility = if (isVisible) VISIBLE else INVISIBLE
        }
    }


}