package dk.verzier.shoppingv6.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shops")
data class ShopEntity(
    @PrimaryKey
    val name: String,
    val imageUrl: String,
    val brandColor: String
)
