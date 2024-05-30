package com.bangkit2024.educook.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.bangkit2024.educook.R
import com.bangkit2024.educook.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextWatcher(binding.nameEditText, binding.nameEditTextLayout, R.string.username)
        setupTextWatcher(binding.emailEditText, binding.emailEditTextLayout, R.string.email)
        setupTextWatcher(binding.passwordEditText, binding.passwordEditTextLayout, R.string.password)

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 8 && s.isNotEmpty()) {
                    binding.passwordEditTextLayout.error = getString(R.string.password_char)
                } else {
                    binding.passwordEditTextLayout.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    binding.passwordEditTextLayout.error = null
                }
            }
        })

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setupTextWatcher(editText: TextInputEditText, textInputLayout: TextInputLayout, hintResId: Int) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayout.hint = ""
            } else {
                textInputLayout.hint = getString(hintResId)
                if (editText.text.isNullOrEmpty()) {
                    textInputLayout.hint = getString(hintResId)
                } else {
                    textInputLayout.hint = ""
                }
            }
        }
    }
}