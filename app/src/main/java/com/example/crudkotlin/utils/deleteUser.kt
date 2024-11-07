package com.example.crudkotlin.utils

import com.google.firebase.firestore.FirebaseFirestore

fun deleteUser(
    userId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users").document(userId)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onError(e.message ?: "Error al eliminar usuario") }
}
