package com.example.crudkotlin.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.res.painterResource
import com.example.crudkotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToUserSearch: () -> Unit,
    userId: String? = null // Parámetero opcional userId para identificar el perfil a cargar
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val isOwnProfile = userId == null // Verifica si es el perfil propio o de otro usuario

    var description by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf("") }
    var avatarChoice by remember { mutableStateOf("avatar1") }
    var saveError by remember { mutableStateOf<String?>(null) }

    // Cargar el perfil del usuario especificado o el perfil propio
    LaunchedEffect(userId) {
        val profileId = userId ?: auth.currentUser?.uid
        profileId?.let {
            loadUserProfile(
                userId = it,
                onProfileLoaded = { descriptionText, nameText, emailText, avatarSelected ->
                    description = TextFieldValue(descriptionText)
                    name = TextFieldValue(nameText)
                    email = emailText ?: ""
                    avatarChoice = avatarSelected ?: "avatar1"
                },
                onError = { error -> saveError = error }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar el avatar seleccionado
        Image(
            painter = painterResource(id = getAvatarResource(avatarChoice)),
            contentDescription = "Imagen de perfil",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Si es el perfil propio, mostrar el selector de avatar
        if (isOwnProfile) {
            Text("Elige tu avatar:")
            Row(horizontalArrangement = Arrangement.Center) {
                listOf("avatar1", "avatar2", "avatar3", "avatar4", "avatar5").forEach { avatar ->
                    IconButton(onClick = { avatarChoice = avatar }) {
                        Image(
                            painter = painterResource(id = getAvatarResource(avatar)),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar nombre, correo y descripción (si es el perfil propio, se puede editar)
        Text(text = "Nombre: ${name.text}", style = MaterialTheme.typography.bodyLarge)
        if (!isOwnProfile) {
            Text(text = "Correo: $email", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isOwnProfile) {
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
                        name.text,
                        description.text,
                        avatarChoice,
                        onSuccess = { saveError = "Cambios guardados" },
                        onError = { error -> saveError = error }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToUserSearch, modifier = Modifier.fillMaxWidth()) {
                Text("Buscar Usuarios")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    auth.signOut()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cerrar sesión")
            }
        } else {
            Text(text = "Descripción: ${description.text}", style = MaterialTheme.typography.bodyMedium)
        }

        saveError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = if (it == "Cambios guardados") Color.Green else Color.Red)
        }
    }
}

// Helper functions
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

fun saveUserProfile(name: String, description: String, avatarChoice: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val email = FirebaseAuth.getInstance().currentUser?.email
    val db = FirebaseFirestore.getInstance()

    if (userId != null && email != null) {
        val userProfile = hashMapOf(
            "name" to name,
            "description" to description,
            "avatarChoice" to avatarChoice,
            "email" to email // Almacenar el correo en Firestore
        )

        db.collection("users").document(userId)
            .set(userProfile)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error al guardar") }
    } else {
        onError("Usuario no autenticado")
    }
}

fun loadUserProfile(
    userId: String,
    onProfileLoaded: (String, String, String?, String?) -> Unit, // Cambiado para mantener el orden correcto de los datos
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    db.collection("users").document(userId)
        .get()
        .addOnSuccessListener { document ->
            val description = document.getString("description") ?: ""
            val name = document.getString("name") ?: ""
            val avatarChoice = document.getString("avatarChoice") ?: "avatar1" // Valor predeterminado si no existe
            val email = document.getString("email") ?: "" // Asegúrate de que el campo email exista en Firestore

            // Llama al callback con los datos, en el orden correcto
            onProfileLoaded(description, name, email, avatarChoice)
        }
        .addOnFailureListener { e ->
            onError(e.message ?: "Error al cargar el perfil")
        }
}


