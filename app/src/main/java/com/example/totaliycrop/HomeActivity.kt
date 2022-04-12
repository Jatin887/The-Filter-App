package com.example.totaliycrop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.example.totaliycrop.activities.CropActivity
import com.example.totaliycrop.activities.EditActivity
import com.example.totaliycrop.activities.SavedImageActivity
import com.example.totaliycrop.databinding.ActivityHomeBinding
import com.theartofdev.edmodo.cropper.CropImage


class HomeActivity : AppCompatActivity() {

//    private val cropActivityResultContracts  = object :
//        ActivityResultContract<Any,Uri?>() {
//        override fun createIntent(context: Context, input: Any?): Intent {
//            return CropImage.activity()
//                .setAspectRatio(16, 9)
//                .getIntent(this@HomeActivity)
//
//        }

//        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
//            return CropImage.getActivityResult(intent)?.uri
//        }
//    }
//    private lateinit var cropActivityResultLauncher : ActivityResultLauncher<Any?>

    companion object{
        private const val REQUEST_CODE_PICK_IMAGE = 1
        const val KEY_IMAGE_URI = "imageUri"
        private const val cameraRequestId = 2
        const val KEY_CAMERA = "imageUrl"
    }

    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setListeners()

    }
    private fun setListeners() {
        binding.buttonEditImage.setOnClickListener{
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).also { pickerIntent->
                pickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(pickerIntent, REQUEST_CODE_PICK_IMAGE)
            }
        }
        binding.buttonSaveImage.setOnClickListener {
            Intent(applicationContext, SavedImageActivity::class.java).also{
                startActivity(it)
            }
        }
        binding.buttonOpenCamera.setOnClickListener{
            val intent = Intent(this,CropActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK){
            data?.data?.let{ imageUri->
                Intent(applicationContext, EditActivity::class.java).also { editImageIntent->
                    editImageIntent.putExtra(KEY_IMAGE_URI, imageUri)
                    startActivity(editImageIntent)
                }
            }
        }
//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            var result = CropImage.getActivityResult(data)
////            Intent(applicationContext, CameraActivity::class.java).also { editImageIntent->
////                editImageIntent.putExtra(KEY_IMAGE_URI, result.uri)
////                startActivity(editImageIntent)
////            }
//
//        }
//        if (requestCode == cameraRequestId && resultCode == RESULT_OK) {
//            data?.data?.let{ imageUri->
//
//            }
////            var image = data?.getParcelableExtra<Bitmap>(KEY_CAMERA)
////            Intent(applicationContext,CameraActivity::class.java).also { cameraImageIntent->
////                cameraImageIntent.putExtra(KEY_CAMERA,image)
////                startActivity(cameraImageIntent)
////            }
//
//
//        }
    }



}