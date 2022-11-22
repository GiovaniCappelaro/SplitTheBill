package com.example.splitthebill.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.splitthebill.adapter.TransactionAdapter
import com.example.splitthebill.databinding.ActivitySplittedBillBinding
import com.example.splitthebill.model.Model
import com.example.splitthebill.model.Transaction
import com.example.splitthebill.model.TransactionList

class SplittedBillActivity: AppCompatActivity() {

    private val binding: ActivitySplittedBillBinding by lazy {
        ActivitySplittedBillBinding.inflate(layoutInflater)
    }

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val receivedTransactionList = intent.getParcelableExtra<TransactionList>(Model.EXTRA_TRANSACTION_LIST)

        Log.d("MY", receivedTransactionList.toString())

        transactionAdapter = TransactionAdapter(this, receivedTransactionList?.list ?: listOf())
        binding.transactionLv.adapter = transactionAdapter
    }
}