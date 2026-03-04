package dk.itu.shopping.database

import android.database.Cursor
import android.database.CursorWrapper
import dk.itu.shopping.Item
import dk.itu.shopping.database.ItemsDbSchema.ItemTable

class ItemCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {
    val item: Item
        get() {
            val what = getString(getColumnIndex(ItemTable.Cols.WHAT))
            val where = getString(getColumnIndex(ItemTable.Cols.WHERE))
            return Item(what, where)
        }
}