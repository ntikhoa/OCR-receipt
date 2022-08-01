package com.ntikhoa.ocrreceipt.presentation.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.domain.model.Transaction
import com.ntikhoa.ocrreceipt.databinding.LayoutTransactionItemBinding

class TransactionAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transaction>() {

        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return true
        }

    }
    private val differ = AsyncListDiffer(
        TransactionRecyclerChangeCallBack(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK).build(),
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_transaction_item, parent, false)
        return TransactionViewHolder(LayoutTransactionItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Transaction>) {
        val newList = list.toMutableList()
        differ.submitList(newList)
    }

    class TransactionViewHolder(private val binding: LayoutTransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transaction) {
            binding.apply {
                tvVoucherName.text = item.voucherName
                tvGift.text = item.status
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Transaction)
    }

    internal inner class TransactionRecyclerChangeCallBack(
        private val adapter: TransactionAdapter
    ) : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }
    }
}