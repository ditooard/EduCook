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
import com.bangkit2024.educook.databinding.ActivityLoginBinding
import com.bangkit2024.educook.viewmodel.LoginViewModel
import com.bangkit2024.educook.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextInput(binding.emailEditText, binding.emailEditTextLayout, R.string.email)
        setupTextInput(binding.passwordEditText, binding.passwordEditTextLayout, R.string.password)

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val isLogout = intent.getBooleanExtra(EXTRA_LOGOUT_FLAG, false)
        if (isLogout) {
            finish()
            return
        }

        setValidatePassword()
        setupAction()
    }

    private fun setupTextInput(editText: TextInputEditText, textInputLayout: TextInputLayout, hintResId: Int) {
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

    private fun setValidatePassword(){
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
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                viewModel.login(email, password).observe(this@LoginActivity) { result ->
                    binding.progressBar.visibility = View.GONE
                    result.onSuccess { response ->
                        response.data.token.let {token ->
                            viewModel.saveToken(token)
                            Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }.onFailure { e ->
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_LOGOUT_FLAG = "EXTRA_LOGOUT_FLAG"
    }
}