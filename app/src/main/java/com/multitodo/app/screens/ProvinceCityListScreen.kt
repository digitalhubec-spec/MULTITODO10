package com.multitodo.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.multitodo.app.composables.AppTopBar
import com.multitodo.app.utils.EcuadorData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvinceCityListScreen(navController: NavController, categoryName: String?, provinceName: String?) {
    val cities = EcuadorData.ciudadesPorProvincia[provinceName] ?: emptyList()

    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = provinceName ?: "Ciudades", showLogout = true)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Ciudades en $provinceName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(cities) { city ->
                val cityUpper = city.uppercase()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (categoryName) {
                                "MULTISALUD" -> navController.navigate("specialties_list/$cityUpper")
                                "MULTISMART" -> navController.navigate("professions_list/$cityUpper")
                                "MULTISTORE" -> navController.navigate("multistore_list/$cityUpper")
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
