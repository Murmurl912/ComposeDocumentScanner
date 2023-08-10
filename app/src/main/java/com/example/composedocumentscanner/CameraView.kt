package com.example.composedocumentscanner

import android.content.Context
import android.graphics.Canvas
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.camera.core.Preview
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionView() {

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = {

        },
        permissionsNotAvailableContent = {

        }
    ) {

    }
}

@Composable
fun CameraView() {
    val localContext = LocalContext.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(localContext)
    }
    val previewView = remember {
        PreviewView(localContext)
    }
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }
}

class CameraSurfaceView: SurfaceView, SurfaceProvider, SurfaceHolder.Callback {

    constructor(context: Context): super(context)

    init {
        holder.addCallback(this)
    }

    private var surfaceRequest: SurfaceRequest? = null
    private var targetSize: Size = Size(0, 0)
    private var currentSize: Size = Size(0, 0)
    private var surfaceProvided = false

    override fun onSurfaceRequested(request: SurfaceRequest) {
        if (surfaceRequest != null) {
            // cancel previous request
            surfaceRequest?.willNotProvideSurface()
        }

        surfaceRequest = request
        targetSize = request.resolution
        surfaceProvided = false
        request.addRequestCancellationListener(ContextCompat.getMainExecutor(context)) {
            // todo
        }
        post {
            tryProvideSurface()
        }
    }

    private fun tryProvideSurface() {
        val surface: Surface = holder.surface
        if (canProvideSurface()) {
            surfaceRequest?.provideSurface(surface,
                ContextCompat.getMainExecutor(context)
            ) { result: SurfaceRequest.Result? ->

            }
            surfaceProvided = true
        } else {
            holder.setFixedSize(
                targetSize.width,
                targetSize.height
            )
        }
    }

    private fun canProvideSurface(): Boolean {
        return !surfaceProvided && surfaceRequest != null
                && targetSize == currentSize
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceRequest?.provideSurface(
            holder.surface,
            ContextCompat.getMainExecutor(context)
        ) {

        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        currentSize = Size(width, height)
        tryProvideSurface()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (!surfaceProvided) {
            surfaceRequest?.willNotProvideSurface()
        }
        surfaceRequest = null
        surfaceProvided = false
        currentSize = Size(0, 0)
        targetSize = Size(0, 0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}