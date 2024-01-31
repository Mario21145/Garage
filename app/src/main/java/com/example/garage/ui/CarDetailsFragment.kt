package com.example.garage.ui

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import kotlinx.coroutines.withContext


class CarDetailsFragment : Fragment() {

    private val sharedViewModel: CarViewModel by activityViewModels {
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

        sharedViewModel.carList.observe(viewLifecycleOwner) { cars ->
            binding.root.visibility = VISIBLE
            if (cars.isNotEmpty()) {
                val car = cars[0]
                binding.carBrandDetails.text = getString(R.string.model, car.model)
                binding.carCubicCapacityDetails.text = getString(R.string.cubicCapacity, car.cubicCapacity)
                binding.carFuelDetails.text = getString(R.string.fuel, car.powerSupply)
                binding.carKmDetails.text = getString(R.string.km, car.km)
                binding.carDescriptionDetails.text = getString(R.string.description, car.description)
                binding.carYearDetails.text = getString(R.string.year, car.year)
                binding.carModelDetails?.text = car.model
                val bmp = car.imageLogo?.let { BitmapFactory.decodeByteArray(car.imageLogo, 0, it.size) }
                binding.carLogoDetails?.setImageBitmap(bmp)
            }
        }

        sharedViewModel.updatedCar.observe(viewLifecycleOwner) { updatedCar ->
            if (updatedCar.isNotEmpty()) {
                val car = updatedCar[0]
                binding.carBrandDetails.text = getString(R.string.model, car.model)
                binding.carCubicCapacityDetails.text = getString(R.string.cubicCapacity, car.cubicCapacity)
                binding.carFuelDetails.text = getString(R.string.fuel, car.powerSupply)
                binding.carKmDetails.text = getString(R.string.km, car.km)
                binding.carDescriptionDetails.text = getString(R.string.description, car.description)
                binding.carYearDetails.text = getString(R.string.year, car.year)
                binding.carModelDetails?.text = car.Brand
                val bmp = car.imageLogo?.let { BitmapFactory.decodeByteArray(car.imageLogo, 0, it.size) }
                binding.carLogoDetails?.setImageBitmap(bmp)
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
                    currentCar.value?.get(0)?.logo ?: "",
                    currentCar.value?.get(0)?.logo?.let { it1 -> sharedViewModel.downloadImage(it1) }
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
                    sharedViewModel.scheduleReminder(requireContext(), carUpdated, k)
                    sharedViewModel.updateCar(carUpdated)
                    val imm =
                        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
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
                        val fuelIndex =
                            dataSet.typeFuels.indexOf(currentCar.value?.get(0)?.powerSupply ?: "")
                        if (fuelIndex != -1) {
                            dropdown.setText(adapterFuels.getItem(fuelIndex), false)
                        } else {
                            dropdown.setText(adapterFuels.getItem(0), false)
                        }
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