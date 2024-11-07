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
import androidx.compose.ui.platform.LocalContext
import com.example.crudkotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    var description by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember { mutableStateOf(TextFieldValue(user?.displayName ?: "")) }
    var avatarChoice by remember { mutableStateOf("avatar1") } // Avatar predeterminado
    var saveError by remember { mutableStateOf<String?>(null) }

    // Cargar el perfil del usuario, incluyendo el avatar seleccionado
    LaunchedEffect(Unit) {
        loadUserProfile(
            onProfileLoaded = { descriptionText, nameText, avatarSelected ->
                description = TextFieldValue(descriptionText)
                name = TextFieldValue(nameText)
                avatarChoice = avatarSelected ?: "avatar1"
            },
            onError = { error -> saveError = error }
        )
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

        // Selector de avatares
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

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para editar el nombre
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para descripción personal
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar cambios
        Button(
            onClick = {
                saveUserProfile(name.text, description.text, avatarChoice,
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
    }
}

fun getAvatarResource(avatarChoice: String): Int {
    return when (avatarChoice) {
        "avatar1" -> R.drawable.avatar1 // Cambia estos nombres por los nombres de tus archivos
        "avatar2" -> R.drawable.avatar2
        "avatar3" -> R.drawable.avatar3
        "avatar4" -> R.drawable.avatar4
        "avatar5" -> R.drawable.avatar5
        else -> R.drawable.default_profile_image
    }
}

fun saveUserProfile(name: String, description: String, avatarChoice: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    if (userId != null) {
        val userProfile = hashMapOf(
            "name" to name,
            "description" to description,
            "avatarChoice" to avatarChoice
        )

        db.collection("users").document(userId)
            .set(userProfile)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error al guardar") }
    } else {
        onError("Usuario no autenticado")
    }
}

fun loadUserProfile(onProfileLoaded: (String, String, String?) -> Unit, onError: (String) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    if (userId != null) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val description = document.getString("description") ?: ""
                val name = document.getString("name") ?: ""
                val avatarChoice = document.getString("avatarChoice")
                onProfileLoaded(description, name, avatarChoice)
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error al cargar") }
    } else {
        onError("Usuario no autenticado")
    }
}
