package com.example.garage.ui

import android.os.Bundle
import android.provider.ContactsContract.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.example.garage.R
import com.example.garage.database.CarDao
import com.example.garage.databinding.FragmentAddCarBinding
import com.example.garage.databinding.FragmentHomeBinding
import com.example.garage.datasets.Dataset
import com.example.garage.viewmodels.CarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddCarFragment : Fragment() {

    private val sharedViewModel: CarViewModel by activityViewModels()
    private lateinit var binding: FragmentAddCarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        sharedViewModel.carList.observe(viewLifecycleOwner){ carList ->
            val adapterBrand = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                carList.map { it.name }
            )
            binding.filledExposedDropdownBrand.setAdapter(adapterBrand)
        }

        val adapterFuels = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            dataSet.typeFuels
        )
        binding.filledExposedDropdownFuel.setAdapter(adapterFuels)









        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.InsertButton.setOnClickListener{
            GlobalScope.launch(Dispatchers.IO) {
                sharedViewModel.insertCar()
            }
        }

    }

}