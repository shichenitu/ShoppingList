package dk.itu.shopping

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class UIFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_ui, container, false)

        // Shared data
        val viewModel = ViewModelProvider(requireActivity())[ShoppingViewModel::class.java]

        //Text Fields
        val newWhat: TextView = v.findViewById(R.id.what_text)
        val newWhere: TextView = v.findViewById(R.id.where_text)

        //Buttons
        val addItem: Button = v.findViewById(R.id.add_button)


        // list all things
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val listItems: Button = v.findViewById(R.id.list)
            listItems.setOnClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.main_fragment,
                        ListFragment()
                    ).commit()
            }
        }

        // adding a new thing
        addItem.setOnClickListener {
            activity?.let { fragmentActivity ->
                viewModel.onAddItemClick(
                    newWhat,
                    newWhere,
                    fragmentActivity
                )
            }
        }
        return v
    }
}
