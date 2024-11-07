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
    onEditUser: (String) -> Unit,
    onDeleteUser: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onEditUser(user.id) },
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(user.avatarChoice),
                contentDescription = "User Avatar",
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
                Text(text = "Rol: ${user.role}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { onEditUser(user.id) }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit User")
            }

            IconButton(onClick = { onDeleteUser(user.id) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete User")
            }
        }
    }
}
