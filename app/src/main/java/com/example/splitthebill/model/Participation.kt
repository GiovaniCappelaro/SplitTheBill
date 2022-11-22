package com.example.splitthebill.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Participation(
    val id: Int,
    val name: String,
    val value: Long,
    val items: MutableList<Item>,
): Parcelable