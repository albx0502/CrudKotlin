package com.example.crudkotlin.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crudkotlin.model.User
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminUserManagementScreen(
    onUserEdit: (String) -> Unit,
    onUserDelete: (String) -> Unit
) {
    var users by remember { mutableStateOf(listOf<User>()) }
    var showDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<String?>(null) }

    // Cargar los usuarios desde Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener { result ->
                users = result.map { document ->
                    User(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        email = document.getString("email") ?: "",
                        avatarChoice = document.getString("avatarChoice") ?: "avatar1",
                        role = document.getString("role") ?: "user"
                    )
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn {
            items(users) { user ->
                AdminUserCard(
                    user = user,
                    onEditUser = { onUserEdit(user.id) },
                    onDeleteUser = {
                        userToDelete = user.id
                        showDialog = true
                    }
                )
            }
        }
    }

    // Diálogo de confirmación para eliminar usuario
    if (showDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Seguro que quieres eliminar este usuario? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        onUserDelete(userToDelete!!)
                        showDialog = false
                        users = users.filter { it.id != userToDelete } // Actualizar la lista
                        userToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
