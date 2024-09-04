package com.ralphevmanzano.animals_mvi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.ralphevmanzano.animals_mvi.api.AnimalService
import com.ralphevmanzano.animals_mvi.model.Animal
import com.ralphevmanzano.animals_mvi.ui.theme.AnimalsMVITheme
import com.ralphevmanzano.animals_mvi.view.MainIntent
import com.ralphevmanzano.animals_mvi.view.MainState
import com.ralphevmanzano.animals_mvi.view.MainViewModel
import com.ralphevmanzano.animals_mvi.view.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel =
            ViewModelProvider(this, ViewModelFactory(AnimalService.api))[MainViewModel::class.java]

        val onButtonClick: () -> Unit = {
            lifecycleScope.launch {
                mainViewModel.userIntent.send(MainIntent.FetchAnimals)
            }
        }

        enableEdgeToEdge()
        setContent {
            AnimalsMVITheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen(vm = mainViewModel, onButtonClick = onButtonClick)
                }
            }
        }
    }
}

@Composable
fun MainScreen(vm: MainViewModel, onButtonClick: () -> Unit) {
    val state = vm.state.value

    when (state) {
        is MainState.Idle -> IdleScreen(onButtonClick)
        is MainState.Loading -> LoadingScreen()
        is MainState.Animals -> AnimalsList(animals = state.animals)
        is MainState.Error -> {
            IdleScreen(onButtonClick)
            Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun IdleScreen(onButtonClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onButtonClick) {
            Text(text = "Fetch Animals")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun AnimalsList(animals: List<Animal>) {
    LazyColumn {
        items(items = animals) {
            AnimalItem(animal = it)
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}

@Composable
fun AnimalItem(animal: Animal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val url = AnimalService.BASE_URL + animal.image + animal.image
        val painter = rememberAsyncImagePainter(url)
        Image (
            painter = painter,
            contentDescription = animal.name,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.FillHeight,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp)
        ) {
            Text(text = animal.name, fontWeight = FontWeight.Bold)
            Text(text = animal.location)
        }
    }
}