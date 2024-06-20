package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bangkit2024.educook.R
import com.bangkit2024.educook.databinding.ActivityCameraBinding
import com.bangkit2024.educook.ui.RecommendationActivity
import com.bangkit2024.educook.util.getImageUri
import com.bangkit2024.educook.util.reduceFileImage
import com.bangkit2024.educook.util.uriToFile
import com.bangkit2024.educook.viewmodel.RecommendViewModel
import com.bangkit2024.educook.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.net.SocketTimeoutException

class CameraActivity : Fragment() {
    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<RecommendViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ActivityCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            btnRecommend.setOnClickListener { predictImage() }
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

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun predictImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "Image Path: ${imageFile.path}")
            val requestImageFile = imageFile.asRequestBody("image/*".toMediaType())
            val image = MultipartBody.Part.createFormData(
                "gambar",
                imageFile.name,
                requestImageFile
            )
            showLoading(true)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.predictImage(image).observe(viewLifecycleOwner) { result ->
                    showLoading(false)
                    result.onSuccess { response ->
                        val prediction = response.dicari
                        navigateToRecommendation(prediction)
                    }.onFailure { error ->
                        if (error is SocketTimeoutException) {
                            showToast("Request timed out. Please try again.")
                        } else {
                            showToast("Prediction failed: ${error.message}")
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun navigateToRecommendation(prediction: String) {
        val intent = Intent(requireContext(), RecommendationActivity::class.java).apply {
            putExtra(EXTRA_PREDICT, prediction)
        }
        startActivity(intent)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "Image URI: $it")
            binding.ivInsertPhoto.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_PREDICT = "EXTRA_PREDICTION"
    }
}
