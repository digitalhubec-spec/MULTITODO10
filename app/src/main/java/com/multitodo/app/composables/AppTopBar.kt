package com.multitodo.app.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    title: String = "",
    @DrawableRes titleImage: Int? = null,
    showBackButton: Boolean = true,
    showLogout: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (titleImage != null) {
                AsyncImage(
                    model = titleImage,
                    contentDescription = "Logo",
                    modifier = Modifier.height(32.dp) // Adjust size as needed
                )
            } else {
                Text(title, fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        },
        actions = {
            if (showLogout && Firebase.auth.currentUser != null) {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(text = { Text("Cerrar Sesión") }, onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    })
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}
