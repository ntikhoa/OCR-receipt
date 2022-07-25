package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.LayoutEditPriceItemBinding


class PriceAdapter : ListAdapter<String, PriceAdapter.PriceViewHolder>(DIFF_CALLBACK) {

    private lateinit var context: Context

    private var onEditReceiptList: OnEditReceiptList? = null

    fun setOnEditReceiptList(onEdit: OnEditReceiptList) {
        this.onEditReceiptList = onEdit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_edit_price_item, parent, false)
        return PriceViewHolder(LayoutEditPriceItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: PriceViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(it)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class PriceViewHolder(private val binding: LayoutEditPriceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            this.setIsRecyclable(false)

            binding.apply {
                viewEditMode.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        if (!etReceipt.isEnabled) {
                            editMode()
                        }
                    }
                }

                btnCancel.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        etReceipt.setText(currentList[position])
                        closeMode()
                    }
                }

                btnRemove.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onEditReceiptList?.onRemove(position)
                        closeMode()
                        notifyDataSetChanged()
                    }
                }

                btnDone.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onEditReceiptList?.onDone(position, etReceipt.text.toString().trim())
                        closeMode()
                        notifyDataSetChanged()
                    }
                }

                flAddTop.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onEditReceiptList?.onAddTop(position)
                        closeMode()
                        notifyDataSetChanged()
                    }
                }

                flAddBottom.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onEditReceiptList?.onAddBottom(position)
                        closeMode()
                        notifyDataSetChanged()
                    }
                }
            }
        }


        private fun editMode() {
            binding.apply {
                viewEditMode.visibility = View.GONE

                etReceipt.isEnabled = true
                btnRemove.visibility = View.VISIBLE
                llEdit.visibility = View.VISIBLE
                flAddTop.visibility = View.VISIBLE
                flAddBottom.visibility = View.VISIBLE
                etReceipt.requestFocus()
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etReceipt, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        private fun closeMode() {
            binding.apply {
                viewEditMode.visibility = View.VISIBLE

                etReceipt.isEnabled = false
                btnRemove.visibility = View.GONE
                llEdit.visibility = View.GONE
                flAddTop.visibility = View.GONE
                flAddBottom.visibility = View.GONE
            }
        }

        fun bind(item: String) {
            binding.apply {
                tvIndex.text = (adapterPosition + 1).toString()
                etReceipt.setText(item)
                if (item.contains("[^0-9,]".toRegex())) {
                    tvIndex.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_red))
                    tvIndex.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }
    }
}