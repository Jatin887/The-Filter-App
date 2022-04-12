package com.example.totaliycrop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.totaliycrop.adapters.SavedImagesAdapter
import com.example.totaliycrop.databinding.ActivitySavedImageBinding
import com.example.totaliycrop.listeners.SavedImageListener
import com.example.totaliycrop.utilites.displayToast
import com.example.totaliycrop.viewmodels.SavedImagesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class SavedImageActivity : AppCompatActivity(),SavedImageListener {
    private lateinit var savedImagesAdapter: SavedImagesAdapter
    private lateinit var binding: ActivitySavedImageBinding
    private val viewModel: SavedImagesViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObserver()
        setListners()
        viewModel.loadSavedImages()
    }
    private fun setupObserver(){
        viewModel.savedImageUiState.observe(this,{
            val savedImageDataState = it?: return@observe
            binding.savedImagesProgressBar.visibility =
                    if(savedImageDataState.isLoading) View.VISIBLE else View.GONE
            savedImageDataState.savedImages?.let {savedImages->
               savedImagesAdapter = SavedImagesAdapter(this,savedImages,this)
                binding.savedImageRecylerView.layoutManager = GridLayoutManager(this,3)
                binding.savedImageRecylerView.adapter = savedImagesAdapter
                savedImagesAdapter.notifyDataSetChanged()
                binding.savedImageRecylerView.visibility = View.VISIBLE
            }?: run {
                savedImageDataState.error?.let { error->
                    displayToast(error)
                }
            }
        })
    }
    private fun setListners(){
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onImageClicked(file: File) {
        val fileUri = FileProvider.getUriForFile(
                applicationContext,
                "${packageName}.provider",
                file
        )
        Intent(
                applicationContext,
                FilteredImageActivity::class.java
        ).also { filteredImageIntent->
            filteredImageIntent.putExtra(EditActivity.KEY_FILTERED_IMAGE_URI,fileUri)
            startActivity(filteredImageIntent)
        }
    }
}