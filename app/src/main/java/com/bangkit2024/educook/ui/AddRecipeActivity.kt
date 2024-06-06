package com.bangkit2024.educook.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit2024.educook.R
import com.bangkit2024.educook.databinding.ActivityAddRecipeBinding
import com.bangkit2024.educook.ui.nav_activity.HomeActivity
import com.bangkit2024.educook.util.reduceFileImage
import com.bangkit2024.educook.util.uriToFile
import com.bangkit2024.educook.viewmodel.AddViewModel
import com.bangkit2024.educook.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRecipeBinding
    private val viewModel by viewModels<AddViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            ibArrowBack.setOnClickListener { finish() }
            ivInsertPhoto.setOnClickListener { startGallery() }
            uploadButton.setOnClickListener { uploadRecipe() }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivInsertPhoto.setImageURI(it)
        }
    }

    private fun uploadRecipe() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            binding.progressBar.visibility = View.VISIBLE

            val requestBody = binding.etIngredient.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            lifecycleScope.launch {
                viewModel.getToken().collect {token ->
                    if (token.isNotEmpty()){
                        viewModel.addRecipe(token, multipartBody, requestBody).observe(this@AddRecipeActivity){ result ->
                            binding.progressBar.visibility = View.GONE
                            result.onSuccess {
                                Toast.makeText(this@AddRecipeActivity, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@AddRecipeActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            result.onFailure {error ->
                                Toast.makeText(this@AddRecipeActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}