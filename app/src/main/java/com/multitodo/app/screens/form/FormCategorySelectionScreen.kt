package com.multitodo.app.screens.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.multitodo.app.composables.AppTopBar
import com.multitodo.app.ui.theme.MULTITODOAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormCategorySelectionScreen(navController: NavController) {
    Scaffold(
        topBar = { AppTopBar(navController = navController, title = "Seleccionar Categoría") }
    ) { paddingValues ->
        val categories = listOf("MULTISALUD", "MULTISMART", "MULTISTORE")
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (category == "MULTISTORE") {
                                navController.navigate("professional_form?category=$category")
                            } else {
                                navController.navigate("form_specialty_selection/$category")
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(text = category, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormCategorySelectionScreenPreview() {
    MULTITODOAppTheme {
        FormCategorySelectionScreen(rememberNavController())
    }
}
