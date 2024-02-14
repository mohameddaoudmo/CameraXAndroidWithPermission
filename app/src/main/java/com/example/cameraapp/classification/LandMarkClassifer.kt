package com.example.cameraapp.classification

import android.graphics.Bitmap
import com.example.cameraapp.classification.Classification

interface LandMarkClassifer {
    fun classifier (bitmap: Bitmap,rotation:Int):List<Classification>
}