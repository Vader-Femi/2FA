package com.femi.a2fa

import java.lang.reflect.UndeclaredThrowableException
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object TOTP {
    /**
     * @param key - secret credential key (HEX)
     * @return the OTP
     */
    fun getOTP(key: String): String {
        return getOTP(step, key)
    }

    /**
     * @param key - secret credential key (HEX)
     * @param otp - OTP to validate
     * @return valid?
     */
    fun validate(key: String, otp: String): Boolean {
        return validate(step, key, otp)
    }

    private fun validate(step: Long, key: String, otp: String): Boolean {
        return getOTP(step, key) == otp || getOTP(step - 1, key) == otp
    }

    // 30 seconds stepize (ID TOTP)
    private val step: Long
        get() =// 30 seconds stepize (ID TOTP)
            System.currentTimeMillis() / 30000

    private fun getOTP(step: Long, key: String): String {
        var step = java.lang.Long.toHexString(step).uppercase(Locale.getDefault())
        while (step.length < 16) {
            step = "0$step"
        }

        // Get the HEX in a Byte[]
        val msg: ByteArray = hexStr2Bytes(step)
        val k: ByteArray = hexStr2Bytes(key)
        val hash: ByteArray = hmac_sha1(k, msg)

        // put selected bytes into result int
        val offset: Int = hash[hash.size - 1].toInt() and  0xf
        val binary: Int =
            hash[offset] and 0x7f shl 24 or (hash[offset + 1] and 0xff shl 16) or (hash[offset + 2] and 0xff shl 8) or (hash[offset + 3] and 0xff)
        val otp = binary % 1000000
        var result = otp.toString()
        while (result.length < 6) {
            result = "0$result"
        }
        return result
    }

    fun getTimeRemaining(): Long {
//        (current_time / time_block) - (totp_now_time / time_block)
        return (30 - (System.currentTimeMillis() / 1000 ) % 30)
    }

    /**
     * This method converts HEX string to Byte[]
     *
     * @param hex the HEX string
     *
     * @return A byte array
     */
    private fun hexStr2Bytes(hex: String): ByteArray {
        // Adding one byte to get the right conversion
        // values starting with "0" can be converted
        val bArray: ByteArray = BigInteger("10$hex", 16).toByteArray()
        val ret = ByteArray(bArray.size - 1)

        // Copy all the REAL bytes, not the "first"
        System.arraycopy(bArray, 1, ret, 0, ret.size)
        return ret
    }

    /**
     * This method uses the JCE to provide the crypto algorithm. HMAC computes a Hashed Message Authentication Code with the crypto hash
     * algorithm as a parameter.
     *
     * @param crypto the crypto algorithm (HmacSHA1, HmacSHA256, HmacSHA512)
     * @param keyBytes the bytes to use for the HMAC key
     * @param text the message or text to be authenticated.
     */
    private fun hmac_sha1(keyBytes: ByteArray, text: ByteArray): ByteArray {
        return try {
            val hmac: Mac = Mac.getInstance("HmacSHA1")
            val macKey = SecretKeySpec(keyBytes, "RAW")
            hmac.init(macKey)
            hmac.doFinal(text)
        } catch (gse: GeneralSecurityException) {
            throw UndeclaredThrowableException(gse)
        }
    }

}

infix fun Byte.shl(that: Int): Int = this.toInt().shl(that)
infix fun Int.shl(that: Byte): Int = this.shl(that.toInt()) // Not necessary in this case because no there's (Int shl Byte)
infix fun Byte.shl(that: Byte): Int = this.toInt().shl(that.toInt()) // Not necessary in this case because no there's (Byte shl Byte)

infix fun Byte.and(that: Int): Int = this.toInt().and(that)
infix fun Int.and(that: Byte): Int = this.and(that.toInt()) // Not necessary in this case because no there's (Int and Byte)
infix fun Byte.and(that: Byte): Int = this.toInt().and(that.toInt()) // Not necessary in this case because no there's (Byte and Byte)