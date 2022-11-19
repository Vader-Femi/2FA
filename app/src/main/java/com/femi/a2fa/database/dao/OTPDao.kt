package com.femi.a2fa.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.femi.a2fa.database.entity.OTPData

@Dao
interface OTPDao {

    @Query("SELECT * FROM otp_data ORDER BY accountName ASC")
    fun getAllOTPData(): LiveData<List<OTPData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOTPData(data: OTPData)

//    @Update
//    fun update(date: OTPData)

    @Delete
    fun deleteOTPData(data: OTPData)
}
