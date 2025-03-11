package com.example.objectdetection.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.objectdetection.MainViewModel
import com.example.objectdetection.R
import com.example.objectdetection.data.Photo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeMainScreen()
        }
    }
}

@Composable
fun ComposeMainScreen() {
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        val viewModel: MainViewModel = hiltViewModel()
        var isSelected by rememberSaveable { mutableStateOf(false) }
        var searchText by rememberSaveable { mutableStateOf("") }
        val photos by viewModel.photos.observeAsState(emptyList())

        TopBar(isSelected = isSelected,
            onToggleLayout = { isSelected = !isSelected })

        SearchBar(
            searchQuery = searchText,
            onSearchQueryChange = { searchText = it },
            onSearchSubmit = { viewModel.searchPhotos(searchText) },
            onClearSearch = { searchText = "" }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isSelected) {
            LazyVerticalGrid(
                GridCells.Fixed(2),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(photos.orEmpty()) { photo ->
                    ImageItem(photo)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(photos.orEmpty()) { photo ->
                    ImageItem(photo)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(isSelected: Boolean, onToggleLayout: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

        },
        actions = {
            IconButton(onClick = onToggleLayout) {
                Icon(
                    painter = if (isSelected) {
                        painterResource(id = R.drawable.ic_list_24)
                    } else {
                        painterResource(id = R.drawable.ic_grid_24)
                    },
                    contentDescription = "Toggle Layout"
                )
            }
        }
    )
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onClearSearch: () -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                Row {
                    IconButton(onClick = onClearSearch) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                    IconButton(onClick = { onSearchSubmit() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Enter")
                    }
                }
            }
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        keyboardActions = KeyboardActions(
            onDone = { onSearchSubmit() }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
    )
}

@Composable
fun ImageItem(photo: Photo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
            .height(200.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = photo.urls?.small ?: "",
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}