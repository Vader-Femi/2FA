package com.femi.a2fa.ui

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.femi.a2fa.R
import com.femi.a2fa.TOTP
import com.femi.a2fa.database.OTPDataDatabase
import com.femi.a2fa.database.entity.OTPData
import com.femi.a2fa.databinding.FragmentOTPBinding
import com.femi.a2fa.repository.OTPDataRepository
import com.femi.a2fa.utils.KEY_ACCOUNT_NAME
import com.femi.a2fa.utils.KEY_ID
import com.femi.a2fa.utils.KEY_SECRET_KEY
import com.femi.a2fa.viewmodels.OTPDataViewModel
import com.femi.a2fa.viewmodels.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding
    private lateinit var viewModel: OTPDataViewModel

    private val progress = MutableLiveData(-1)
    var accountId = -1
    private lateinit var accountName: String
    private lateinit var secretKey: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOTPBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null && context != null) {
            setupViewModel(requireContext())

            accountId = requireArguments().getInt(KEY_ID, -1)
            accountName = requireArguments().getString(KEY_ACCOUNT_NAME, "")
            secretKey = requireArguments().getString(KEY_SECRET_KEY, "")

            getTOTPCode()

            if (!getProgress().isActive)
                getProgress()


            progress.observe(requireActivity()) {
                when {
                    (it in 0..49) -> binding.remainingTimeBar.trackColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                    (it in 49..75) -> binding.remainingTimeBar.trackColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow)
                    (it in 75..100) -> binding.remainingTimeBar.trackColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.red)
                }
                ObjectAnimator.ofInt(binding.remainingTimeBar, "progress", it)
                    .setDuration(1000)
                    .start()
                getTOTPCode()
            }
            val cm: ClipboardManager =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            binding.btnCopyPassword.setOnClickListener {
                val cData = ClipData.newPlainText(requireContext().getString(R.string.one_time_password), binding.tvOTP.text)
                cm.setPrimaryClip(cData)
                Toast.makeText(requireContext(), getString(R.string.password_copied_to_clipboard), Toast.LENGTH_SHORT).show()
            }

            binding.fabDelete.setOnClickListener {
                deleteConfirmation()
            }
        }
    }

    private fun getProgress() = viewLifecycleOwner.lifecycleScope.launch {
        while (isActive) {
            progress.value = (((30 - TOTP.getTimeRemaining()) * 3.33).roundToInt())
            delay(1000)
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

    private fun getTOTPCode() {
        val base32 = Base32()
        val bytes = base32.decode(secretKey)
        val hexKey = Hex.encodeHexString(bytes)
        val otp = TOTP.getOTP(hexKey)
        binding.tvOTP.text = otp
    }

    private fun deleteConfirmation(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this account.\n\nThis cannot be undone?")
            .setPositiveButton("Pretty Sure"){ _,_ ->
                deleteAccount()
                findNavController().popBackStack()
            }
            .setNeutralButton("Never Mind"){_,_ ->

            }.show()
    }

    private fun deleteAccount() {
        viewModel.delete(
            OTPData(
                accountId,
                accountName,
                secretKey
            )
        )
    }
}
