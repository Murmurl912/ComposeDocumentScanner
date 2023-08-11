package com.example.scan

import android.graphics.Bitmap
import android.graphics.Point
import java.nio.ByteBuffer

object NativeScanner {

    init {
        System.loadLibrary("scan")
    }
    external fun decode(width: Int, height: Int, buffer: ByteBuffer): Bitmap

    external fun scan(imageBuffer: ByteBuffer, width: Int, height: Int, drawPoints: Boolean, out: Bitmap): Array<Point>

    external fun smartScan(buffer: ByteBuffer, out: Bitmap, width: Int, height: Int, overlayPoint: Boolean): Array<Point>

    fun detect(image: ByteBuffer, width: Int, height: Int): Pair<Bitmap, Array<Point>> {
        val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
        val points = scan(image, width, height, true, bitmap)
        println("points: " + points.contentToString())
        return bitmap to points
    }

    fun smartDocument(image: ByteBuffer, width: Int, height: Int): Pair<Bitmap, Array<Point>> {
        val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
        val points = smartScan(image, bitmap, width, height, true)
        println("points: " + points.contentToString())
        return bitmap to points
    }

}