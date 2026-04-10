package dk.verzier.shoppingv10.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dk.verzier.shoppingv10.R
import dk.verzier.shoppingv10.core.NotificationHelper

class BatteryLowReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // TODO Trigger notification when battery is low
        // HINT: Use NotificationHelper.showNotification() with a unique static ID for this specific
        // notification & check strings.xml for the notification title and message.
    }
}