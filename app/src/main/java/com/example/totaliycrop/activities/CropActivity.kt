package com.example.totaliycrop.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.totaliycrop.HomeActivity
import com.example.totaliycrop.R
import com.example.totaliycrop.databinding.ActivityCameraBinding
import com.example.totaliycrop.viewmodels.EditImageViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.jar.Manifest

class CropActivity : AppCompatActivity() {
    private lateinit var uri :Uri
    private val cameraRequestCode = 111
    private val viewModel:EditImageViewModel by viewModel()
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    private lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.openCamera.isEnabled = false
        observers()
        launchers()
        setListeners()
        openCameraFunction()

    }
    private fun openCameraFunction(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),cameraRequestCode)
        else
            binding.openCamera.isEnabled = true
    }
    private fun setListeners(){
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
        binding.openCamera.setOnClickListener{
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,cameraRequestCode)
        }
        binding.undoButton.setOnClickListener { viewModel.undoFunction() }
        binding.selectImageButton.setOnClickListener { selectImageFromGallery() }
        binding.rotateImageButton.setOnClickListener { viewModel.rotateImage() }
        binding.cropImageButton.setOnClickListener { if (viewModel.getTaskLength() > 0) cropActivityResultLauncher.launch(null) }
        binding.saveButton.setOnClickListener {
            viewModel.saveCropImage(baseContext)
            makeToast()
        }
    }
    private val cropActivityResultContract = object : ActivityResultContract<Any?,Bitmap?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return createCropIntent(uri)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
            return intent?.extras?.getParcelable("data")!!
        }
    }

    private val selectImageFromGalleryResultContract = registerForActivityResult(ActivityResultContracts.GetContent()) { _uri: Uri? ->
        _uri?.let {
            uri = _uri
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            viewModel.selectImage(bitmap)
        }
    }
    private fun launchers() {
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { viewModel.cropImage(it) }
        }

        viewModel.imageBitmap.observe(this, {
            binding.ImagePreview.setImageBitmap(it)
        })
    }

    private fun observers() {
        viewModel.imageBitmap.observe(this, {
            binding.ImagePreview.setImageBitmap(it)
        })
    }

    private fun makeToast() {
        Toast.makeText(this,"Image Saved",Toast.LENGTH_LONG).show()
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResultContract.launch("image/*")
    private fun createCropIntent(uri : Uri) : Intent {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(uri, "image/*")
        cropIntent.putExtra("crop", true)
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("outputX", 128)
        cropIntent.putExtra("outputY", 128)
        cropIntent.putExtra("return-data", true)
        return cropIntent
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == cameraRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            binding.openCamera.isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == cameraRequestCode){
            var image = data!!.getParcelableExtra<Bitmap>("data")
            binding.ImagePreview.setImageBitmap(image)
        }
    }
}