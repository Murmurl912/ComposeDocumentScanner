package com.example.composedocumentscanner.camera

import androidx.camera.core.CameraControl
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.sign


@Composable
fun CameraView() {
    
}

@Composable
fun ScannerView(modifier: Modifier = Modifier) {
    
    Column(modifier) {

        
        
        
    }
}

@Preview
@Composable
fun ScanControl(
    modifier: Modifier = Modifier,
    preview: @Composable () -> Unit = {
        Image(imageVector = Icons.Default.Image, contentDescription = "")
    },
    onCapture: () -> Unit = {},
    onImport: () -> Unit = {},
    onPreview: () -> Unit = {}
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.weight(1F))
            Icon(Icons.Default.FlashOff, "flash")
            Spacer(modifier = Modifier.weight(1F))
            Icon(Icons.Default.AutoAwesomeMotion, "Auto")
            Spacer(modifier = Modifier.weight(1F))
            Icon(Icons.Default.AspectRatio, "Ratio")
            Spacer(modifier = Modifier.weight(1F))
            Icon(Icons.Default.Camera, "Camera")
            Spacer(modifier = Modifier.weight(1F))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.weight(1F))
            Column {
                Image(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "")
            }
            Spacer(modifier = Modifier.weight(1F))
            Column {
                Canvas(modifier = Modifier.size(48.dp)) {
                    drawOval(Color.Black,
                        topLeft = Offset(size.width * 0.1F, size.height * 0.1F),
                        size = size * 0.8F)
                    drawOval(Color.Black,
                        topLeft = Offset(size.width * 0.025F, size.height * 0.025F),
                        size = size * 0.95f,
                        style = Stroke(width = 0.05F * size.maxDimension))
                }
            }
            Spacer(modifier = Modifier.weight(1F))
            Column {
                preview()
            }
            Spacer(modifier = Modifier.weight(1F))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun FlashControl(modifier: Modifier = Modifier) {
    Column(modifier) {
        Box(Modifier.fillMaxWidth(1F)) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.Clear, contentDescription = "close", modifier = Modifier.padding(10.dp))
            }
            Text(text = "Auto", Modifier.align(Alignment.Center))
        }
        Row(Modifier.fillMaxWidth(1F), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.ArrowLeft, contentDescription = "close")
            }

            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.ArrowRight, contentDescription = "close")
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun CenterAlignLazyRow() {
    val listState = rememberLazyListState()

    val snapFlingBehavior = rememberSnapFlingBehavior((
        listState
    ))
    var lazyRowSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    var firstItemSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    var lastItemSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    val density = LocalDensity.current
    val contentPadding by remember(density) {
        derivedStateOf {
            with(density) {
                val startPadding = ((lazyRowSize.width - firstItemSize.width) / 2F).coerceAtLeast(0F).toDp()
                val endPadding =
                    if (lastItemSize == IntSize.Zero) {
                        startPadding
                    } else {
                        ((lazyRowSize.width - lastItemSize.width) / 2F).coerceAtLeast(0F).toDp()
                    }

                PaddingValues(
                    start =  startPadding,
                    end = endPadding
                )
            }

        }
    }
    println("contentPadding = $contentPadding")
    println("first item size: $firstItemSize")
    println("last item size: $lastItemSize")
    println("lazy row size: $lazyRowSize")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                lazyRowSize = it
            }.drawBehind {
                         drawLine(Color.Black,
                             start = Offset(size.width / 2F - 2F, 0F),
                             end = Offset(size.width / 2F - 2F, size.height),
                             strokeWidth = 4F)
            },
        state = listState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.Center,
        flingBehavior = snapFlingBehavior
    ) {
        item() {
            Box(
                modifier = Modifier
                    .onSizeChanged {
                        firstItemSize = it
                    }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "up")
                    Text(text = "Item 0")
                }
            }
        }
        items(10) {
            Box(
                modifier = Modifier
                    .onSizeChanged {
                        firstItemSize = it
                    }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "up")
                    Text(text = "Item ${it + 1}")
                }
            }
        }
        item() {
            Box(
                modifier = Modifier
                    .onSizeChanged {
                        lastItemSize = it
                    }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "up")
                    Text(text = "Item 12")
                }
            }
        }
    }



}
