package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.FragmentEditReceiptBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherActivity
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherEvent
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ReceiptAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditReceiptFragment : Fragment(R.layout.fragment_edit_receipt) {

    private var _binding: FragmentEditReceiptBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ReceiptAdapter
    private lateinit var priceAdapter: ReceiptAdapter

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditReceiptBinding.bind(view)

        initRecyclerView()

        repeatLifecycleFlow {
            viewModel.state.collectLatest {
                (activity as ExchangeVoucherActivity).loading(it.isLoading)

                it.receipt?.let { receipt ->
                    var priceList = "Giá: \n"
                    receipt.prices.forEach { item -> priceList = priceList + item + "\n" }
                    priceList = priceList + "Sản phẩm\n"
                    receipt.products.forEach { item -> priceList = priceList + item + "\n" }
                    priceList += "\n\n RAW:\n" + receipt.visionText
                    println(priceList)
                    
                    productAdapter.submitList(receipt.products)
                    priceAdapter.submitList(receipt.prices)
                }

                it.message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.onTriggerEvent(ExchangeVoucherEvent.ScanReceipt(viewModel.croppedImage!!))
    }

    private fun initRecyclerView() {
        binding.apply {
            productAdapter = ReceiptAdapter(false)
            rvProduct.adapter = productAdapter

            priceAdapter = ReceiptAdapter(true)
            rvPrice.adapter = priceAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}