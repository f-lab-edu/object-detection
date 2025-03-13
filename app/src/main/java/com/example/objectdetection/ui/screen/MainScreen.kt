package com.example.objectdetection.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.objectdetection.MainViewModel
import com.example.objectdetection.ui.component.ImageItem
import com.example.objectdetection.ui.component.SearchBar
import com.example.objectdetection.ui.component.TopBar

@Composable
fun MainScreen(navController: NavHostController) {
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
                    ImageItem(photo, navController)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(photos.orEmpty()) { photo ->
                    ImageItem(photo, navController)
                }
            }
        }
    }
}