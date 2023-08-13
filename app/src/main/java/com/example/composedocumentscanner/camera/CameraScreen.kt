package com.example.composedocumentscanner.camera

import androidx.camera.core.CameraControl
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val state = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(
        state,
        positionInLayout = {
                layoutSize, itemSize ->
            println("layout size: $layoutSize $itemSize")
            (layoutSize / 2f - itemSize / 2F)
        }
    ))
    Column(modifier) {
        Box(Modifier.fillMaxWidth(1F)) {
            Icon(Icons.Default.Clear, contentDescription = "close",
                Modifier
                    .padding(5.dp)
                    .align(Alignment.CenterStart)
                    .size(14.dp))
            Text(text = "Auto", Modifier.align(Alignment.Center))
        }
        Row(Modifier.fillMaxWidth(1F)) {
            Icon(Icons.Default.ArrowLeft, contentDescription = "close", Modifier.align(Alignment.CenterVertically))
            BoxWithConstraints(
                Modifier
                    .wrapContentHeight()
                    .weight(1F)
                    .onSizeChanged {

                    }) {
                var size by remember {
                    mutableStateOf(IntSize.Zero)
                }
                LazyRow(
                    modifier = Modifier.onSizeChanged {
                        size = it
                    },
                    state = state,
                    flingBehavior = flingBehavior) {
                    item {
                        Layout(
                            content = {
                                // Here's the content of each list item.
                                Text(text = ("Item 1"), Modifier.padding(12.dp))
                            },
                            measurePolicy = { measurables, constraints ->
                                // I'm assuming you'll declaring just one root
                                // composable in the content function above
                                // so it's measuring just the Box
                                val placeable = measurables.first().measure(constraints)
                                // maxWidth is from the BoxWithConstraints
                                val maxWidthInPx = maxWidth.roundToPx()
                                // Box width
                                val itemWidth = placeable.width
                                // Calculating the space for the first and last item
                                val startSpace = (maxWidthInPx - itemWidth) / 2
                                val endSpace = 0
                                // The width of the box + extra space
                                val width = startSpace + placeable.width + endSpace
                                layout(width, placeable.height) {
                                    // Placing the Box in the right X position
                                    val x = startSpace
                                    placeable.place(x, 0)
                                }
                            }
                        )
                    }
                    items(10) {
                        Text(text = ("Item $it"), Modifier.padding(12.dp))
                    }
                }
            }

            Icon(Icons.Default.ArrowRight, contentDescription = "close", Modifier.align(Alignment.CenterVertically))
        }
    }

}