package com.multitodo.app.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.multitodo.app.composables.AppTopBar
import com.multitodo.app.ui.theme.MULTITODOAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Estados para la visibilidad de las contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = "Crear Cuenta")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Crea tu cuenta para empezar", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // CAMPO CONTRASEÑA CON OJITO
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                    }
                },
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // CAMPO CONFIRMAR CONTRASEÑA CON OJITO
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Verificar Contraseña") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                    }
                },
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    val finalEmail = email.trim()
                    val finalPassword = password.trim()

                    if (finalEmail.isBlank() || finalPassword.isBlank()) {
                        Toast.makeText(context, "Correo y contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (finalPassword != confirmPassword.trim()) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    auth.createUserWithEmailAndPassword(finalEmail, finalPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrarse")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes una cuenta? Inicia Sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MULTITODOAppTheme {
        RegisterScreen(rememberNavController(), Firebase.auth)
    }
}
