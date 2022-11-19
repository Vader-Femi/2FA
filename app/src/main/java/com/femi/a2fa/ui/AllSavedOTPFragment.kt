package com.femi.a2fa.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.femi.a2fa.R
import com.femi.a2fa.database.OTPDataDatabase
import com.femi.a2fa.database.entity.OTPData
import com.femi.a2fa.databinding.FragmentAllSavedOTPBinding
import com.femi.a2fa.repository.OTPDataRepository
import com.femi.a2fa.utils.KEY_ACCOUNT_NAME
import com.femi.a2fa.utils.KEY_ID
import com.femi.a2fa.utils.KEY_SECRET_KEY
import com.femi.a2fa.viewmodels.OTPDataViewModel
import com.femi.a2fa.viewmodels.ViewModelFactory

class AllSavedOTPFragment : Fragment() {

    private lateinit var binding: FragmentAllSavedOTPBinding
    private lateinit var viewModel: OTPDataViewModel
    private lateinit var adapter: AllSavedOTPAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAllSavedOTPBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null && context != null) {
            setupViewModel(requireContext())

            initAdapter()
            binding.rvAccounts.setHasFixedSize(true)
            binding.rvAccounts.layoutManager = LinearLayoutManager(context)
            binding.rvAccounts.adapter = adapter

            viewModel.getData().observe(viewLifecycleOwner) {
                renderOTPData(it)
            }

            controlFab()

            binding.fabAdd.setOnClickListener {
                findNavController().navigate(R.id.action_accountsFragment_to_addAccountFragment)
            }

        }
    }

    private fun controlFab() {
        binding.rvAccounts.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 20 && binding.fabAdd.isExtended) {
                    binding.fabAdd.shrink()
                }

                if (dy < -20 && !binding.fabAdd.isExtended) {
                    binding.fabAdd.extend()
                }

//                if (!binding.fabAddNewEvent.canScrollVertically(-1)) {
//                    binding.fabAddNewEvent.extend()
//                }

//                if (!binding.fabAddNewEvent.canScrollVertically(1)) {
//                    binding.fabAddNewEvent.extend()
//                }

            }
        })
    }

    private fun initAdapter() {
        adapter = AllSavedOTPAdapter { account ->
            view?.let {
                findNavController().navigate(R.id.action_accountsFragment_to_OTPFragment,Bundle().apply {
                    putInt(KEY_ID, account.id)
                    putString(KEY_ACCOUNT_NAME, account.accountName)
                    putString(KEY_SECRET_KEY, account.secretKey)
                })

            }

        }
    }

    private fun setupViewModel(context: Context) {
        val database: OTPDataDatabase = OTPDataDatabase.invoke(context)
        val repository = OTPDataRepository(database)
        val viewModelFactory = ViewModelFactory(repository)
        viewModel =
            ViewModelProvider(
                this,
                viewModelFactory
            )[OTPDataViewModel::class.java]
    }

    private fun renderOTPData(otpData: List<OTPData>) {
        if (otpData.isEmpty()) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvAccounts.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.rvAccounts.visibility = View.VISIBLE
        }
        setOTPData(otpData)
    }

    private fun setOTPData(otpData: List<OTPData>) {
        adapter.submitList(otpData)
    }
}