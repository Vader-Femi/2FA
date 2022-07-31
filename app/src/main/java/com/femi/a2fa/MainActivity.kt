package com.femi.a2fa

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.femi.a2fa.databinding.ActivityMainBinding
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex
import java.security.SecureRandom


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    https://medium.com/@ihorsokolyk/two-factor-authentication-with-java-and-google-authenticator-9d7ea15ffee6
//    https://github.com/taimos/totp/blob/master/src/main/java/de/taimos/totp/TOTP.java

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        getTOTPCode(generateSecretKey())

        binding.secretKey.setOnClickListener {
            val cm: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val cData = ClipData.newPlainText("Secret Key", binding.secretKey.text)
            cm.setPrimaryClip(cData)
            Toast.makeText(this, "Copied key to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.secretOTP.setOnClickListener {
            val cm: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val cData = ClipData.newPlainText("Secret OTP", binding.secretOTP.text)
            cm.setPrimaryClip(cData)
            Toast.makeText(this, "Copied OTP to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.fab.setOnClickListener {
            getTOTPCode(binding.secretKey.text.toString())
        }
    }

    private fun generateSecretKey(): String? {
        val random = SecureRandom()
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        val base32 = Base32()
        val secretKey = base32.encodeToString(bytes)
        binding.secretKey.text = secretKey
        return secretKey
    }

    private fun getTOTPCode(secretKey: String?): String {
        val base32 = Base32()
        val bytes = base32.decode(secretKey)
        val hexKey = Hex.encodeHexString(bytes)
        val otp = TOTP.getOTP(hexKey)
        Log.e("SecretKey", secretKey.toString())
        Log.e("SecretOTP", otp)
        binding.secretOTP.text = otp
        return otp
    }

}