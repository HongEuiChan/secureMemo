package com.ecsoft.securememo.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ecsoft.securememo.R
import com.ecsoft.securememo.SecureStorage

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // Show intro for 1.5 seconds then navigate
        Handler(Looper.getMainLooper()).postDelayed({
            navigateNext()
        }, 1500)
    }

    private fun navigateNext() {
        val termsAccepted = SecureStorage.getString(this, "terms_accepted") == "true"
        if (!termsAccepted) {
            startActivity(Intent(this, TermsActivity::class.java))
            finish()
            return
        }

        val permissionGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            startActivity(Intent(this, PermissionActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
