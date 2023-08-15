package com.example.composedocumentscanner.record

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter



@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanCollectionDetail() {
    val scanCollection = ScanCollection(
        id = 0,
        title = "Book",
        cover = "",
        photoCount = 0,
        status = 0,
        createAt = 0,
        updateAt = 0,
    )

    Scaffold(
        topBar = {
            TopAppBar (title = {
                Column {
                    Text(
                        text = "Book",
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        ""
                    )
                }
            }, actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Default.Search,
                        "search"
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Default.MoreVert,
                        "more"
                    )
                }

            },)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Default.AddAPhoto,
                    "add"
                )
            }
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                            .wrapContentSize()
                    ) {
                        Icon(
                            Icons.Default.DocumentScanner,
                            "image"
                        )
                        Text(
                            text = "Text",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                            .wrapContentSize()
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            "pdf"
                        )
                        Text(
                            text = "Export PDF",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    ) {
        ScanRecordList(modifier = Modifier
            .padding(it)
            .fillMaxSize(), records = emptyList())

    }

}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScanRecordList(
    modifier: Modifier,
    records: List<ScanRecordState>
) {
    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp) {
        itemsIndexed(records) { _, item ->
            ScanRecordItem(item)
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScanRecordItem(
    scan: ScanRecordState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Card(modifier = modifier
        .combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
        .fillMaxWidth()
        .aspectRatio(scan.record.ratio),) {
        Box() {
            var state: AsyncImagePainter.State by remember {
                mutableStateOf(AsyncImagePainter.State.Empty)
            }
            AsyncImage(
                model = scan.record,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(scan.record.ratio)
                ,
                onState = {
                    state = it
                },
            )
            when (state) {
                is AsyncImagePainter.State.Loading, AsyncImagePainter.State.Empty -> {
                    Image(Icons.Default.Image, contentDescription = null,
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(0.5F),
                        contentScale = ContentScale.Fit
                    )

                }
                is AsyncImagePainter.State.Success -> {

                }
                is AsyncImagePainter.State.Error -> {
                    Image(Icons.Default.BrokenImage, contentDescription = null,
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(0.5F),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            val selectionIndex by remember {
                scan.selectionIndex
            }

            val selection by remember {
                scan.selection
            }
            val selected by remember {
                scan.selected
            }
            val index by remember {
                derivedStateOf {
                    if (selection) {
                        selectionIndex?.toString()
                    } else {
                        scan.record.order.toString()
                    }
                }
            }
            SelectionCheckBox(modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
                index = index,
                selection = selection,
                selected = selected,
            )
        }
    }
}

@Composable
fun SelectionCheckBox(
    modifier: Modifier = Modifier,
    index: String? = null,
    selection: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {

    if (selection) {
        if (selected) {
            Box(
                modifier = modifier
                    .clickable(onClick = onClick)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                if (index != null) {
                    Text(
                        index,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        } else {
            Box(
                modifier = modifier
                    .clickable(onClick = onClick)
                    .size(24.dp)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

            }
        }
    } else {
        Box(modifier = modifier
            .clickable(onClick = onClick)
            .size(24.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            if (index != null) {
                Text(index,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
    }

}

@Preview
@Composable
fun SelectionPreview() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {

        SelectionCheckBox(
            index = "1"
        )
        SelectionCheckBox(
            index = "2",
            selection = false,
            selected = true
        )
        SelectionCheckBox(
            index = "3",
            selection = true,
            selected = true
        )
        SelectionCheckBox(
            index = "4",
            selection = true,
            selected = false
        )
    }
}

@Preview
@Composable
fun ScanRecordPreview() {

    val scan = ScanRecord(
        id = 0,
        createAt = 0,
        updateAt = 0,
        order = 0,
        photo = null,
        orignal = null,
        corner = emptyList(),
        ratio = 1 / 1.44F,
        text = null,
        layout = null,
        status = -1,
    )

    val state = ScanRecordState(
        record = scan,
    )
    ScanRecordItem(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 200.dp),
        scan = state
    )
}

@Preview
@Composable
fun ScanRecordListPreview() {
    val scan = ScanRecord(
        id = 0,
        createAt = 0,
        updateAt = 0,
        order = 0,
        photo = null,
        orignal = null,
        corner = emptyList(),
        ratio = 1 / 1.44F,
        text = null,
        layout = null,
        status = -1,
    )

    val state = ScanRecordState(
        record = scan,
    )
    ScanRecordList(
        modifier = Modifier
            .fillMaxWidth(),
        records = List(10) {

            ScanRecordState(
                record = ScanRecord(
                    id = it.toLong(),
                    createAt = 0,
                    updateAt = 0,
                    order = it,
                    photo = null,
                    orignal = null,
                    corner = emptyList(),
                    ratio = 1 / 1.44F,
                    text = null,
                    layout = null,
                    status = -1,
                ),
            )
        }
    )
}