package com.example.crudkotlin.model

data class User(
    var id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarChoice: String = "avatar1", // Agregamos avatarChoice
    val description: String = "",
    val role: String = "user" // Rol por defecto
)
