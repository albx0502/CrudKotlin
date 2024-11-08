package com.example.crudkotlin.utils

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth

fun deleteUser(
    userId: String,
    userEmail: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // Primero elimina el documento del usuario en Firestore
    db.collection("users").document(userId)
        .delete()
        .addOnSuccessListener {
            // Luego elimina al usuario de Authentication
            deleteUserFromAuthAsAdmin(userEmail, onSuccess, onError)
        }
        .addOnFailureListener { e ->
            onError(e.message ?: "Error al eliminar usuario de Firestore")
        }
}

// Función para eliminar el usuario de Firebase Authentication
fun deleteUserFromAuthAsAdmin(
    userEmail: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    // Inicia sesión temporalmente en la cuenta del usuario con su correo y contraseña conocida
    auth.signInWithEmailAndPassword(userEmail, "123456789")
        .addOnSuccessListener {
            // Si el inicio de sesión es exitoso, elimina al usuario autenticado
            auth.currentUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Después de eliminar al usuario, vuelve a autenticar al administrador original
                        auth.signInWithEmailAndPassword("albx0502rodriguez@gmail.com", "123456789")
                            .addOnSuccessListener {
                                // Llama a onSuccess una vez el usuario esté eliminado y el administrador haya vuelto a iniciar sesión
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onError("Error al volver a iniciar sesión como administrador: ${e.message}")
                            }
                    } else {
                        onError("Error al eliminar usuario de Authentication")
                    }
                }
        }
        .addOnFailureListener { e ->
            onError("Error al iniciar sesión temporalmente: ${e.message}")
        }
}