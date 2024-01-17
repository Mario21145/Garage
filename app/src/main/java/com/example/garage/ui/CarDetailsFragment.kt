package com.example.garage.ui

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.garage.DbIstance
import com.example.garage.R
import com.example.garage.databinding.FragmentCarDetailsBinding
import com.example.garage.databinding.FragmentHomeBinding
import com.example.garage.datasets.Dataset
import com.example.garage.models.CarDb
import com.example.garage.viewmodels.CarViewModel
import com.example.garage.viewmodels.CarViewModelFactory
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text


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

        val currentCar = sharedViewModel.currentCar

        binding.carBrandDetails.text = currentCar[0].Brand
        binding.carCubicCapacityDetails.text = currentCar[0].cubicCapacity
        binding.carFuelDetails.text = currentCar[0].powerSupply
        binding.carKmDetails.text = currentCar[0].km
        binding.carDescriptionDetails.text = currentCar[0].description
        binding.carYearDetails.text = currentCar[0].year

        binding.carModelDetails.text = currentCar[0].model
        binding.carLogoDetails.load(currentCar[0].logo) {
            crossfade(true)
            placeholder(R.drawable.loading)
            error(R.drawable.pictures)
        }


        binding.backArrow.setOnClickListener {
            if (binding.deleteCar.visibility == INVISIBLE && binding.editCar.visibility == INVISIBLE) {
                binding.deleteCar.visibility = VISIBLE
                binding.editCar.visibility = VISIBLE
                binding.confirmCar.visibility = INVISIBLE

                setEditTextVisibility(false , currentCar)
                setTextViewVisibility(true)
            } else {
                sharedViewModel.clearCurrentCar()
                findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
            }
        }

        binding.deleteCar.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
                .setTitle("You want to delete your car?")
                .setPositiveButton("OK") { dialog, which ->
                    sharedViewModel.deleteCar(currentCar[0])
                    sharedViewModel.clearCurrentCar()
                    Log.d("Sata", "${sharedViewModel.currentCar}")
                    findNavController().navigate(R.id.action_carDetailsFragment_to_homeFragment)
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

            setEditTextVisibility(true , currentCar)
            setTextViewVisibility(false)

        }

        binding.confirmCar.setOnClickListener{
            if(binding.carBrand.text?.isNotEmpty() == true && binding.carCubicCapacity.text?.isNotEmpty() == true && binding.filledExposedDropdownFuel.text?.isNotEmpty() == true && binding.carKm.text?.isNotEmpty() == true && binding.carDescription.text?.isNotEmpty() == true && binding.carYear.text?.isNotEmpty() == true){
            }

            val carUpdated = CarDb(binding.carBrand.text.toString(), currentCar[0].Brand , binding.carCubicCapacity.text.toString(), binding.filledExposedDropdownFuel.text.toString(), binding.carKm.text.toString(), binding.carDescription.text.toString(), binding.carYear.text.toString() , currentCar[0].logo)

            sharedViewModel.clearCurrentCar()
            sharedViewModel.setCurrentCar(carUpdated)
            sharedViewModel.updateCar(carUpdated)

            binding.deleteCar.visibility = VISIBLE
            binding.editCar.visibility = VISIBLE

            setEditTextVisibility(false , currentCar)
            setTextViewVisibility(true)

            binding.confirmCar.visibility = INVISIBLE
        }
    }




    fun setEditTextVisibility(isVisible: Boolean , currentCar : List<CarDb>) {
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

        for (editText in editTextLayouts) {
                if (isVisible) {
                    editText.visibility = VISIBLE
                } else {
                    editText.visibility = INVISIBLE
                }
            }

        for ((index, element) in editTextLayouts.withIndex()) {
            val editText = editTexts[index]
            if (isVisible) {
                element.visibility = VISIBLE
                when (editText.id) {
                    R.id.car_brand -> editText.setText(currentCar[0].Brand)
                    R.id.car_cubic_capacity -> editText.setText(currentCar[0].cubicCapacity)
                    R.id.filled_exposed_dropdown_fuel -> {
                        val dataSet = Dataset()
                        val dropdown = binding.filledExposedDropdownFuel

                        val adapterFuels = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            dataSet.typeFuels
                        )
                        dropdown.setAdapter(adapterFuels)

                        val fuelIndex = dataSet.typeFuels.indexOf(currentCar[0].powerSupply)
                        dropdown.setText(adapterFuels.getItem(fuelIndex), false)

                    }
                    R.id.car_km -> editText.setText(currentCar[0].powerSupply)
                    R.id.car_description -> editText.setText(currentCar[0].description)
                    R.id.car_year -> editText.setText(currentCar[0].year)
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
            binding.carKmDetails,
            binding.carDescriptionDetails,
            binding.carYearDetails,
        )

        for (textView in textViewList) {
            textView.visibility = if (isVisible) VISIBLE else INVISIBLE
        }
    }


}