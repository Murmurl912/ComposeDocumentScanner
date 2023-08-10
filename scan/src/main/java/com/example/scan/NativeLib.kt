package com.example.scan

object NativeLib {

    init {
        System.loadLibrary("scan")
    }

    /**
     * A native method that is implemented by the 'scan' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String


}