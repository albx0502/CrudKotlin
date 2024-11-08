package com.example.crudkotlin.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.crudkotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.crudkotlin.utils.getAvatarResource


@Composable
fun UserProfileScreen(
    userId: String,
    onBack: () -> Unit,
    isEditable: Boolean = false // Añadir el parámetro isEditable con un valor por defecto
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var avatarChoice by remember { mutableStateOf("avatar1") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var saveError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        loadUserProfile(
            userId = userId,
            onProfileLoaded = { loadedName, loadedEmail, loadedAvatarChoice, loadedDescription ->
                name = loadedName
                email = loadedEmail
                avatarChoice = loadedAvatarChoice ?: "avatar1"
                description = loadedDescription
            },
            onError = { error ->
                errorMessage = error
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mostrar imagen de perfil
        Image(
            painter = painterResource(id = getAvatarResource(avatarChoice)),
            contentDescription = "Imagen de perfil",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditable) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    saveUserProfile(
                        userId = userId,
                        name = name,
                        description = description,
                        avatarChoice = avatarChoice,
                        onSuccess = { saveError = "Cambios guardados" },
                        onError = { error -> saveError = error }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }

            saveError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = if (it == "Cambios guardados") Color.Green else Color.Red)
            }
        } else {
            Text(text = "Nombre: $name", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Correo: $email", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Descripción: $description", style = MaterialTheme.typography.bodyMedium)
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

// Guarda el perfil actualizado
fun saveUserProfile(
    userId: String,
    name: String,
    description: String,
    avatarChoice: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = Firebase.firestore
    val userProfile = mapOf(
        "name" to name,
        "description" to description,
        "avatarChoice" to avatarChoice
    )

    db.collection("users").document(userId)
        .update(userProfile)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onError(e.message ?: "Error al guardar") }
}


fun loadUserProfile(
    userId: String,
    onProfileLoaded: (name: String, email: String, avatarChoice: String?, description: String) -> Unit,
    onError: (String) -> Unit
) {
    val db = Firebase.firestore
    db.collection("users").document(userId)
        .get()
        .addOnSuccessListener { document ->
            val name = document.getString("name") ?: "Nombre no disponible"
            val email = document.getString("email") ?: "Email no disponible"
            val avatarChoice = document.getString("avatarChoice") ?: "avatar1"
            val description = document.getString("description") ?: "Descripción no disponible"
            onProfileLoaded(name, email, avatarChoice, description)
        }
        .addOnFailureListener { e ->
            onError(e.message ?: "Error al cargar el perfil del usuario")
        }
}
