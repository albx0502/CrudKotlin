package com.example.crudkotlin.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(
    onPasswordResetSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
){
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var resetError by remember { mutableStateOf<String?>(null) }
    var resetSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Recuperar contraseña", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.text.isNotEmpty()) {
                    resetPassword(email.text,
                        onPasswordResetSuccess = {
                            resetSuccess = true
                            resetError = null
                        },
                        onError = { error ->
                            resetSuccess = false
                            resetError = error
                        }
                    )
                } else {
                    resetError = "Por favor, ingresa un correo electrónico válido"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar correo de recuperación")
        }


        Spacer(modifier = Modifier.height(8.dp))

        resetError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (resetSuccess) {
            Text(
                text = "Correo de recuperación enviado. Por favor revisa tu bandeja.",
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text("Volver a Inicio de Sesión")
            }
        }
    }
}
private fun resetPassword(email: String, onPasswordResetSuccess: () -> Unit, onError: (String) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onPasswordResetSuccess()
            } else {
                onError(task.exception?.message ?: "Error al enviar correo de recuperación")
            }
        }
}