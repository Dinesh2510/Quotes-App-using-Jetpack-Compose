package com.app.quotesapp.include


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlin.math.absoluteValue

@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerWithFadeTransition(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 10 })
    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        state = pagerState,
        beyondBoundsPageCount = 2
    ) { page ->
        Box(
            Modifier
                .pagerFadeTransition(page, pagerState)
                .fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = rememberRandomSampleImageUrl
                        (width = 1200)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerFadeTransition(page: Int, pagerState: PagerState) =
    graphicsLayer {
        val pageOffset = pagerState.calculateCurrentOffsetForPage(page)
        translationX = pageOffset * size.width
        alpha = 1 - pageOffset.absoluteValue
    }

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerHingeTransition(page: Int, pagerState: PagerState) = graphicsLayer {
    // Calculate the absolute offset for the current page from the
    // scroll position.
    val pageOffset = pagerState.calculateCurrentOffsetForPage(page)
    translationX = pageOffset * size.width
    transformOrigin = TransformOrigin(0f, 0f)

    if (pageOffset < -1f) {
        // page is far off screen
        alpha = 0f
    } else if (pageOffset <= 0) {
        // page is to the right of the selected page or the selected page
        alpha = 1f - pageOffset.absoluteValue
        rotationZ = 0f

    } else if (pageOffset <= 1) {
        // page is to the left of the selected page
        alpha = 1f - pageOffset.absoluteValue
        rotationZ = 90f * pageOffset.absoluteValue
    } else {
        alpha = 0f
    }
}
private val rangeForRandom = (0..100000)

fun randomSampleImageUrl(
    seed: Int = rangeForRandom.random(),
    width: Int = 300,
    height: Int = width,
): String {
    return "https://picsum.photos/seed/$seed/$width/$height"
}

@Composable
fun rememberRandomSampleImageUrl(
    seed: Int = rangeForRandom.random(),
    width: Int = 300,
    height: Int = width,
): String = remember { randomSampleImageUrl(seed, width, height) }