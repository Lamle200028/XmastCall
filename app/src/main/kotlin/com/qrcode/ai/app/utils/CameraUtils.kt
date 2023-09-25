package com.qrcode.ai.app.utils

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

object CameraUtils {
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var camera: Camera? = null
    var previewView: PreviewView? = null

    fun init(previewView: PreviewView): PreviewView {
        this.previewView = previewView
        this.previewView?.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        startCamera()
        return this.previewView!!
    }

    private fun startCamera() {
        if (previewView != null) {
            val context = previewView!!.context

            cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture?.addListener({
                val cameraProvider = cameraProviderFuture?.get()
                if (cameraProvider != null) {
                    bindPreview(cameraProvider)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        if (previewView != null) {
            val preview: Preview = Preview.Builder().build()

            val context = previewView!!.context

            val cameraSelector: CameraSelector =
                CameraSelector.Builder().requireLensFacing(lensFacing).build()

            preview.setSurfaceProvider(previewView?.surfaceProvider)
            cameraProvider.unbindAll()
            camera =
                cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, preview)
        }
    }

    fun flipCamera() {
        if (previewView != null) {
            lensFacing =
                if (lensFacing == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK
                else CameraSelector.LENS_FACING_FRONT

            startCamera()
        }
    }
}
