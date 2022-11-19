package com.femi.a2fa.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.femi.a2fa.database.entity.OTPData
import com.femi.a2fa.repository.OTPDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OTPDataViewModel(private val repository: OTPDataRepository) : ViewModel() {

    fun getData(): LiveData<List<OTPData>> =
        repository.getAllOTPData()

    fun insert(data: OTPData) {
        viewModelScope.launch (Dispatchers.Default) {
            repository.insertOTPData(data)
        }
    }

    fun delete(data: OTPData) {
        viewModelScope.launch (Dispatchers.Default) {
            repository.deleteOTPData(data)
        }
    }

    suspend fun firstTimeUser(): Boolean {
        return repository.firstTimeUser()
    }

    fun firstTimeUser(status: Boolean) {
        viewModelScope.launch (Dispatchers.Default) {
            repository.firstTimeUser(status)
        }
    }
}