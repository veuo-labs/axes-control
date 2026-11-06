package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.AccentGold
import com.example.ui.theme.OutlineSubtle
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

@Composable
fun CameraViewfinder(
    modifier: Modifier = Modifier,
    onFrameCaptured: ((Bitmap) -> Unit)? = null,
    onImageCaptureReady: ((() -> Unit) -> Unit)? = null,
    onBitmapReady: ((Bitmap) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (onFrameCaptured != null) {
                            val bitmap = imageProxyToBitmap(imageProxy)
                            if (bitmap != null) {
                                onFrameCaptured(bitmap)
                            }
                        }
                        imageProxy.close()
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalysis
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraViewfinder", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Pass capture callback up to caller
        onImageCaptureReady?.invoke {
            val photoFile = File(context.cacheDir, "scan_object_${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        if (bitmap != null) {
                            ContextCompat.getMainExecutor(context).execute {
                                onBitmapReady?.invoke(bitmap)
                            }
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraViewfinder", "Photo capture failed: ${exception.message}", exception)
                    }
                }
            )
        }

        // Minimalist golden reticle overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val reticleSize = 240.dp.toPx()
            val left = (size.width - reticleSize) / 2f
            val top = (size.height - reticleSize) / 2f
            val cornerLen = 28.dp.toPx()
            val strokeW = 3.dp.toPx()

            // Top-left
            drawLine(AccentGold, Offset(left, top), Offset(left + cornerLen, top), strokeW)
            drawLine(AccentGold, Offset(left, top), Offset(left, top + cornerLen), strokeW)

            // Top-right
            drawLine(AccentGold, Offset(left + reticleSize, top), Offset(left + reticleSize - cornerLen, top), strokeW)
            drawLine(AccentGold, Offset(left + reticleSize, top), Offset(left + reticleSize, top + cornerLen), strokeW)

            // Bottom-left
            drawLine(AccentGold, Offset(left, top + reticleSize), Offset(left + cornerLen, top + reticleSize), strokeW)
            drawLine(AccentGold, Offset(left, top + reticleSize), Offset(left, top + reticleSize - cornerLen), strokeW)

            // Bottom-right
            drawLine(AccentGold, Offset(left + reticleSize, top + reticleSize), Offset(left + reticleSize - cornerLen, top + reticleSize), strokeW)
            drawLine(AccentGold, Offset(left + reticleSize, top + reticleSize), Offset(left + reticleSize, top + reticleSize - cornerLen), strokeW)

            // Center target cross
            val cx = size.width / 2f
            val cy = size.height / 2f
            val crossSize = 10.dp.toPx()
            drawLine(AccentGold.copy(alpha = 0.6f), Offset(cx - crossSize, cy), Offset(cx + crossSize, cy), 1.5.dp.toPx())
            drawLine(AccentGold.copy(alpha = 0.6f), Offset(cx, cy - crossSize), Offset(cx, cy + crossSize), 1.5.dp.toPx())
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    return try {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        buffer.rewind()
        val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)

        // Rotate bitmap if needed
        val rotationDegrees = image.imageInfo.rotationDegrees
        if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    } catch (e: Exception) {
        null
    }
}
