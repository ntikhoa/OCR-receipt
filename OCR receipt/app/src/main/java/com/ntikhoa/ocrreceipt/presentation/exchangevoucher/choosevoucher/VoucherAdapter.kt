package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.choosevoucher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.domain.model.Voucher
import com.ntikhoa.ocrreceipt.databinding.LayoutVoucherItemBinding

class VoucherAdapter(var currentVoucherID: Int? = -1) :
    ListAdapter<Voucher, VoucherAdapter.VoucherViewHolder>(DIFF_CALLBACK) {

    var listener: OnItemClickListener? = null

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Voucher>() {
            override fun areItemsTheSame(oldItem: Voucher, newItem: Voucher): Boolean {
                return oldItem.ID == newItem.ID
            }

            override fun areContentsTheSame(oldItem: Voucher, newItem: Voucher): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_voucher_item, parent, false)
        return VoucherViewHolder(LayoutVoucherItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = currentList[position]
        voucher.let {
            holder.bind(voucher, position)
        }
    }

    inner class VoucherViewHolder(private val binding: LayoutVoucherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ll.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    currentVoucherID = currentList[position].ID
                    notifyDataSetChanged()
                    listener?.onClick(position)
                }
            }
        }

        fun bind(voucher: Voucher, position: Int) {
            binding.apply {
                tvVoucherName.text = voucher.Name
                tvGift.text = voucher.GiftName
                if (voucher.ID == currentVoucherID) {
                    ivChosen.visibility = View.VISIBLE
                } else ivChosen.visibility = View.GONE
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }
}