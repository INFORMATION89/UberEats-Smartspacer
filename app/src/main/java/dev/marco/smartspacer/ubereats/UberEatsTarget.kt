package dev.marco.smartspacer.ubereats

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon as AndroidIcon
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate

/**
 * The card's subtitle: Uber Eats' details (restaurant, courier...) and the time left or the
 * progress, joined with " · ". The time goes first when the line is long.
 */
fun cardSubtitle(state: OrderState, settings: Settings): String? {
    val extra = when (settings.extra) {
        Settings.Extra.TIME_LEFT -> state.timeLeftText()
        Settings.Extra.PROGRESS -> state.progressText()
        Settings.Extra.NONE -> null
    }
    val base = state.text
    // Long lines get cut off at the end, so put the short extra first:
    //   "Tacos El Güero · 12 min"  but  "12 min · Luis is heading your way…"
    val parts = if ((base?.length ?: 0) > LONG_LINE) listOf(extra, base) else listOf(base, extra)
    return parts.filterNotNull().joinToString(" · ").takeIf { it.isNotEmpty() }
}

/** Divider between the subtitle and "[logo] Uber Eats" */
const val SOURCE_BULLET = " ·"

/** The app's name next to its logo on the card */
const val SOURCE_NAME = "Uber Eats"

/** Past this many characters the subtitle is likely to be cut off on the card */
private const val LONG_LINE = 16

class UberEatsTarget : SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        val state = OrderStore.state ?: return emptyList()
        val ctx = provideContext()
        val settings = Settings(ctx)
        // The app the order is in: Uber Eats, or the Uber app
        val pkg = state.sourcePackage ?: EatsIcon.defaultPackage(ctx)
        val name = EatsIcon.name(pkg)
        // Tapping opens the order (or just the app if the notification has no intent)
        val openOrder = TapAction(
            pendingIntent = state.contentIntent,
            intent = if (state.contentIntent == null) ctx.packageManager.getLaunchIntentForPackage(pkg) else null
        )

        // Courier photo (full color) > the app's icon (full color) > bag glyph
        val icon = state.courier?.takeIf { settings.showCourier }
            ?.let { Icon(AndroidIcon.createWithBitmap(it), shouldTint = false) }
            ?: EatsIcon.get(ctx, pkg)
                ?.let { Icon(AndroidIcon.createWithBitmap(it), contentDescription = name, shouldTint = false) }
            ?: Icon(AndroidIcon.createWithResource(ctx, R.drawable.ic_order))

        // "[logo] Uber Eats" (or "[logo] Uber") after the subtitle (the supplemental spot, where
        // weather usually shows), so it's clear where the info comes from. Logo only, tinted.
        val source = EatsIcon.glyph(ctx, pkg)?.let {
            ComplicationTemplate.Basic(
                "uber_eats_source_$smartspacerId",
                Icon(AndroidIcon.createWithBitmap(it), contentDescription = name, shouldTint = true),
                Text(name),
                openOrder
            ).create()
        }

        return listOf(
            TargetTemplate.Basic(
                "uber_eats_$smartspacerId",
                ComponentName(ctx, UberEatsTarget::class.java),
                SmartspaceTarget.FEATURE_UNDEFINED,
                Text(state.title),
                cardSubtitle(state, settings)?.let { Text(if (source != null) it + SOURCE_BULLET else it) },
                icon,
                openOrder,
                source
            ).create().apply {
                // With Native Smartspace, the lock screen hides this notification while our card
                // is the first one showing. Nothing is cancelled.
                if (settings.hideNotification) sourceNotificationKey = state.notificationKey
            }
        )
    }

    override fun getConfig(smartspacerId: String?): Config {
        val ctx = provideContext()
        return Config(
            label = ctx.getString(R.string.target_label),
            description = ctx.getString(R.string.target_description),
            icon = AndroidIcon.createWithResource(ctx, R.drawable.ic_order),
            configActivity = Intent(ctx, SettingsActivity::class.java)
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean = false
}
