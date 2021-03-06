package com.example.totaliycrop.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.totaliycrop.databinding.ItemContainerSavedImageBinding
import com.example.totaliycrop.listeners.SavedImageListener
import java.io.File

class SavedImagesAdapter(
        private val context: Context,private val savedImages:List<Pair<File,Bitmap>>,
        private val savedImageListener: SavedImageListener

) : RecyclerView.Adapter<SavedImagesAdapter.SavedImagesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedImagesViewHolder {
        val binding = ItemContainerSavedImageBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
        )
        return SavedImagesViewHolder(binding)
    }
    override fun onBindViewHolder(holder: SavedImagesViewHolder, position: Int) {
        with(holder){
            with(savedImages[position]){
                binding.imageSaved.setImageBitmap(second)
                binding.imageSaved.setOnClickListener {
                    savedImageListener.onImageClicked(first)
                }
            }
        }
    }

    override fun getItemCount() = savedImages.size
    inner class SavedImagesViewHolder(val binding: ItemContainerSavedImageBinding):
            RecyclerView.ViewHolder(binding.root)
}