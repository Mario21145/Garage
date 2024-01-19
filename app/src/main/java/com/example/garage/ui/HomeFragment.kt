package com.example.garage.ui

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.garage.DbIstance
import com.example.garage.R
import com.example.garage.adapter.CarAdapter
import com.example.garage.databinding.FragmentHomeBinding
import com.example.garage.models.CarDb
import com.example.garage.viewmodels.CarViewModel
import com.example.garage.viewmodels.CarViewModelFactory


class HomeFragment : Fragment() {


    val sharedViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(
            (activity?.application as DbIstance).database.CarDao() , Application()
        )
    }

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


        sharedViewModel.getCars()
        sharedViewModel.carList.observe(viewLifecycleOwner){ cars ->
        val adapter = CarAdapter(sharedViewModel){car ->
                sharedViewModel.setCurrentCar(car)
                findNavController().navigate(R.id.action_homeFragment_to_carDetailsFragment)
            }
            binding.HomeRecyclerView.adapter = adapter
        }


        binding.AddCar.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_addCarFragment)
        }



        binding.SearchCar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val list = mutableListOf<CarDb>()
                if (query != null) {

                    for (i in sharedViewModel.carList.value!!) {
                        if(i.model.contains(query , true)){
                            list.add(i)
                        }
                    }

                    sharedViewModel.carList.value = list
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        binding.SearchCar.setOnCloseListener{
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            sharedViewModel.getCars()
            true
        }


    }


}