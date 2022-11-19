package com.femi.a2fa.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.femi.a2fa.R
import com.femi.a2fa.database.OTPDataDatabase
import com.femi.a2fa.database.entity.OTPData
import com.femi.a2fa.databinding.FragmentAddAccountBinding
import com.femi.a2fa.repository.OTPDataRepository
import com.femi.a2fa.viewmodels.OTPDataViewModel
import com.femi.a2fa.viewmodels.ViewModelFactory

class AddAccountFragment : Fragment() {

    private lateinit var binding: FragmentAddAccountBinding
    private lateinit var viewModel: OTPDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentAddAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null && context != null) {
            setupViewModel(requireContext())

            binding.etAccountName.doOnTextChanged { _, _, _, _ ->
                validateSaveButton()
            }

            binding.etSecretKey.doOnTextChanged { _, _, _, _ ->
                validateSaveButton()
            }

            binding.btnSave.setOnClickListener {
                add(
                    binding.etAccountName.text.toString(),
                    binding.etSecretKey.text.toString()
                )
                findNavController().popBackStack()
            }

        }
    }

    private fun validateSaveButton() {

        val inValidAccountName = binding.etAccountName.text.isNullOrEmpty()
        val emptySecretKey = binding.etSecretKey.text.isNullOrEmpty()
        val inValidSecretKey = (binding.etSecretKey.text?.length ?: 0) != 32


        binding.accountNameLayout.helperText =
            if (inValidAccountName) getString(R.string.required) else null

        binding.secretKeyLayout.helperText =
            if (emptySecretKey) getString(R.string.required) else null

        binding.btnSave.isEnabled = !inValidAccountName && !emptySecretKey && !inValidSecretKey
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

    private fun add(accountName: String, secretKey: String) {
        viewModel.insert(
            OTPData(
                accountName = accountName,
                secretKey = secretKey
            )
        )
    }
}