package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.core.app.ApplicationProvider
import com.example.data.UserPreferencesRepository
import com.example.vision.OfflineVisualEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `verify app name resource is ZeroReel`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Axes Control", appName)
  }

  @Test
  fun `test user preferences reel limit and increment`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = UserPreferencesRepository(context)

    repo.setDailyLimit(50)
    repo.resetDailyReelsCount()

    val count = repo.incrementReelsViewed()
    assertEquals(1, count)

    val prefs = repo.userPreferencesFlow.value
    assertEquals(50, prefs.dailyReelLimit)
    assertEquals(1, prefs.reelsViewedToday)
    assertFalse(prefs.isLimitBlocked)
  }

  @Test
  fun `test offline object fingerprint extraction and self-matching`() {
    val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply { color = Color.RED }
    canvas.drawCircle(32f, 32f, 20f, paint)

    val fingerprint = OfflineVisualEngine.extractObjectFingerprint(bitmap)
    val serialized = fingerprint.serialize()
    assertTrue(serialized.isNotEmpty())

    val score = OfflineVisualEngine.matchObject(bitmap, fingerprint)
    assertTrue("Self match score should be high ($score)", score > 0.85f)
  }
}
