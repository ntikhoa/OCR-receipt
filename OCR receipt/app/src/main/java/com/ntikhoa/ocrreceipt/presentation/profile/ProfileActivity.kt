package com.ntikhoa.ocrreceipt.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            Picasso.get()
                .load(viewModel.getAvatarURL())
                .into(ivAvatar)

            btnLogout.setOnClickListener {
                viewModel.logout()
                finish()
            }

            btnHome.setOnClickListener {
                onBackPressed()
            }
        }

        repeatLifecycleFlow {
            viewModel.state.collectLatest { dataState ->
                dataState.profile?.let {
                    binding.apply {
                        tvName.text = it.name
                        tvProvider.text = it.providerName
                        tvUsername.text = "Username: ${it.username}"
                    }
                }

                dataState.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.onTriggerEvent(ProfileEvent.GetProfileAccount)
    }
}