package dk.verzier.shoppingv9.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dk.verzier.shoppingv9.R
import dk.verzier.shoppingv9.core.NotificationHelper
import dk.verzier.shoppingv9.domain.ItemRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@HiltWorker
class DeadlineNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val itemRepository: ItemRepository
) : CoroutineWorker(appContext = context, params = workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Calculate tomorrow's date
            val tomorrow = Calendar.getInstance().apply {
                add(/* p0 = */ Calendar.DAY_OF_YEAR, /* p1 = */ 1)
            }.time
            val format = SimpleDateFormat(/* pattern = */ "dd.MM.yyyy", /* locale = */ Locale.getDefault())
            val tomorrowString = format.format(/* date = */ tomorrow)

            val items = itemRepository.getShoppingList().first()

            val expiringItems = items.filter { it.deadline == tomorrowString }

            expiringItems.forEach { item ->
                NotificationHelper.showNotification(
                    context = applicationContext,
                    title = applicationContext.getString(/* resId = */ R.string.deadline_notification_title),
                    notificationId = item.id.hashCode(),
                    message = applicationContext.getString(
                        /* resId = */ R.string.deadline_notification_message,
                        /* ...formatArgs = */ item.what,
                        item.where
                    )
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}