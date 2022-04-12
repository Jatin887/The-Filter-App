package com.example.totaliycrop.repositories

import android.graphics.Bitmap
import java.io.File

interface SavedImageRepository {
    suspend fun loadSaveImages():List<Pair<File,Bitmap>>?

}