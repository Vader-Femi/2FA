package com.femi.a2fa.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.femi.a2fa.R
import com.femi.a2fa.database.entity.OTPData
import com.femi.a2fa.databinding.ItemAccountsBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class AllSavedOTPAdapter(
    private val navigateToOTP: (OTPData) -> Unit
) : ListAdapter<OTPData, AllSavedOTPAdapter.AllSavedOTPViewHolder>(COMPARATOR){

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<OTPData>() {
            override fun areItemsTheSame(oldItem: OTPData, newItem: OTPData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: OTPData, newItem: OTPData): Boolean {
                return ((oldItem.id == newItem.id) &&
                        (oldItem.accountName == newItem.accountName) &&
                        (oldItem.secretKey == newItem.secretKey))
            }
        }
    }

    inner class AllSavedOTPViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAccountsBinding.bind(itemView)
        val tvAccount: MaterialTextView = binding.tvAccount
        val cardAccount: MaterialCardView = binding.cardAccount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllSavedOTPAdapter.AllSavedOTPViewHolder {
        return AllSavedOTPViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_accounts, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AllSavedOTPViewHolder, position: Int) {
        getItem(position).let { account ->
            holder.apply {
                tvAccount.text = account.accountName
                cardAccount.setOnClickListener {
                    navigateToOTP(account)
                }
            }

        }
    }

}
