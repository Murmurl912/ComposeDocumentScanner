package com.example.scan

import android.graphics.Bitmap
import java.nio.ByteBuffer

object NativeLib {

    init {
        System.loadLibrary("scan")
    }


    external fun decode(width: Int, height: Int, buffer: ByteBuffer): Bitmap

}