package dk.verzier.shoppingv6.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: String,
    val what: String,
    val where: String,
    val description: String,
    // TODO: Add deadline to item
)
