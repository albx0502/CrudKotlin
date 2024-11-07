package com.example.crudkotlin.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crudkotlin.model.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore


@Composable
fun UserSearchScreen(
    onUserSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val db = Firebase.firestore

    // Cargar los datos de Firestore
    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                users = result.map { document ->
                    User(
                        id = document.id,
                        email = document.getString("email") ?: "", // Usar el campo de correo
                        description = document.getString("description") ?: ""
                    )
                }
                errorMessage = null
            }
            .addOnFailureListener { e ->
                errorMessage = e.message ?: "Error al cargar usuarios"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(users.filter { it.email.contains(searchQuery, ignoreCase = true) }) { user ->
                    UserCard(user = user, onClick = { onUserSelected(user.id) })
                }
            }
        }
    }
}
