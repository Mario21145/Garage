package com.example.garage.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
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
            (activity?.application as DbIstance).database.CarDao(), Application()
        )
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = this@HomeFragment
        }

        //CarDetails sliding pane layout only for large display
        val slidingPaneLayoutCar = binding.slidingPaneLayoutCarDetails
        slidingPaneLayoutCar?.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        slidingPaneLayoutCar?.let { CarsListOnBackPressedCallback(it) }?.let {
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                it
            )
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)


        sharedViewModel.getCars()
        sharedViewModel.carList.observe(viewLifecycleOwner) { cars ->

            if (resources.configuration.screenWidthDp > 600) {
                if (sharedViewModel.carList.value!!.isNotEmpty()) {
                    sharedViewModel.setCurrentCar(cars[0])
                }
            }

            val adapter = CarAdapter(sharedViewModel) { car ->
                val slidingPanelayoutCar = binding.slidingPaneLayoutCarDetails
                val slidingPanel = binding.HomeRecyclerView
                if (resources.configuration.screenWidthDp > 600) {
                    slidingPanel.visibility = View.VISIBLE
                    slidingPanelayoutCar?.openPane()
                    sharedViewModel.setCurrentCar(car)
                } else {
                    binding.slidingPaneLayoutCarDetails?.visibility = View.GONE
                    sharedViewModel.setCurrentCar(car)
                    findNavController().navigate(R.id.action_homeFragment_to_carDetailsFragment)
                }
            }

            binding.HomeRecyclerView.adapter = adapter
        }




        sharedViewModel.notifications.observe(viewLifecycleOwner) {
            val size = it.size
            if (size == 0) {
                binding.notificationCounter.visibility = View.GONE
            } else {
                binding.notificationCounter.visibility = View.VISIBLE
                binding.notificationCounter.text = size.toString()
            }
        }

        sharedViewModel.isInternetAvailable(requireContext())
        sharedViewModel.isInternetAvailable.observe(viewLifecycleOwner){
            binding.AddCar.isEnabled = it
        }

        binding.AddCar.setOnClickListener {
            if(binding.AddCar.isEnabled){
                findNavController().navigate(R.id.action_homeFragment_to_addCarFragment)
            } else {
                Log.d("State" , "No connection")
            }
        }

        binding.SearchCar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val list = mutableListOf<CarDb>()
                if (query != null) {
                    for (i in sharedViewModel.carList.value!!) {
                        if (i.model.contains(query, true)) {
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

        binding.SearchCar.setOnCloseListener {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            sharedViewModel.getCars()
            true
        }

        binding.notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }
    }
}

class CarsListOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout,
) : OnBackPressedCallback(
    slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
), SlidingPaneLayout.PanelSlideListener {

    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }

    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {}

    override fun onPanelOpened(panel: View) {
        isEnabled = true
    }

    override fun onPanelClosed(panel: View) {
        isEnabled = false
    }
}
