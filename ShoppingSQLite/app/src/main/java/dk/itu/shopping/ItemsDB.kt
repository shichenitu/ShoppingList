package dk.itu.shopping

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.ViewModel
import dk.itu.shopping.database.DBCreate
import dk.itu.shopping.database.ItemCursorWrapper
import dk.itu.shopping.database.ItemsDbSchema

class ItemsDB {
    companion object {
        var mDatabase: SQLiteDatabase? = null
    }

    //Must be called by Activity (and only there)
    fun initialize(context: Context) {
        if (mDatabase == null) {
            mDatabase = DBCreate(context.applicationContext).writableDatabase
        }
    }

    //Used in observer
    fun getValues(): ArrayList<Item> {
        val items = ArrayList<Item>()
        val cursor = queryItems(null, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            items.add(cursor.item)
            cursor.moveToNext()
        }
        cursor.close()
        return items
    }

    fun addItem(what: String, where: String) {
        val values = ContentValues()
        values.put(ItemsDbSchema.ItemTable.Cols.WHAT, what)
        values.put(ItemsDbSchema.ItemTable.Cols.WHERE, where)
        mDatabase!!.insert(ItemsDbSchema.ItemTable.NAME, null, values)
    }

    fun removeItem(what: String) {
        val newItem = Item(what, "")
        val selection = ItemsDbSchema.ItemTable.Cols.WHAT + " LIKE ?"
        mDatabase!!.delete(ItemsDbSchema.ItemTable.NAME, selection, arrayOf(newItem.what))
    }

    fun size(): Int {
        return getValues().size
    }

    // Database helper methods to convert between Items and database rows
    private fun queryItems(whereClause: String?, whereArgs: Array<String>?): ItemCursorWrapper {
        val cursor = mDatabase!!.query(
            ItemsDbSchema.ItemTable.NAME,
            null,  // Columns - null selects all columns
            whereClause, whereArgs,
            null,  // groupBy
            null,  // having
            null // orderBy
        )
        return ItemCursorWrapper(cursor)
    }
}