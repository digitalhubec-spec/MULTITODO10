package com.multitodo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.multitodo.app.listings.FormularioDetailScreen
import com.multitodo.app.listings.ListingsScreen
import com.multitodo.app.screens.AboutScreen
import com.multitodo.app.screens.CityListScreen
import com.multitodo.app.screens.LoginScreen
import com.multitodo.app.screens.MainMenuScreen
import com.multitodo.app.screens.MultiStoreScreen
import com.multitodo.app.screens.ProfessionsScreen
import com.multitodo.app.screens.RegisterScreen
import com.multitodo.app.screens.SpecialtiesScreen
import com.multitodo.app.screens.admin.AdminFormDetailScreen
import com.multitodo.app.screens.admin.AdminFormEditScreen
import com.multitodo.app.screens.admin.AdminHostScreen
import com.multitodo.app.screens.form.FormCategorySelectionScreen
import com.multitodo.app.screens.form.FormSpecialtySelectionScreen
import com.multitodo.app.screens.form.ProfessionalFormScreen
import com.multitodo.app.ui.theme.MULTITODOAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // La pantalla de splash se debe instalar ANTES de super.onCreate
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        auth = Firebase.auth
        enableEdgeToEdge()
        
        setContent {
            MULTITODOAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(navController = navController, auth = auth)
                        }
                        composable("register") {
                            RegisterScreen(navController = navController, auth = auth)
                        }
                        composable("about") {
                            AboutScreen(navController = navController)
                        }
                        composable("admin") {
                            AdminHostScreen(navController = navController)
                        }
                        composable(
                            "admin_form_detail/{formId}",
                            arguments = listOf(navArgument("formId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            AdminFormDetailScreen(navController, backStackEntry.arguments?.getString("formId"))
                        }
                         composable(
                            "admin_form_edit/{formId}",
                            arguments = listOf(navArgument("formId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            AdminFormEditScreen(navController, backStackEntry.arguments?.getString("formId"))
                        }
                        composable("main_menu") {
                            MainMenuScreen(navController = navController)
                        }
                        composable(
                            "city_list/{categoryName}",
                            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val categoryName = backStackEntry.arguments?.getString("categoryName")
                            CityListScreen(
                                navController = navController,
                                categoryName = categoryName
                            )
                        }
                        composable(
                            "specialties_list/{cityName}",
                            arguments = listOf(navArgument("cityName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            SpecialtiesScreen(
                                navController = navController,
                                cityName = backStackEntry.arguments?.getString("cityName")
                            )
                        }
                        composable(
                            "professions_list/{cityName}",
                            arguments = listOf(navArgument("cityName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            ProfessionsScreen(
                                navController = navController,
                                cityName = backStackEntry.arguments?.getString("cityName")
                            )
                        }
                         composable(
                            "multistore_list/{cityName}",
                            arguments = listOf(navArgument("cityName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            MultiStoreScreen(
                                navController = navController,
                                cityName = backStackEntry.arguments?.getString("cityName")
                            )
                        }
                        composable(
                            "listings/{city}/{category}/{specialty}",
                            arguments = listOf(
                                navArgument("city") { type = NavType.StringType },
                                navArgument("category") { type = NavType.StringType },
                                navArgument("specialty") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            ListingsScreen(
                                navController = navController,
                                city = backStackEntry.arguments?.getString("city"),
                                category = backStackEntry.arguments?.getString("category"),
                                specialty = backStackEntry.arguments?.getString("specialty")
                            )
                        }
                        composable(
                            "formulario_detail/{formId}",
                            arguments = listOf(navArgument("formId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            FormularioDetailScreen(
                                navController = navController,
                                formId = backStackEntry.arguments?.getString("formId")
                            )
                        }
                        composable("form_category_selection") {
                            FormCategorySelectionScreen(navController = navController)
                        }
                        composable(
                            "form_specialty_selection/{categoryName}",
                            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            FormSpecialtySelectionScreen(
                                navController = navController,
                                categoryName = backStackEntry.arguments?.getString("categoryName")
                            )
                        }
                        composable(
                            "professional_form?category={category}&specialty={specialty}",
                            arguments = listOf(
                                navArgument("category") { type = NavType.StringType; nullable = true },
                                navArgument("specialty") { type = NavType.StringType; nullable = true }
                            )
                        ) { backStackEntry ->
                            ProfessionalFormScreen(
                                navController = navController,
                                category = backStackEntry.arguments?.getString("category"),
                                specialty = backStackEntry.arguments?.getString("specialty")
                            )
                        }
                    }
                }
            }
        }
    }
}
