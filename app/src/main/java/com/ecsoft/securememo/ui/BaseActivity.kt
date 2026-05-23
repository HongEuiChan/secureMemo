package com.ecsoft.securememo.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ecsoft.securememo.R

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupWindowInsets()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        setupWindowInsets()
    }

    private fun setupWindowInsets() {
        val rootView = findViewById<View>(R.id.main)
        if (rootView != null) {
            // 배경이 흰색이므로 상태바 아이콘을 어두운 색으로 설정
            WindowInsetsControllerCompat(window, rootView).isAppearanceLightStatusBars = true

            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}
