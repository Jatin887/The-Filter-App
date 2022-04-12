package com.example.totaliycrop.activities

import android.R
import android.R.attr.path
import android.R.attr.visibility
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.totaliycrop.HomeActivity
import com.example.totaliycrop.adapters.ImageFilterAdapter
import com.example.totaliycrop.data.ImageFilter
import com.example.totaliycrop.databinding.ActivityEditBinding
import com.example.totaliycrop.listeners.ImageFilterListener
import com.example.totaliycrop.utilites.displayToast
import com.example.totaliycrop.utilites.show
import com.example.totaliycrop.viewmodels.EditImageViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import jp.co.cyberagent.android.gpuimage.GPUImage
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditActivity:AppCompatActivity(),ImageFilterListener {
    companion object{
        const val KEY_FILTERED_IMAGE_URI = "filteredImageUri"

    }
    private lateinit var binding: ActivityEditBinding
    lateinit var filterAdapter: ImageFilterAdapter
    private lateinit var gpuImage:GPUImage
    private lateinit var originalBitmap:Bitmap
    private val filteredBitmap = MutableLiveData<Bitmap>()
    private val viewModel:EditImageViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setListeners()
        setupObservers()
        prepareImagePreview()
    }

    private fun setupObservers(){
        viewModel.imagePreviewUiState.observe(this, {
            val dataState = it ?: return@observe
            binding.previewProgressBar.visibility =
                if (dataState.isLoading) View.VISIBLE else View.GONE
            dataState.bitmap?.let { bitmap ->
                originalBitmap = bitmap
                filteredBitmap.value = bitmap
                with(originalBitmap) {
                    gpuImage.setImage(this)
                    binding.ImagePreview.setImageBitmap(bitmap)
                    binding.ImagePreview.show()
                    viewModel.loadImageFilters(this)
                }
            } ?: kotlin.run {
                dataState.error?.let { error ->
                    displayToast(error)
                }
            }
        })
        viewModel.imageFilterUiState.observe(this, {
            val imageFiltersDataState = it ?: return@observe
            binding.imageFiltersProgressBar.visibility =
                if (imageFiltersDataState.isLoading) View.VISIBLE else View.GONE
            imageFiltersDataState.imageFilters?.let { imageFilters ->
                filterAdapter = ImageFilterAdapter(this, imageFilters, this)
                binding.filtersRecylerView.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                binding.filtersRecylerView.adapter = filterAdapter
                filterAdapter.notifyDataSetChanged()

            } ?: kotlin.run {
                imageFiltersDataState.error?.let { error ->
                    displayToast(error)

                }
            }
        })
        filteredBitmap.observe(this, { bitmap ->
            binding.ImagePreview.setImageBitmap(bitmap)
        })
        viewModel.saveFilteredImageUiState.observe(this, {
            val saveFilteredImageDataState = it ?: return@observe
            if (saveFilteredImageDataState.isLoading) {
                binding.imageSave.visibility = View.GONE
                binding.savingProgressBar.visibility = View.VISIBLE
            } else {
                binding.savingProgressBar.visibility = View.GONE
                binding.imageSave.visibility = View.VISIBLE
            }
            saveFilteredImageDataState.uri?.let { savedImageUri ->
                Intent(
                    applicationContext,
                    FilteredImageActivity::class.java
                ).also { filteredImageIntent ->
                    filteredImageIntent.putExtra(KEY_FILTERED_IMAGE_URI, savedImageUri)
                    startActivity((filteredImageIntent))
                }
            } ?: kotlin.run {
                saveFilteredImageDataState.error?.let { error ->
                    displayToast(error)
                }
            }
        })
    }
    private fun prepareImagePreview(){
        gpuImage = GPUImage(applicationContext)
        intent.getParcelableExtra<Uri>(HomeActivity.KEY_IMAGE_URI)?.let{ imageUri ->
            viewModel.prepareImagePreview(imageUri)
        }
        intent.getParcelableExtra<Uri>(HomeActivity.KEY_CAMERA)?.let { imageUri->
            launchImageCrop(imageUri)
            setImage(imageUri)
        }


    }
    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(binding.ImagePreview)
    }


    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
        binding.imageSave.setOnClickListener {
            filteredBitmap.value?.let { bitmap ->
                viewModel.saveFilteredImage(bitmap)
            }
        }
        binding.ImagePreview.setOnLongClickListener {
            binding.ImagePreview.setImageBitmap(originalBitmap)
            return@setOnLongClickListener false
        }
        binding.ImagePreview.setOnClickListener {
            binding.ImagePreview.setImageBitmap(filteredBitmap.value)
        }
    }

    override fun onFilterSelected(imageFilter: ImageFilter) {
        with(imageFilter){
            with(gpuImage){
                setFilter(filter)
                filteredBitmap.value = bitmapWithFilterApplied
            }
        }
    }
    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1920, 1080)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(this)
    }
}
