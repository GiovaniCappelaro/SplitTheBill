package com.example.splitthebill.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.splitthebill.R
import com.example.splitthebill.model.Transaction

class TransactionAdapter(
    context: Context,
    private val transactionList: List<Transaction>
) : ArrayAdapter<Transaction>(context, R.layout.tile_transaction, transactionList) {

    private data class TileTransactionHolder(val nameTv: TextView, val valueTv: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d("MY", position.toString())

        val transaction = transactionList[position]

        var transactionTileView = convertView

        if (transactionTileView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            transactionTileView = inflater.inflate(
                R.layout.tile_transaction,
                parent,
                false
            )

            val tileTransactionHolder =  TileTransactionHolder(
                transactionTileView.findViewById(R.id.nameTv),
                transactionTileView.findViewById(R.id.valueTv),
            )

            transactionTileView.tag = tileTransactionHolder
        }

        with(transactionTileView?.tag as TileTransactionHolder) {
            nameTv.text = transaction.name

            val u: Long = transaction.value/100
            val d: Long = transaction.value%100

            if (transaction.owing) {
                valueTv.text = String.format(context.getString(R.string.neg_monetary_value), u, d)
                valueTv.setTextColor(Color.RED)
            } else {
                valueTv.text = String.format(context.getString(R.string.monetary_value), u, d)
                valueTv.setTextColor(Color.GREEN)
            }
        }

        return transactionTileView
    }
}