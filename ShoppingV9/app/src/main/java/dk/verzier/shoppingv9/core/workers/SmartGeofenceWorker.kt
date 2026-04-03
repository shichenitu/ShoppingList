package dk.verzier.shoppingv9.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dk.verzier.shoppingv9.R
import dk.verzier.shoppingv9.core.NotificationHelper
import dk.verzier.shoppingv9.domain.ItemRepository
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
        // TODO filter the items that belong to this shop
        val itemsForShop = items.filter {
            it.where.equals(shopName, ignoreCase = true)
        }

        // 4. Only show the notification if the list isn't empty!
        if (itemsForShop.isNotEmpty()) {
            val itemWord = if (itemsForShop.size == 1) context.getString(/* resId = */ R.string.item_singular) else context.getString(R.string.item_plural)
            val sampleItem = itemsForShop.first().what

            // TODO use the NotificationHelper to show a notification
            // TODO HINT: notificationId = shopName.hashCode()

            NotificationHelper.showNotification(
                context = context,
                title = "Shopping Trip: $shopName",
                message = "You have ${itemsForShop.size} $itemWord to buy here, including $sampleItem!",
                notificationId = shopName.hashCode()
            )

        } else {
            Log.d(/* tag = */ "SmartGeofenceWorker", /* msg = */ "Passed $shopName, but the shopping list is empty. Staying quiet!")
        }

        return Result.success()
    }
}