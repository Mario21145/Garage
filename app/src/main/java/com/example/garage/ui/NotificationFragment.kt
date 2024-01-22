package com.example.garage.ui

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.garage.DbIstance
import com.example.garage.R
import com.example.garage.adapter.NotificationAdapter
import com.example.garage.databinding.FragmentHomeBinding
import com.example.garage.databinding.FragmentNotificationBinding
import com.example.garage.viewmodels.CarViewModel
import com.example.garage.viewmodels.CarViewModelFactory

class NotificationFragment : Fragment() {



    val sharedViewModel: CarViewModel by activityViewModels {
        CarViewModelFactory(
            (activity?.application as DbIstance).database.CarDao(), Application()
        )
    }

    private lateinit var binding: FragmentNotificationBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = this@NotificationFragment
        }

        sharedViewModel.getNotifications()

        sharedViewModel.notifications.observe(viewLifecycleOwner){
            val adapter = NotificationAdapter(sharedViewModel)
            binding.notificationRecyclerView.adapter = adapter
            binding.deleteAllNotification.setOnClickListener{
                if(sharedViewModel.notifications.value!!.isNotEmpty()){
                    sharedViewModel.clearNotifications()
                    sharedViewModel.getNotifications()
                }
            }
        }


        binding.backArrow.setOnClickListener{
            findNavController().popBackStack()
        }

    }


}