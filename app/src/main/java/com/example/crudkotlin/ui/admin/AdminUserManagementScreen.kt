package com.example.crudkotlin.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crudkotlin.model.User
import com.example.crudkotlin.utils.deleteUser
import com.google.firebase.firestore.FirebaseFirestore
import com.example.crudkotlin.ui.admin.AdminUserCard

@Composable
fun AdminUserManagementScreen(
    onUserEdit: (String) -> Unit,
    onUserDelete: (String) -> Unit
) {
    var users by remember { mutableStateOf(listOf<User>()) }
    var showDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<User?>(null) }
    val db = FirebaseFirestore.getInstance()

    // Función para refrescar la lista de usuarios
    fun refreshUsers() {
        fetchUsers { fetchedUsers -> users = fetchedUsers }
    }

    // Cargar los usuarios desde Firestore
    LaunchedEffect(Unit) {
        refreshUsers()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        LazyColumn {
            items(users) { user ->
                AdminUserCard(
                    user = user,
                    onEditUser = { onUserEdit(user.id) },
                    onDeleteUser = {
                        userToDelete = user
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
                        userToDelete?.let { user ->
                            deleteUser(
                                userId = user.id,
                                userEmail = user.email,
                                onSuccess = {
                                    refreshUsers()  // Actualiza la lista después de eliminar
                                    showDialog = false
                                },
                                onError = {
                                    // Opcional: muestra un mensaje de error si es necesario
                                    showDialog = false
                                }
                            )
                        }
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

// Función auxiliar para obtener la lista de usuarios de Firestore
fun fetchUsers(onUsersFetched: (List<User>) -> Unit) {
    FirebaseFirestore.getInstance().collection("users")
        .get()
        .addOnSuccessListener { result ->
            val users = result.map { document ->
                User(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    email = document.getString("email") ?: "",
                    avatarChoice = document.getString("avatarChoice") ?: "avatar1",
                    role = document.getString("role") ?: "user"
                )
            }
            onUsersFetched(users)
        }
}
