package com.femi.a2fa

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.femi.a2fa.databinding.ActivityMainBinding
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex
import kotlinx.coroutines.*
import java.security.SecureRandom
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val progress = MutableLiveData(-1)
//    private lateinit var getProgressJob: Job

//    https://medium.com/@ihorsokolyk/two-factor-authentication-with-java-and-google-authenticator-9d7ea15ffee6
//    https://github.com/taimos/totp/blob/master/src/main/java/de/taimos/totp/TOTP.java

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        newKey()

        if (!getProgress().isActive)
            getProgress()

        progress.observe(this) {
            ObjectAnimator.ofInt(binding.remainingTimeBar, "progress", it)
                .setDuration(1000)
                .start()
            getTOTPCode()
            when {
                (it in 0..49) -> binding.remainingTimeBar.trackColor = getColor(R.color.green)
                (it in 49..75) -> binding.remainingTimeBar.trackColor = getColor(R.color.yellow)
                (it in 75..100) -> binding.remainingTimeBar.trackColor = getColor(R.color.red)
            }
        }
        val cm: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        binding.secretKey.setOnClickListener {
            val cData = ClipData.newPlainText("Secret Key", binding.secretKey.text)
            cm.setPrimaryClip(cData)
            Toast.makeText(this, "Copied key to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.secretOTP.setOnClickListener {
            val cData = ClipData.newPlainText("Secret OTP", binding.secretOTP.text)
            cm.setPrimaryClip(cData)
            Toast.makeText(this, "Copied OTP to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.fab.setOnClickListener {
            newKey()
        }
    }

    private fun getProgress() = lifecycleScope.launch {
        while (isActive) {
            progress.value = (((30 - TOTP.getTimeRemaining()) * 3.33).roundToInt())
            delay(1000)
        }
    }

    private fun newKey() {
        generateSecretKey()
        getTOTPCode()
    }

    private fun generateSecretKey() {
        val random = SecureRandom()
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        val base32 = Base32()
        val secretKey = base32.encodeToString(bytes)
        binding.secretKey.text = secretKey
    }

    private fun getTOTPCode() {
        val secretKey = binding.secretKey.text.toString()
        val base32 = Base32()
        val bytes = base32.decode(secretKey)
        val hexKey = Hex.encodeHexString(bytes)
        val otp = TOTP.getOTP(hexKey)
        binding.secretOTP.text = otp
    }

}