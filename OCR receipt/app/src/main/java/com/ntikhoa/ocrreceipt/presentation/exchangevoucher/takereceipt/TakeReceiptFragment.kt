package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.takereceipt

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.FragmentTakeReceiptBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.TakePhotoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class TakeReceiptFragment : TakePhotoFragment(R.layout.fragment_take_receipt) {

    private var _binding: FragmentTakeReceiptBinding? = null
    private val binding get() = _binding!!

    protected val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTakeReceiptBinding.bind(view)

        startCamera(binding.viewFinder.surfaceProvider)

        binding.apply {
            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnTakePhoto.setOnClickListener {
                takePhoto()
            }

            fabDev.setOnClickListener {
                loadImage()
            }
        }
    }

    override fun onTakePhoto(bitmap: Bitmap) {
        viewModel.image = bitmap
        findNavController().navigate(R.id.action_takeReceiptFragment_to_cropImageFragment)
    }

    override fun onLoadImage(bitmap: Bitmap) {
        viewModel.image = bitmap
        findNavController().navigate(R.id.action_takeReceiptFragment_to_cropImageFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}