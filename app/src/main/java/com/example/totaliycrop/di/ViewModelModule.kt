package com.example.totaliycrop.di

import com.example.totaliycrop.viewmodels.EditImageViewModel
import com.example.totaliycrop.viewmodels.SavedImagesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EditImageViewModel(editImageRepository = get())}
    viewModel { SavedImagesViewModel(savedImageRepository =get())}
}