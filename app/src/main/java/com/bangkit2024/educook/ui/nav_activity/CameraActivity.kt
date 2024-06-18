package com.bangkit2024.educook.ui.nav_activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bangkit2024.educook.R
import com.bangkit2024.educook.databinding.ActivityCameraBinding
import com.bangkit2024.educook.helper.ImageClassifierHelper
import com.bangkit2024.educook.util.getImageUri
import com.bangkit2024.educook.util.reduceFileImage
import com.bangkit2024.educook.util.uriToFile
import org.tensorflow.lite.task.vision.classifier.Classifications

class CameraActivity : Fragment() {
    private lateinit var binding: ActivityCameraBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ActivityCameraBinding.inflate(inflater, container, false)
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

    private fun predictImage(){
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image Classification File", "showImage: ${imageFile.path}")
            showLoading(true)
            imageClassifierHelper = ImageClassifierHelper(
                context = requireContext(),
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                        showLoading(false)
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        showLoading(false)
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                val topCategory = it[0].categories.maxByOrNull { it.score }
                                val prediction = topCategory?.label
                                binding.tvPredict.text = prediction
                            }
                        }
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(Uri.fromFile(imageFile))
        } ?: run {
            showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivInsertPhoto.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
