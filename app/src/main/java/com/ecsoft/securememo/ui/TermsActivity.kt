package com.ecsoft.securememo.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.ecsoft.securememo.R
import com.ecsoft.securememo.SecureStorage

class TermsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        findViewById<Button>(R.id.acceptButton).setOnClickListener {
            SecureStorage.setString(this, "terms_accepted", "true")
            navigateNext()
        }
    }

    private fun navigateNext() {
        // Android 13(TIRAMISU) 미만에서만 외부 저장소 권한이 필요함
        val needsPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        val permissionGranted = if (needsPermission) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (needsPermission && !permissionGranted) {
            startActivity(Intent(this, PermissionActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
