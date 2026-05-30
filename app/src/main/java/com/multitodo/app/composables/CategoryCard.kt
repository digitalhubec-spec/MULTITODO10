package com.multitodo.app.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Category(val name: String, val imageRes: Int, val description: String)

@Composable
fun CategoryCard(category: Category, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .aspectRatio(0.9f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xAA000000)),
                            startY = 250f
                        )
                    )
            )
            IconButton(
                onClick = { 
                    Toast.makeText(context, category.description, Toast.LENGTH_LONG).show()
                 },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color.White
                )
            }
            Text(
                text = category.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 12.dp, end = 24.dp)
            )
        }
    }
}
