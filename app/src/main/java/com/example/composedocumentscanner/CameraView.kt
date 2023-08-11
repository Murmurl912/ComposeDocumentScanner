package com.example.composedocumentscanner

import android.graphics.Bitmap
import android.graphics.Point
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.scan.NativeScanner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionView() {

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
        )
    )

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = {
            // ask permission
            LaunchedEffect(Unit) {
                permissionState.launchMultiplePermissionRequest()
            }
        },
        permissionsNotAvailableContent = {
            // ask permission
            LaunchedEffect(Unit) {
                permissionState.launchMultiplePermissionRequest()
            }
        }
    ) {
        CameraView(Modifier.fillMaxSize())
    }
}

@Composable
fun CameraView(
    modifier: Modifier
) {
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var camera: Camera? by remember {
        mutableStateOf(null)
    }
    val selector = remember {
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }
    var bitmap: ImageBitmap? by remember {
        mutableStateOf(null)
    }
    var points: Array<Point> by remember {
        mutableStateOf(emptyArray())
    }

    val analysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build().apply {
                setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                    kotlin.runCatching {
                        image.detect().apply {
                            bitmap = first.asImageBitmap()
                            points = second
                        }
                    }.onFailure {
                        it.printStackTrace()
                        image.close()
                    }.onSuccess {
                         image.close()
                    }

                }
            }
    }
    LaunchedEffect(selector, lifecycleOwner) {
        val provider = withContext(Dispatchers.IO) {
            ProcessCameraProvider.getInstance(localContext)
                .get()
        }
        camera = provider.bindToLifecycle(
            lifecycleOwner,
            selector,
            analysis
        )
    }
    val bitmapSnapshot = bitmap
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (bitmapSnapshot == null) {
            Text(text = "No Image", style = MaterialTheme.typography.bodyLarge)
        } else {
            Image(bitmap = bitmapSnapshot,
                contentDescription = "",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun ImageProxy.detect(): Pair<Bitmap, Array<Point>> {
    val planeProxy = planes[0]
    val buffer: ByteBuffer = planeProxy.buffer
    return NativeScanner.smartDocument(buffer, width, height)
}