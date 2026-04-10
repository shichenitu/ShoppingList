package dk.verzier.shoppingv10.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dk.verzier.shoppingv10.R
import dk.verzier.shoppingv10.core.NotificationHelper
import dk.verzier.shoppingv10.domain.ItemRepository
import kotlinx.coroutines.flow.first

@HiltWorker
class SmartGeofenceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val itemRepository: ItemRepository
) : CoroutineWorker(appContext = context, params = workerParams) {

    override suspend fun doWork(): Result {
        // 1. Get the shop name passed from the GeofenceReceiver
        val shopName = inputData.getString(key = "shop_name") ?: return Result.failure()

        // 2. Fetch the current shopping list
        val items = itemRepository.getShoppingList().first()

        // 3. Check if any items belong to this shop (ignoring case)
        val itemsForShop = items.filter { it.where.equals(other = shopName, ignoreCase = true) }

        // 4. Only show the notification if the list isn't empty!
        if (itemsForShop.isNotEmpty()) {
            val itemWord = if (itemsForShop.size == 1) context.getString(/* resId = */ R.string.item_singular) else context.getString(R.string.item_plural)
            val sampleItem = itemsForShop.first().what

            NotificationHelper.showNotification(
                context = applicationContext,
                title = context.getString(/* resId = */ R.string.geofence_notification_title, /* ...formatArgs = */
                    shopName),
                message = context.getString(/* resId = */ R.string.geofence_notification_message, /* ...formatArgs = */
                    itemsForShop.size, itemWord, sampleItem),
                notificationId = shopName.hashCode()
            )
        } else {
            Log.d(/* tag = */ "SmartGeofenceWorker", /* msg = */ "Passed $shopName, but the shopping list is empty. Staying quiet!")
        }

        return Result.success()
    }
}