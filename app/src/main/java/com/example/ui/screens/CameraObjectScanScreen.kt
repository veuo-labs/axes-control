package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.alarm.AlarmScheduler
import com.example.data.UserPreferencesRepository
import com.example.ui.components.CameraViewfinder
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.GuardSuccess
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.vision.OfflineVisualEngine
import java.io.File
import java.io.FileOutputStream

@Composable
fun CameraObjectScanScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefsRepo = remember { UserPreferencesRepository.getInstance(context) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var captureTrigger by remember { mutableStateOf<(() -> Unit)?>(null) }
    var selectedHour by remember { mutableIntStateOf(7) }
    var selectedMinute by remember { mutableIntStateOf(30) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .testTag("camera_object_scan_screen")
    ) {
        if (capturedBitmap == null) {
            if (hasCameraPermission) {
                CameraViewfinder(
                    modifier = Modifier.fillMaxSize(),
                    onImageCaptureReady = { trigger -> captureTrigger = trigger },
                    onBitmapReady = { bitmap -> capturedBitmap = bitmap }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "OPTICAL ACCESS REQUIRED",
                            fontFamily = SpaceGrotesk,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentGold,
                                contentColor = BgDark
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("GRANT CAMERA", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(BgDark.copy(alpha = 0.85f), shape = CircleShape)
                        .border(1.dp, OutlineSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextLight
                    )
                }

                Box(
                    modifier = Modifier
                        .background(BgDark.copy(alpha = 0.85f), shape = RoundedCornerShape(20.dp))
                        .border(1.dp, OutlineSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "ANCHOR // CALIBRATION",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        color = AccentGold
                    )
                }

                Spacer(modifier = Modifier.size(44.dp))
            }

            // Bottom Shutter Bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(BgDark.copy(alpha = 0.90f))
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CENTER UNIQUE OBJECT IN RETICLE",
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = TextDim
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { captureTrigger?.invoke() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        contentColor = BgDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("capture_object_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LOCK IN OBJECT",
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        } else {
            // Confirmation & Alarm Time
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { capturedBitmap = null },
                        modifier = Modifier
                            .size(44.dp)
                            .background(SurfaceDark, shape = CircleShape)
                            .border(1.dp, OutlineSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rescan",
                            tint = TextLight
                        )
                    }

                    Text(
                        text = "FINGERPRINT VERIFIED",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = GuardSuccess
                    )

                    Spacer(modifier = Modifier.size(44.dp))
                }

                // Thumbnail
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(2.dp, AccentGold, RoundedCornerShape(14.dp))
                    ) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Object",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "OPTICAL HASH EXTRACTED",
                        fontFamily = SpaceGrotesk,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = TextLight
                    )
                }

                // Digital Time Picker
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ALARM TIME",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%02d".format(selectedHour),
                                fontFamily = SpaceGrotesk,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight
                            )
                            Row {
                                OutlinedButton(
                                    onClick = { selectedHour = (selectedHour - 1 + 24) % 24 },
                                    modifier = Modifier.size(36.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) { Text("-") }
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedButton(
                                    onClick = { selectedHour = (selectedHour + 1) % 24 },
                                    modifier = Modifier.size(36.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) { Text("+") }
                            }
                        }

                        Text(
                            text = ":",
                            fontFamily = SpaceGrotesk,
                            fontSize = 38.sp,
                            color = AccentGold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%02d".format(selectedMinute),
                                fontFamily = SpaceGrotesk,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight
                            )
                            Row {
                                OutlinedButton(
                                    onClick = { selectedMinute = (selectedMinute - 5 + 60) % 60 },
                                    modifier = Modifier.size(36.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) { Text("-") }
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedButton(
                                    onClick = { selectedMinute = (selectedMinute + 5) % 60 },
                                    modifier = Modifier.size(36.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) { Text("+") }
                            }
                        }
                    }
                }

                // ARM ALARM BUTTON
                Button(
                    onClick = {
                        val bitmap = capturedBitmap ?: return@Button
                        val file = File(context.filesDir, "alarm_anchor_object.jpg")
                        FileOutputStream(file).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        }
                        val fingerprint = OfflineVisualEngine.extractObjectFingerprint(bitmap).serialize()
                        prefsRepo.saveAlarmObject(file.absolutePath, fingerprint)
                        prefsRepo.setAlarmTime(selectedHour, selectedMinute)
                        prefsRepo.setAlarmActive(true)
                        AlarmScheduler.scheduleAlarm(context, selectedHour, selectedMinute)

                        Toast.makeText(context, "Alarm armed for %02d:%02d".format(selectedHour, selectedMinute), Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        contentColor = BgDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("activate_alarm_button")
                ) {
                    Text(
                        text = "ARM ALARM",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
    }
}
