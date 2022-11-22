package com.example.splitthebill.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.splitthebill.R
import com.example.splitthebill.model.Participation

class MonetaryOperationAdapter(
    context: Context,
    private val participationList: MutableList<Participation>
) : ArrayAdapter<Participation>(context, R.layout.tile_participation, participationList) {

    private data class TileParticipationHolder(val nameTv: TextView, val valueTv: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val participation = participationList[position]

        var participationTileView = convertView

        if (participationTileView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            participationTileView = inflater.inflate(
                R.layout.tile_participation,
                parent,
                false
            )

            val tileParticipationHolder =  TileParticipationHolder(
                participationTileView.findViewById(R.id.nameTv),
                participationTileView.findViewById(R.id.valueTv),
            )

            participationTileView.tag = tileParticipationHolder
        }

        with(participationTileView?.tag as TileParticipationHolder) {
            nameTv.text = participation.name

            val u: Long = participation.value/100
            val d: Long = participation.value%100
            valueTv.text = String.format(context.getString(R.string.monetary_value), u, d)
        }

        return participationTileView
    }
}