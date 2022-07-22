package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.cropimage

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.FragmentCropImageBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import com.ntikhoa.ocrreceipt.presentation.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropImageFragment : Fragment(R.layout.fragment_crop_image) {

    private var _binding: FragmentCropImageBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCropImageBinding.bind(view)

        binding.civReceipt.setImageBitmap(viewModel.image)

        binding.apply {
            btnDone.setOnClickListener {
                viewModel.croppedImage = civReceipt.croppedImage
                findNavController().navigate(R.id.action_cropImageFragment_to_editReceiptFragment)
            }

            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}