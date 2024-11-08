package com.example.crudkotlin.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.crudkotlin.model.User

@Composable
fun AdminUserCard(
    user: User,
    onEditUser: () -> Unit,
    onDeleteUser: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEditUser() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Nombre: ${user.name}")
            Text(text = "Correo: ${user.email}")
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "Editar",
                    modifier = Modifier
                        .clickable { onEditUser() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Eliminar",
                    modifier = Modifier
                        .clickable { onDeleteUser() }
                        .padding(8.dp)
                )
            }
        }
    }
}

