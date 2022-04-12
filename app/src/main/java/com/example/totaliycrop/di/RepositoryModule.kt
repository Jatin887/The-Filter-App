package com.example.totaliycrop.di

import com.example.totaliycrop.repositories.EditImageRepository
import com.example.totaliycrop.repositories.EditImageRepositoryImpl
import com.example.totaliycrop.repositories.SavedImageRepository
import com.example.totaliycrop.repositories.SavedImageRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<EditImageRepository>{EditImageRepositoryImpl(androidContext())}
    factory<SavedImageRepository>{SavedImageRepositoryImpl(androidContext())}
}