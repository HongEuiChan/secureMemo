package com.ecsoft.securememo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ecsoft.securememo.R
import com.ecsoft.securememo.SecureStorage

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        findViewById<Button>(R.id.acceptButton).setOnClickListener {
            SecureStorage.setString(this, "terms_accepted", "true")
            startActivity(Intent(this, PermissionActivity::class.java))
            finish()
        }
    }
}
