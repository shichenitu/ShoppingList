package dk.verzier.shoppingv9.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dk.verzier.shoppingv9.core.workers.SmartGeofenceWorker

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(/* intent = */ intent)

        if (geofencingEvent == null || geofencingEvent.hasError()) {
            Log.e(/* tag = */ "GeofenceReceiver", /* msg = */ "Error receiving geofence event")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return

            triggeringGeofences.forEach { geofence ->
                val shopName = geofence.requestId

                val workRequest = OneTimeWorkRequestBuilder<SmartGeofenceWorker>()
                    .setInputData(workDataOf("shop_name" to shopName))
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }
}