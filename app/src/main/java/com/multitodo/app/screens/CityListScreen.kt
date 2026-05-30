package com.multitodo.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.multitodo.app.composables.AppTopBar
import com.multitodo.app.ui.theme.MULTITODOAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(navController: NavController, categoryName: String?) {
    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = categoryName ?: "Ciudades", showLogout = true)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val cities = listOf("MILAGRO", "GUAYAQUIL", "BABAHOYO")
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cities) { city ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (categoryName) {
                                "MULTISALUD" -> navController.navigate("specialties_list/$city")
                                "MULTISMART" -> navController.navigate("professions_list/$city")
                                "MULTISTORE" -> navController.navigate("multistore_list/$city")
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                     Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationCity,
                            contentDescription = "Ciudad",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = city,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityListScreenPreview() {
    MULTITODOAppTheme {
        CityListScreen(rememberNavController(), "MULTISALUD")
    }
}
