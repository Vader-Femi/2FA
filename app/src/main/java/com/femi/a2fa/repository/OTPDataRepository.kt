package com.femi.a2fa.repository

import androidx.lifecycle.LiveData
import com.femi.a2fa.UserPreferences
import com.femi.a2fa.database.OTPDataDatabase
import com.femi.a2fa.database.entity.OTPData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

class OTPDataRepository(
    private val database: OTPDataDatabase,
    private val dataStore: UserPreferences? = null,
) {

    fun getAllOTPData(): LiveData<List<OTPData>> =
        database.OTPDao().getAllOTPData()

    fun insertOTPData(data: OTPData) {
        database.OTPDao().insertOTPData(data)
    }

    fun deleteOTPData(data: OTPData) {
        database.OTPDao().deleteOTPData(data)
    }

    suspend fun firstTimeUser(): Boolean {
        return dataStore?.firstTimeUser?.first() ?: true
    }

    suspend fun firstTimeUser(status: Boolean) {
        dataStore?.firstTimeUser(status)
    }
}