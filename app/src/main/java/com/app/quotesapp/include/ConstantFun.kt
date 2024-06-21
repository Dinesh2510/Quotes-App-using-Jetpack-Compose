package com.app.quotesapp.include

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.quotesapp.R
import com.app.quotesapp.data.models.QuotesResult
import kotlin.random.Random

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@Composable
fun SimpleLightTopAppBar(title: String) {

    val act = LocalContext.current as Activity
    TopAppBar(
        title = {
            Text(title, style = MaterialTheme.typography.titleMedium)
        },
        navigationIcon = {
            IconButton(onClick = {
                act.finish()
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton(onClick = { /* doSomething */ }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "")
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )

    )

}

fun shareQuote(quotesResult: QuotesResult, context: Context) {

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Sharing Quote written by -${quotesResult.author} \n${quotesResult.content}. \uD83D\uDCDA✨ Check out this Android Jetpack Compose App. #Quotes #Inspiration #AndroidApp")
        type = "text/*"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Quote....."))
}

@Composable
fun generateRandomGradient(): List<Color> {
    val random = Random
    val color1 = Color(random.nextFloat(), random.nextFloat(), random.nextFloat())
    val color2 = Color(random.nextFloat(), random.nextFloat(), random.nextFloat())
    return listOf(color1, color2)
}
val kalamFamily = FontFamily(
    Font(R.font.kalam_bold, FontWeight.Bold),
    Font(R.font.kalam_regular, FontWeight.Normal),

)