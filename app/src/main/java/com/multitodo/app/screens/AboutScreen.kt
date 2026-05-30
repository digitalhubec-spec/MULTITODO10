package com.multitodo.app.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = "Sobre Nosotros")
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("¡Bienvenido a MULTITODO!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Somos un HUB Digital nacido en Milagro, Ecuador, con una misión clara: conectar a nuestra comunidad. Creemos en el poder de lo local y en la importancia de encontrar soluciones rápidas y confiables cerca de ti.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("¿Cómo Funciona?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nuestra plataforma está organizada en tres grandes categorías para que encuentres justo lo que necesitas:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("• MULTISALUD:", fontWeight = FontWeight.Bold)
                Text("Aquí encontrarás médicos, especialistas y todos los servicios para cuidar tu salud y la de tus mascotas.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("• MULTISMART:", fontWeight = FontWeight.Bold)
                Text("Conecta con una red de expertos y asesores para impulsar tus proyectos y requerimientos profesionales. Desde abogados y contadores hasta ingenieros y diseñadores.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("• MULTISTORE:", fontWeight = FontWeight.Bold)
                Text("Descubre y contacta con las mejores tiendas y emprendimientos de tu ciudad. Todo lo que necesitas, en un solo lugar.")
                Spacer(modifier = Modifier.height(24.dp))
                Text("Nuestro Fin", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "En MULTITODO, nuestro fin es doble: facilitarle la vida a nuestros usuarios, ofreciéndoles un directorio de profesionales y servicios de confianza, y al mismo tiempo, darle una plataforma a los negocios locales para que puedan crecer y llegar a más personas.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("¡Gracias por ser parte de esta comunidad!", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    MULTITODOAppTheme {
        AboutScreen(rememberNavController())
    }
}
