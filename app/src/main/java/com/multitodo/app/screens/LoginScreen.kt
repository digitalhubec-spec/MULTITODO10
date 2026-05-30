package com.multitodo.app.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.multitodo.app.R
import com.multitodo.app.ui.theme.MULTITODOAppTheme
import com.multitodo.app.ui.theme.MultyOnPrimary
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(500)
        showContent = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.height(150.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(1000)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¡Bienvenid@ a nuestro HUB DIGITAL!",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // CAMPO EMAIL CON FONDO SÓLIDO
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        modifier = Modifier.width(280.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // CAMPO PASSWORD CON FONDO SÓLIDO
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            val finalEmail = email.trim()
                            val finalPassword = password.trim()
                            if (finalEmail.isBlank() || finalPassword.isBlank()) {
                                Toast.makeText(context, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            auth.signInWithEmailAndPassword(finalEmail, finalPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (finalEmail == "vicocastillolopez27@gmail.com") {
                                            navController.navigate("admin") { popUpTo("login") { inclusive = true } }
                                        } else {
                                            navController.navigate("main_menu") { popUpTo("login") { inclusive = true } }
                                        }
                                    } else {
                                        Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        },
                        modifier = Modifier.width(280.dp).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Ingresar", color = MultyOnPrimary)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(onClick = { navController.navigate("register") }) {
                        Text("¿No tienes una cuenta? REGISTRARSE")
                    }
                }
            }
        }
    }
}
