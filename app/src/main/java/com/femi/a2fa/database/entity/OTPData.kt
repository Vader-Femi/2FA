package com.femi.a2fa.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "otp_data")
data class OTPData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "accountName")
    val accountName: String,

    @ColumnInfo(name = "secretKey")
    val secretKey: String,

) : Serializable