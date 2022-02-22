package com.gitexplorer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Owner(
    val id: Int,
    val login: String,
    val avatar_url: String
): Parcelable