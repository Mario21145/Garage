package com.example.garage.ui

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
import com.example.garage.R
import com.example.garage.databinding.FragmentHomeBinding
import com.example.garage.viewmodels.CarViewModel


class HomeFragment : Fragment() {

    private val sharedViewModel: CarViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = this@HomeFragment
        }


        binding.AddCar.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_addCarFragment)
        }

        sharedViewModel.carList.observe(viewLifecycleOwner){
            Log.d("Data" , "${sharedViewModel.carList.value}")
        }


    }


}