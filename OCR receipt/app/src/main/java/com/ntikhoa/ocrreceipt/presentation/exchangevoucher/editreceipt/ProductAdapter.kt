package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.ShapeAppearanceModel
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.domain.model.ProductSearch
import com.ntikhoa.ocrreceipt.databinding.LayoutEditProductItemBinding

class ProductAdapter :
    ListAdapter<ProductSearch, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    private lateinit var context: Context

    private var onEditReceiptList: OnEditReceiptList? = null

    fun setOnEditReceiptList(onEdit: OnEditReceiptList) {
        this.onEditReceiptList = onEdit
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_edit_product_item, parent, false)
        return ProductViewHolder(LayoutEditProductItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(it, position)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductSearch>() {
            override fun areItemsTheSame(oldItem: ProductSearch, newItem: ProductSearch): Boolean {
                return oldItem.productName == newItem.productName
            }

            override fun areContentsTheSame(
                oldItem: ProductSearch,
                newItem: ProductSearch
            ): Boolean {
                return true
            }

        }
    }

    inner class ProductViewHolder(private val binding: LayoutEditProductItemBinding) :
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
                        etReceipt.setText(currentList[position].productName)
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
                cgSuggestion.setOnCheckedStateChangeListener(object :
                    ChipGroup.OnCheckedStateChangeListener {
                    override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {

                    }

                })
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

        fun bind(item: ProductSearch, position: Int) {
            binding.apply {
                tvIndex.text = (adapterPosition + 1).toString()
                etReceipt.setText(item.productName)

                for (productSearch in item.suggestion) {
                    val chip = Chip(context)
                    chip.text = productSearch
                    chip.setTextColor(ContextCompat.getColor(context, R.color.white))
                    chip.setChipBackgroundColorResource(R.color.primary_blue)
                    chip.typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
                    chip.setOnClickListener {
                        onEditReceiptList?.onDone(position, productSearch)
                        notifyDataSetChanged()
                    }
                    cgSuggestion.addView(chip)
                }
            }
        }
    }
}