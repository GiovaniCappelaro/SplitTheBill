package com.example.splitthebill.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.splitthebill.R
import com.example.splitthebill.adapter.ItemAdapter
import com.example.splitthebill.databinding.ActivityItemBinding
import com.example.splitthebill.databinding.ActivityParticipationBinding
import com.example.splitthebill.model.Item
import com.example.splitthebill.model.Model
import com.example.splitthebill.model.Model.EXTRA_ITEM
import com.example.splitthebill.model.Model.VIEW_ITEM
import com.example.splitthebill.model.Participation
import kotlin.random.Random

class ItemActivity : AppCompatActivity() {

    private val binding: ActivityItemBinding by lazy {
        ActivityItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val receivedItem = intent.getParcelableExtra<Item>(EXTRA_ITEM)
        val viewItem = intent.getBooleanExtra(VIEW_ITEM, false)
        val ctx = this;

        receivedItem?.let { item -> with(binding) {
            val u: Long = item.value/100
            val d: Long = item.value%100
            val v: String = String.format(ctx.getString(R.string.monetary_value), u, d)

            binding.nameEt.setText(item.name)
            binding.valueEt.setText(v)
        }}

        if (viewItem) {
            binding.nameEt.isEnabled = false
            binding.valueEt.isEnabled = false
            binding.saveBt.visibility = View.GONE
        }

        binding.saveBt.setOnClickListener {
            val monetaryValue = binding.valueEt.text.toString().replace("R$ ", "")
            val monetaryValueParts = monetaryValue.split(",")

            var value = receivedItem?.value ?: 0

            if (monetaryValueParts.size == 1) {
                val d = monetaryValueParts[0].toLong()

                value = d*100
            } else if (monetaryValueParts.size == 2) {
                val d = monetaryValueParts[0].toLong()
                val u = monetaryValueParts[1].toLong()

                value = d*100 + u
            }

            val item = Item(
                id    = receivedItem?.id ?: Random(System.currentTimeMillis()).nextInt(),
                name  = binding.nameEt.text.toString(),
                value = value,
            )

            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_ITEM, item)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}