package dev.marco.smartspacer.ubereats

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.toBitmap

/**
 * Uber Eats' own app icon, loaded from the installed Uber Eats app at runtime
 * (nothing of Uber's is bundled in this plugin). Null if Uber Eats isn't installed.
 */
object EatsIcon {
    @Volatile private var cached: Bitmap? = null
    @Volatile private var cachedGlyph: Bitmap? = null

    /** The full icon, background included. */
    fun get(context: Context, size: Int = 192): Bitmap? {
        cached?.let { return it }
        return try {
            context.packageManager.getApplicationIcon(EatsListener.EATS_PACKAGE)
                .toBitmap(size, size)
                .copy(Bitmap.Config.ARGB_8888, false)
                .also { cached = it }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Just the logo, without the background: the adaptive icon's monochrome layer
     * (themed icons, Android 13+) or else its foreground layer. Meant to be tinted.
     */
    fun glyph(context: Context, size: Int = 192): Bitmap? {
        cachedGlyph?.let { return it }
        return try {
            val icon = context.packageManager.getApplicationIcon(EatsListener.EATS_PACKAGE)
            val layer: Drawable = (icon as? AdaptiveIconDrawable)?.let {
                (if (Build.VERSION.SDK_INT >= 33) it.monochrome else null) ?: it.foreground
            } ?: return null
            // Adaptive layers are 108dp with the visible logo in the middle 72dp:
            // draw 1.5x larger and centered so the logo fills the bitmap
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val inset = size / 4
            layer.setBounds(-inset, -inset, size + inset, size + inset)
            layer.draw(Canvas(bitmap))
            bitmap.also { cachedGlyph = it }
        } catch (e: Exception) {
            null
        }
    }
}
