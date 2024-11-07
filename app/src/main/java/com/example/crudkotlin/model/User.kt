package com.example.crudkotlin.model

data class User(
    var id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val description: String = ""
)