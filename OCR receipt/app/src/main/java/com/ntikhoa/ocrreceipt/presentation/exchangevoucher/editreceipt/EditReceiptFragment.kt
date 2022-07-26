package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.domain.model.ProductSearch
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.FragmentEditReceiptBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherActivity
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherEvent
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditReceiptFragment : Fragment(R.layout.fragment_edit_receipt) {

    private var _binding: FragmentEditReceiptBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var priceAdapter: PriceAdapter

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onTriggerEvent(ExchangeVoucherEvent.ScanReceipt(viewModel.croppedImage!!))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditReceiptBinding.bind(view)
        initRecyclerView()

        repeatLifecycleFlow {
            viewModel.state.collectLatest {
                (activity as ExchangeVoucherActivity).loading(it.isLoading)

                it.prices?.let { prices ->
                    priceAdapter.submitList(prices)
                    val mutablePrices = viewModel.getCurrentPrices()!!
                    priceAdapter.setOnEditReceiptList(object : OnEditReceiptList {
                        override fun onDone(position: Int, text: String) {
                            mutablePrices[position] = text
                            priceAdapter.submitList(mutablePrices)
                        }

                        override fun onRemove(position: Int) {
                            mutablePrices.removeAt(position)
                            priceAdapter.submitList(mutablePrices)
                        }

                        override fun onAddTop(position: Int) {
                            mutablePrices.add(position, "")
                            priceAdapter.submitList(mutablePrices)
                        }

                        override fun onAddBottom(position: Int) {
                            mutablePrices.add(position + 1, "")
                            priceAdapter.submitList(mutablePrices)
                        }
                    })
                }

                it.productsSearch?.let { productsSearch ->
                    productAdapter.submitList(productsSearch)
                    val mutableProducts = viewModel.getCurrentProducts()!!
                    productAdapter.setOnEditReceiptList(object : OnEditReceiptList {
                        override fun onDone(position: Int, text: String) {
                            mutableProducts[position].productName = text
                            productAdapter.submitList(mutableProducts)
                        }

                        override fun onRemove(position: Int) {
                            mutableProducts.removeAt(position)
                            productAdapter.submitList(mutableProducts)
                        }

                        override fun onAddTop(position: Int) {
                            mutableProducts.add(position, ProductSearch())
                            productAdapter.submitList(mutableProducts)
                        }

                        override fun onAddBottom(position: Int) {
                            mutableProducts.add(position + 1, ProductSearch())
                            productAdapter.submitList(mutableProducts)
                        }
                    })
                }

                it.message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.apply {
            btnDone.setOnClickListener {
                try {
                    viewModel.submitReceipt()
                    findNavController().navigate(R.id.action_editReceiptFragment_to_chooseVoucherFragment)
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            btnAddReceipt.setOnClickListener {
                try {
                    viewModel.submitReceipt()
                    findNavController().navigate(R.id.action_editReceiptFragment_to_takeReceiptFragment)
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            productAdapter = ProductAdapter()
            rvProduct.adapter = productAdapter

            priceAdapter = PriceAdapter()
            rvPrice.adapter = priceAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.apply {
            rvPrice.adapter = null
            rvProduct.adapter = null
        }
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onClearedEditReceipt()
    }
}