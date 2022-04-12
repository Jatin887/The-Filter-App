package com.example.totaliycrop.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.example.totaliycrop.repositories.SavedImageRepository
import com.example.totaliycrop.utilites.Coroutines
import java.io.File

class SavedImagesViewModel(private val savedImageRepository: SavedImageRepository):ViewModel() {
    private val savedImageDataState = MutableLiveData<SavedImageDataState>()
    val savedImageUiState :LiveData<SavedImageDataState>get() = savedImageDataState
    fun loadSavedImages(){
        Coroutines.io{
            runCatching {
                emitSaveImagesUiState(isLoading = true)
                savedImageRepository.loadSaveImages()
            }.onSuccess {savedImages->
                if(savedImages.isNullOrEmpty()){
                    emitSaveImagesUiState(error = "No image found")
                }
                else{
                    emitSaveImagesUiState(savedImages = savedImages)
                }
            }.onFailure {
                emitSaveImagesUiState(error = it.message.toString())
            }
        }
    }
    private fun emitSaveImagesUiState(
            isLoading:Boolean = false,
            savedImages: List<Pair<File, Bitmap>>? = null,
            error: String? = null
    ){
        val dataState = SavedImageDataState(isLoading,savedImages,error)
        savedImageDataState.postValue(dataState)
    }
    data class SavedImageDataState(
            val isLoading :Boolean,
            val savedImages: List<Pair<File,Bitmap>>?,
            val error: String?
    )
}