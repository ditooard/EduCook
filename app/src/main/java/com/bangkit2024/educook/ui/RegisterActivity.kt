package com.bangkit2024.educook.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit2024.educook.R
import com.bangkit2024.educook.databinding.ActivityRegisterBinding
import com.bangkit2024.educook.viewmodel.RegisterViewModel
import com.bangkit2024.educook.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextInput(binding.nameEditText, binding.nameEditTextLayout, R.string.username)
        setupTextInput(binding.emailEditText, binding.emailEditTextLayout, R.string.email)
        setupTextInput(binding.passwordEditText, binding.passwordEditTextLayout, R.string.password)

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        setValidatePassword()
        setupAction()
    }

    private fun setupTextInput(
        editText: TextInputEditText,
        textInputLayout: TextInputLayout,
        hintResId: Int
    ) {
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

    private fun setValidatePassword() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 6 && s.isNotEmpty()) {
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
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                viewModel.register(name, email, password).observe(this@RegisterActivity) { result ->
                    binding.progressBar.visibility = View.GONE
                    result.onSuccess {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.register_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.onFailure { e ->
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}