package dev.marco.smartspacer.ubereats

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap

/**
 * The app icon of Uber Eats or the Uber app (whichever the order is in), loaded from the
 * installed app at runtime (nothing of Uber's is bundled in this plugin). Null if the app
 * isn't installed.
 */
object EatsIcon {
    private val cached = java.util.concurrent.ConcurrentHashMap<String, Bitmap>()

    /** Uber Eats if it's installed, else the Uber app: for when there's no order to go by. */
    fun defaultPackage(context: Context): String = EatsListener.EATS_PACKAGE.takeIf {
        runCatching { context.packageManager.getApplicationInfo(it, 0) }.isSuccess
    } ?: EatsListener.UBER_PACKAGE

    /** The app's name, for accessibility (the logo itself spells it out) */
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
}
