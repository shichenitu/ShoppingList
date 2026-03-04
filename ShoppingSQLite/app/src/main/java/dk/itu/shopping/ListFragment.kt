package dk.itu.shopping

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_list, container, false)

        // Shared data
        val viewModel = ViewModelProvider(requireActivity())[ShoppingViewModel::class.java]

        // Set up recyclerview
        val itemList: RecyclerView = v.findViewById(R.id.listItems)
        itemList.layoutManager = LinearLayoutManager(activity)
        val mAdapter = ItemAdapter(viewModel)
        itemList.adapter = mAdapter

        viewModel.uiState.observe(requireActivity()) { mAdapter.notifyDataSetChanged() }

        //Only show Back button in Portrait mode
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val backButton: Button = v.findViewById(R.id.back_button)
            backButton.setOnClickListener {
                requireActivity()
                    .supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.main_fragment,
                        UIFragment()
                    ).commit()
            }
        }
        return v
    }

    private inner class ItemHolder(itemView: View, shoppingViewModel: ShoppingViewModel) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val mWhatTextView: TextView
        private val mWhereTextView: TextView
        private val mNoView: TextView
        private val viewModel = shoppingViewModel

        init {
            mNoView = itemView.findViewById(R.id.item_no)
            mWhatTextView = itemView.findViewById(R.id.item_what)
            mWhereTextView = itemView.findViewById(R.id.item_where)
            itemView.setOnClickListener(this)
        }

        fun bind(item: Item, position: Int) {
            mNoView.text = " $position "
            mWhatTextView.text = item.what
            mWhereTextView.text = item.where
        }

        override fun onClick(v: View) {
            // Trick from https://stackoverflow.com/questions/5754887/accessing-view-inside-the-linearlayout-with-code
            val what = (v.findViewById<View>(R.id.item_what) as TextView).text as String
            //once we have a value for what and where, we can delete the item
            activity?.let { fragmentActivity ->
                viewModel.onDeleteItemClick(what, fragmentActivity)
            }
        }
    }

    private inner class ItemAdapter(shoppingViewModel: ShoppingViewModel) :
        RecyclerView.Adapter<ItemHolder>() {
        private val viewModel = shoppingViewModel

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val layoutInflater = LayoutInflater.from(activity)
            val v = layoutInflater.inflate(R.layout.one_row, parent, false)
            return ItemHolder(v, viewModel)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val item = viewModel.uiState.value!!.itemList[position]
            holder.bind(item, position)
        }

        override fun getItemCount(): Int {
            return viewModel.uiState.value!!.itemListSize
        }
    }
}