package com.example.splitthebill.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.splitthebill.R
import com.example.splitthebill.model.Item

class ItemAdapter(
    context: Context,
    private val itemList: MutableList<Item>
) : ArrayAdapter<Item>(context, R.layout.tile_item, itemList) {

    private data class TileItemHolder(val nameTv: TextView, val valueTv: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = itemList[position]

        var itemTileView = convertView

        if (itemTileView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            itemTileView = inflater.inflate(
                R.layout.tile_participation,
                parent,
                false
            )

            val tileItemHolder =  TileItemHolder(
                itemTileView.findViewById(R.id.nameTv),
                itemTileView.findViewById(R.id.valueTv),
            )

            itemTileView.tag = tileItemHolder
        }

        with(itemTileView?.tag as TileItemHolder) {
            nameTv.text = item.name

            val u: Long = item.value/100
            val d: Long = item.value%100
            valueTv.text = String.format(context.getString(R.string.monetary_value), u, d)
        }

        return itemTileView
    }
}