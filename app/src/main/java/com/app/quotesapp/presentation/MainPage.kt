package com.app.quotesapp.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.quotesapp.ui.theme.QuotesAppTheme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.quotesapp.data.viewmodel.QuotesViewModel
import com.app.quotesapp.R
import com.app.quotesapp.include.QuotesState
import com.app.quotesapp.data.models.QuotesResult
import com.app.quotesapp.data.models.ResponseQuotes
import com.app.quotesapp.include.kalamFamily
import com.app.quotesapp.include.pagerHingeTransition
import com.app.quotesapp.include.rememberRandomSampleImageUrl
import com.app.quotesapp.include.shareQuote
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainPage : ComponentActivity() {
    private val viewModel: QuotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuotesAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    // Initialize the NavController
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()

                    val currentDestination = navBackStackEntry?.destination
                    Scaffold(bottomBar = {
                        // Create Bottom Navigation Bar
                        BottomNavigationBar(navController = navController, currentDestination)
                    }) { paddingVal ->
                        // Create NavHost to navigate between tabs
                        NavHost(
                            modifier = Modifier.padding(paddingVal),
                            navController = navController,
                            startDestination = BottomNavScreens.Home.route
                        ) {
                            // Define screens for each tab
                            composable(BottomNavScreens.Home.route) {
                                HomeScreen(navController = navController, viewModel)
                            }

                            composable(BottomNavScreens.Quote.route) {
                                QuoteScreen(navController = navController, viewModel)
                            }

                            composable(BottomNavScreens.About.route) {
                                AboutScreen(navController = navController)
                            }

                        }
                    }
                }
            }
        }
    }
}

sealed class BottomNavScreens(val route: String) {
    object Home : BottomNavScreens("home")
    object Quote : BottomNavScreens("quotes")
    object About : BottomNavScreens("about")
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentDestination: NavDestination?) {
    val items = listOf(
        BottomNavScreens.Home, BottomNavScreens.Quote, BottomNavScreens.About
    )

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(icon = {
                when (screen) {
                    BottomNavScreens.Home -> Icon(
                        Icons.Default.Home, contentDescription = null, tint = Color.White
                    )

                    BottomNavScreens.Quote -> Icon(
                        Icons.Default.FormatQuote, contentDescription = null, tint = Color.White
                    )

                    BottomNavScreens.About -> Icon(
                        Icons.Default.Info, contentDescription = null, tint = Color.White
                    )
                }
            },
                label = { Text(screen.route.capitalize(), color = Color.White) },
                selected = currentDestination?.hierarchy?.any {
                    it.route == screen.route
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                })
        }
    }
}


@Composable
fun HomeScreen(navController: NavHostController, viewModel: QuotesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Quote of the Day", fontStyle = FontStyle.Italic, fontSize = 30.sp)
        RandomQuoteScreen(viewModel = viewModel)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp, 0.dp, 0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp, 24.dp)
                    .clickable(onClick = {
                        viewModel.fetchRandomQuotes()
                    }),
                tint = colorResource(id = R.color.white)
            )
        }
    }
}

@Composable
fun QuoteScreen(navController: NavHostController, viewModel: QuotesViewModel) {
    val quoteState by viewModel.quoteResponse.collectAsState()
    when (quoteState) {
        is QuotesState.Success -> {
            // Handle success state for regular quotes
            val quoteData = (quoteState as QuotesState.Success<ResponseQuotes?>).data
            // Render UI with quoteData
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalPagerWithFadeTransition(quoteData!!.quotesResults)
            }

        }

        is QuotesState.Error -> {
            // Handle error state for regular quotes
            val errorMessage = (quoteState as QuotesState.Error).message
            // Render UI with errorMessage
            ShowError(errorMessage)
        }

        is QuotesState.Loading -> {
            // Handle loading state for regular quotes
            // Render loading indicator or any UI
            CenteredCircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavHostController) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quote App") },
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )

                Text(
                    text = "Welcome to Quote App!",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "by PixelDev",
                    style = TextStyle(fontSize = 18.sp, fontStyle = FontStyle.Italic),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Opening the Source code link",
                            Toast.LENGTH_SHORT
                        ).show()
                        uriHandler.openUri("https://github.com/Dinesh2510/")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Source Code")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OptionItem(icon = Icons.Default.Home, text = "Home")
                    OptionItem(icon = Icons.Default.MailOutline, text = "Mail")
                    OptionItem(icon = Icons.Default.Info, text = "Info")
                    // Add more options as needed
                }
            }
        }
    )


}

@Composable
fun OptionItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Text(text = text)
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}

@Composable
fun RandomQuoteScreen(viewModel: QuotesViewModel) {
    val randomQuoteState by viewModel.randomQuoteResponse.collectAsState()
    val quotesResult = randomQuoteState
    QuoteCardRandom(quotesResult)
}

@Composable
fun QuoteCardRandom(randomQuoteState: QuotesState<QuotesResult?>) {
    Log.e("TAG_randomQuoteState", "QuoteCardRandom: "+randomQuoteState )
    when (randomQuoteState) {
        is QuotesState.Success -> {
            // Handle success state for random quotes
            val randomQuoteData = (randomQuoteState as QuotesState.Success<QuotesResult?>).data
            // Render UI with randomQuoteData
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        //GradientStart: “#D9FF43”, GradientCenter: “#F67831”, GradientEnd: “#FF1493”
                        //generateRandomGradient()
                        brush = Brush.verticalGradient(
                            0.0f to Color.Magenta, 1.0f to Color.Cyan, startY = 0.0f, endY = 1500.0f
                        )
                    )
            ) {
                // Card content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = randomQuoteData!!.content,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = kalamFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "- ${randomQuoteData.author}",
                        fontFamily = kalamFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                // Quote icon button partially outside the card
                Icon(imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .align(Alignment.BottomCenter)
                        .background(Color.Transparent)
                        .clickable {
                            // Handle click action
                        })
            }

        }

        is QuotesState.Error -> {
            // Handle error state for random quotes
            val errorMessage = (randomQuoteState as QuotesState.Error).message
            // Render UI with errorMessage
            ShowError(errorMessage)
        }

        is QuotesState.Loading -> {
            // Handle loading state for random quotes
            // Render loading indicator or any UI
            CenteredCircularProgressIndicator()
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerWithFadeTransition(
    quotesResultListing: List<QuotesResult>,
) {
    Column {
        val pagerState = rememberPagerState(pageCount = { quotesResultListing.size })
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            state = pagerState,
            beyondBoundsPageCount = 2
        ) { page ->
            QuoteUI(pagerState, page, quotesResultListing)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuoteUI(pagerState: PagerState, page: Int, quotesResultListing: List<QuotesResult>) {
    Card(modifier = Modifier
        .clickable {}
        .pagerHingeTransition(page, pagerState),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .alpha(0.6f),
                    contentScale = ContentScale.Crop,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(rememberRandomSampleImageUrl(width = 1200))
                        .placeholder(R.drawable.placeholder).allowHardware(false)
                        .error(R.drawable.placeholder_error).build(),
                    contentDescription = stringResource(R.string.app_name),
                )
                Column(
                    modifier = Modifier.padding(
                        top = 70.dp, end = 30.dp, start = 30.dp, bottom = 25.dp
                    ),
                    verticalArrangement = Arrangement.Center,
                ) {

                    Text(
                        text = quotesResultListing[page].content.uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            shadow = Shadow(
                                color = Color.Black, offset = Offset(0.0f, 0.0f), blurRadius = 10f
                            )
                        ),
                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                    )

                    Text(
                        text = quotesResultListing[page].author,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.SansSerif, shadow = Shadow(
                                color = Color.Black, offset = Offset(0.0f, 0.0f), blurRadius = 25f
                            )
                        ),
                        color = Color.White
                    )
                }
                QuoteBottomUI(page, quotesResultListing)
            }
        }
    }

}

@Composable
fun QuoteBottomUI(page: Int, quotesResultListing: List<QuotesResult>) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = CircleShape)
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                shareQuote(quotesResultListing[page], context)
            },
            modifier = Modifier
                .size(55.dp)
                .align(Alignment.BottomCenter),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White),
            contentPadding = PaddingValues(0.dp),  //avoid the little icon
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.Share, contentDescription = "content description")
        }
    }

}


@Composable
fun ShowError(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)

            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = Icons.Default.SignalWifiStatusbarConnectedNoInternet4,
                contentDescription = "contentDescription",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
            )
            Text(message)
        }

    }

}


@Composable
fun CenteredCircularProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(50.dp) // Set the size of the CircularProgressIndicator
                .align(Alignment.Center) // Align it to the center of the Box
        )
    }
}