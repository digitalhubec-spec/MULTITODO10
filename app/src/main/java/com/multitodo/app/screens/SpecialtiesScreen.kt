package com.multitodo.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.multitodo.app.composables.AppTopBar
import com.multitodo.app.ui.theme.MULTITODOAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialtiesScreen(navController: NavController, cityName: String?) {
    val specialties = listOf(
        "TRAUMATOLOGÍA", "VETERINARIA", "ODONTOLOGÍA", "CARDIOLOGÍA",
        "DERMATOLOGÍA", "PEDIATRÍA", "GINECOLOGÍA", "OFTALMOLOGÍA",
        "PSICOLOGÍA", "NUTRICIÓN"
    )

    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = "Especialidades en $cityName", showLogout = true)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(specialties) { specialty ->
                Card(
                    modifier = Modifier.clickable { 
                        navController.navigate("listings/$cityName/MULTISALUD/$specialty") 
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp).fillMaxSize()){
                        Text(text = specialty, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpecialtiesScreenPreview() {
    MULTITODOAppTheme {
        SpecialtiesScreen(rememberNavController(), "GUAYAQUIL")
    }
}
