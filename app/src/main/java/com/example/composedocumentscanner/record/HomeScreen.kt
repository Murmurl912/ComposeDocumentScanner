package com.example.composedocumentscanner.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.HeatPump
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@Preview
@Composable
fun ScanCollectionItem() {

    Card {
        Row(
            Modifier
                .height(150.dp)
                .fillMaxWidth()
                .padding(12.dp)) {

            AsyncImage(
                model = "",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1F)
                ,
                placeholder = rememberVectorPainter(Icons.Default.Image)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(Modifier.fillMaxHeight(1F)) {
                Text(text = "Book of fame", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "2020-01-1", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.weight(1F))
                Row(Modifier.fillMaxWidth(1F), horizontalArrangement = Arrangement.SpaceEvenly) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun ScanCollectionList(modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxWidth(1F),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(10) {
            ScanCollectionItem()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ScanCollectionScreen() {
    Scaffold(
        topBar = {
                 TopAppBar(title = { 
                     Text(text = "Home")
                 }, actions = {
                     IconButton(onClick = { /*TODO*/ }) {
                         Icon(Icons.Default.Search, contentDescription = "Search")
                     }
                     IconButton(onClick = { /*TODO*/ }) {
                         Icon(Icons.Default.MoreVert, contentDescription = "Search")
                     }
                 }, navigationIcon = {
                     TextButton(onClick = { /*TODO*/ }) {
                         Text(text = "\uD83D\uDC96",
                             fontSize = 20.sp
                         )
                     }
                 })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Scan")
            }
        }
    ) {

        ScanCollectionList(modifier = Modifier.padding(it))
    }
}