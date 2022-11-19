package com.femi.a2fa

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.femi.a2fa.database.OTPDataDatabase
import com.femi.a2fa.database.entity.OTPData
import com.femi.a2fa.databinding.ActivityMainBinding
import com.femi.a2fa.repository.OTPDataRepository
import com.femi.a2fa.viewmodels.OTPDataViewModel
import com.femi.a2fa.viewmodels.ViewModelFactory
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base32
import kotlinx.coroutines.*
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: OTPDataViewModel

//    https://medium.com/@ihorsokolyk/two-factor-authentication-with-java-and-google-authenticator-9d7ea15ffee6
//    https://github.com/taimos/totp/blob/master/src/main/java/de/taimos/totp/TOTP.java

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel(this)

        lifecycleScope.launch {
            if (viewModel.firstTimeUser()) {

                viewModel.insert(
                    OTPData(
                        accountName = "Random Account",
                        secretKey = generateSecretKey()
                    )
                )

                viewModel.firstTimeUser(false)
            }
        }
    }

    private fun setupViewModel(context: Context) {
        val database: OTPDataDatabase = OTPDataDatabase.invoke(context)
        val preferences = UserPreferences(this)
        val repository = OTPDataRepository(database, preferences)
        val viewModelFactory = ViewModelFactory(repository)
        viewModel =
            ViewModelProvider(
                this,
                viewModelFactory
            )[OTPDataViewModel::class.java]
    }

    private fun generateSecretKey(): String {
        val random = SecureRandom()
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        val base32 = Base32()
        return base32.encodeToString(bytes)
    }

}