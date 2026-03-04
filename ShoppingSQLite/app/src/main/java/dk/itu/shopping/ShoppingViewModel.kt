package dk.itu.shopping

import android.app.Activity
import android.content.Context
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingViewModel : ViewModel() {

    private val itemsDB: ItemsDB = ItemsDB()

    val uiState: MutableLiveData<ShoppingUiState> =
        MutableLiveData<ShoppingUiState>(ShoppingUiState.Empty)

    fun initializeDB(context: Context) {
        itemsDB.initialize(context)
        updateUiState()
    }

    fun onAddItemClick(newWhat: TextView, newWhere: TextView, activity: Activity) {
        val whatS = newWhat.text.toString().trim { it <= ' ' }
        val whereS = newWhere.text.toString().trim { it <= ' ' }
        if (whatS.isNotEmpty() && whereS.isNotEmpty()) {
            itemsDB.addItem(whatS, whereS)
            newWhat.text = ""
            newWhere.text = ""
            newWhat.onEditorAction(EditorInfo.IME_ACTION_DONE)
            newWhere.onEditorAction(EditorInfo.IME_ACTION_DONE)
            updateUiState()
        } else showToast(activity)
    }

    fun onDeleteItemClick(what: String, activity: FragmentActivity) {
        val message = if (itemsDB.getValues().any { it.what == what }) {
            itemsDB.removeItem(what)
            updateUiState()
            "Removed $what"
        } else {
            "$what not found"
        }
        showToast(activity, message)
    }

    private fun updateUiState() {
        uiState.value = ShoppingUiState(itemsDB.getValues(), itemsDB.size())
    }

    private fun showToast(activity: Activity) {
        Toast.makeText(activity, R.string.empty_toast, Toast.LENGTH_LONG).show()
    }

    private fun showToast(activity: FragmentActivity, message: CharSequence) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    data class ShoppingUiState(
        val itemList: ArrayList<Item>,
        val itemListSize: Int
    ) {
        companion object {
            val Empty = ShoppingUiState(arrayListOf(), 0)
        }
    }
}