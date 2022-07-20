package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.FragmentTakeEvidenceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TakeEvidenceFragment : TakePhotoFragment(R.layout.fragment_take_evidence) {

    private var _binding: FragmentTakeEvidenceBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onTakePhoto(bitmap: Bitmap) {
        viewModel.evidenceImage = bitmap
        findNavController().navigate(R.id.action_takeEvidenceFragment_to_doneExchangeFragment)
    }

    override fun onLoadImage(bitmap: Bitmap) {
        viewModel.evidenceImage = bitmap
        findNavController().navigate(R.id.action_takeEvidenceFragment_to_doneExchangeFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTakeEvidenceBinding.bind(view)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}