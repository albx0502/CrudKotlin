package com.example.crudkotlin.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.crudkotlin.model.User
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserProfileScreen(userId: String) {
    val db = FirebaseFirestore.getInstance()
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                user = document.toObject(User::class.java)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        user?.let {
            Text("Nombre: ${it.name}")
            Spacer(modifier = Modifier.height(16.dp))
            it.profileImageUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier.size(100.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Descripción: ${it.description}")
        } ?: Text("Cargando...")
    }
}
