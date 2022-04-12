package com.example.totaliycrop.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.totaliycrop.data.ImageFilter
import com.example.totaliycrop.repositories.EditImageRepository
import com.example.totaliycrop.utilites.Coroutines
import com.example.totaliycrop.utilites.EditFunction
import com.example.totaliycrop.utilites.Tasks

class EditImageViewModel(private val editImageRepository: EditImageRepository):ViewModel() {
    private val imagePreviewDataState = MutableLiveData<ImagePreviewDataState>()
    val imagePreviewUiState:LiveData<ImagePreviewDataState> get() = imagePreviewDataState
    private  val tasks:ArrayList<Task> = ArrayList()
    private val imageState = MutableLiveData<Bitmap>()
    val imageBitmap : LiveData<Bitmap> get() = imageState

    fun prepareImagePreview(imageUri: Uri){
        Coroutines.io {
            runCatching {
                emitImagePreviewUiState(isLoading = true)
                editImageRepository.prepareImagePreview(imageUri)
            }.onSuccess {bitmap->
                if(bitmap!=null){
                    emitImagePreviewUiState(bitmap = bitmap)
                }else{
                    emitImagePreviewUiState(error = "Unable to prepare image preview")
                }
            }.onFailure {
                emitImagePreviewUiState(error = it.message.toString())
            }
        }
    }
    private fun emitImagePreviewUiState(
            isLoading: Boolean = false,
            bitmap: Bitmap? = null,
            error: String? = null
    ){
        val dataState = ImagePreviewDataState(isLoading,bitmap,error)
        imagePreviewDataState.postValue(dataState)
    }
    data class ImagePreviewDataState(
            val isLoading:Boolean,
            val bitmap:Bitmap?,
            val error:String?
    )

    // Load image filters
    private val imageFiltersDataState = MutableLiveData<ImageFiltersDataState>()
    val imageFilterUiState: LiveData<ImageFiltersDataState>get() = imageFiltersDataState
    fun loadImageFilters(originalImage: Bitmap){
        Coroutines.io{
            runCatching {
                emitImageFilterUiState(isLoading = true)
                editImageRepository.getImageFilters(getPreviewImage(originalImage))
            }.onSuccess { imageFilters->
                emitImageFilterUiState(imageFilters = imageFilters)
            }.onFailure {
                emitImageFilterUiState(error = it.message.toString())
            }
        }
    }
    private fun getPreviewImage(originalImage:Bitmap):Bitmap{
        return kotlin.runCatching {
            val previewWidth = 150
            val previewHeight = originalImage.height*previewWidth/originalImage.width
            Bitmap.createScaledBitmap(originalImage,previewWidth,previewHeight,false)
        }.getOrDefault(originalImage)
    }

    private fun emitImageFilterUiState(
            isLoading: Boolean = false,
            imageFilters: List<ImageFilter>? = null,
            error: String? = null
    ){
        val dataState = ImageFiltersDataState(isLoading,imageFilters,error)
        imageFiltersDataState.postValue(dataState)
    }
    data class ImageFiltersDataState(
            val isLoading: Boolean,
            val imageFilters: List<ImageFilter>?,
            val error: String?
    )
    private val saveFilteredImageDataState = MutableLiveData<SaveFilteredImageDataState>()
    val saveFilteredImageUiState:LiveData<SaveFilteredImageDataState>get() = saveFilteredImageDataState

    fun saveFilteredImage(filteredBitmap:Bitmap){
        Coroutines.io{
            runCatching {
                emitSaveFilteredImageUiState(isLoading = true)
                editImageRepository.saveFilteredImage(filteredBitmap)

            }.onSuccess { savedImageUri->
                emitSaveFilteredImageUiState(uri = savedImageUri)
            }.onFailure {
                emitSaveFilteredImageUiState(error = it.message.toString())
            }
        }
    }

    private fun emitSaveFilteredImageUiState(
            isLoading: Boolean = false,
            uri: Uri? = null,
            error: String? = null
    ){
        val dataState = SaveFilteredImageDataState(isLoading,uri,error)
        saveFilteredImageDataState.postValue(dataState)
    }
    data class SaveFilteredImageDataState(
            val isLoading: Boolean,
            val uri: Uri?,
            val error: String?

    )


    data class Task(
            val name:String,
            val image: Bitmap
    )


    fun cropImage(image: Bitmap?) {
        if(tasks.size > 0){
            tasks.add(Task(Tasks.CROP,imageState.value!!))
            imageState.value = image
        }
    }
    fun getTaskLength() = tasks.size
    fun rotateImage() {
        if(tasks.size > 0) {
            tasks.add(Task(Tasks.ROTATE, imageState.value!!))
            imageState.value = EditFunction.rotateFunc(imageState.value!!)
        }
    }
    fun undoFunction() {
        if (tasks.size > 1) imageState.value = tasks.removeLast().image
    }

    fun saveCropImage(context: Context){
        if(tasks.size > 0) EditFunction.saveImage(imageState.value!!,context)
    }
    fun selectImage(image:Bitmap?){
        tasks.clear()
        tasks.add(Task(Tasks.SELECT,image!!))
        imageState.value = image

    }

}