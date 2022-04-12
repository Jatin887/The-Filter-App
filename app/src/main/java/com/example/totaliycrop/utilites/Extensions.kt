package com.example.totaliycrop.utilites

import android.content.Context
import android.os.Message
import android.view.View
import android.widget.Toast

fun Context.displayToast(message: String?){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()

}
fun View.show(){
    this.visibility = View.VISIBLE
}
