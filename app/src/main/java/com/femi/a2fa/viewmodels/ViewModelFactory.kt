package com.femi.a2fa.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.femi.a2fa.repository.OTPDataRepository

class ViewModelFactory(private val repository: OTPDataRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(OTPDataViewModel::class.java) -> OTPDataViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}