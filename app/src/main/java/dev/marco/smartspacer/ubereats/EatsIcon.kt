package dev.marco.smartspacer.ubereats

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.toBitmap

/**
 * The app icon of Uber Eats or the Uber app (whichever the order is in), loaded from the
 * installed app at runtime (nothing of Uber's is bundled in this plugin). Null if the app
 * isn't installed.
 */
object EatsIcon {
    private val cached = java.util.concurrent.ConcurrentHashMap<String, Bitmap>()
    private val cachedGlyph = java.util.concurrent.ConcurrentHashMap<String, Bitmap>()

    /** Uber Eats if it's installed, else the Uber app: for when there's no order to go by. */
    fun defaultPackage(context: Context): String = EatsListener.EATS_PACKAGE.takeIf {
        runCatching { context.packageManager.getApplicationInfo(it, 0) }.isSuccess
    } ?: EatsListener.UBER_PACKAGE

    /** The name shown next to the app's logo on the card */
    fun name(pkg: String?): String = if (pkg == EatsListener.UBER_PACKAGE) "Uber" else SOURCE_NAME

    /** The full icon, background included. */
    fun get(context: Context, pkg: String = defaultPackage(context), size: Int = 192): Bitmap? {
        cached[pkg]?.let { return it }
        return try {
            context.packageManager.getApplicationIcon(pkg)
                .toBitmap(size, size)
                .copy(Bitmap.Config.ARGB_8888, false)
                .also { cached[pkg] = it }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Just the logo, without the background: the adaptive icon's monochrome layer
     * (themed icons, Android 13+) or else its foreground layer. Meant to be tinted.
     */
    fun glyph(context: Context, pkg: String = defaultPackage(context), size: Int = 192): Bitmap? {
        cachedGlyph[pkg]?.let { return it }
        return try {
            val icon = context.packageManager.getApplicationIcon(pkg)
            val layer: Drawable = (icon as? AdaptiveIconDrawable)?.let {
                (if (Build.VERSION.SDK_INT >= 33) it.monochrome else null) ?: it.foreground
            } ?: return null
            // Adaptive layers are 108dp with the visible logo in the middle 72dp:
            // draw 1.5x larger and centered so the logo fills the bitmap
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val inset = size / 4
            layer.setBounds(-inset, -inset, size + inset, size + inset)
            layer.draw(Canvas(bitmap))
            bitmap.also { cachedGlyph[pkg] = it }
        } catch (e: Exception) {
            null
        }
    }
}
