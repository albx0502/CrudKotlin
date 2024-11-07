package com.example.crudkotlin.utils

import com.example.crudkotlin.R

fun getAvatarResource(avatarChoice: String): Int {
    return when (avatarChoice) {
        "avatar1" -> R.drawable.avatar1
        "avatar2" -> R.drawable.avatar2
        "avatar3" -> R.drawable.avatar3
        "avatar4" -> R.drawable.avatar4
        "avatar5" -> R.drawable.avatar5
        else -> R.drawable.default_profile_image
    }
}
