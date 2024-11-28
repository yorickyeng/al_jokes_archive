package com.tinkoff.aljokes

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import com.tinkoff.aljokes.data.CatRetrofitInstance
import com.tinkoff.aljokes.data.Joke
import com.tinkoff.aljokes.data.RetrofitInstance
import com.tinkoff.aljokes.ui.theme.AlJokesTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            AlJokesTheme(darkTheme = isSystemInDarkTheme()) {
                Kitten()
            }
        }
    }
}

@Composable
fun Kitten() {
    val list = remember { mutableStateListOf<Joke>() }
    var dark by remember { mutableStateOf(false) }
    dark = isSystemInDarkTheme()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var catUrl by rememberSaveable { mutableStateOf("") }
    var reloadCat by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val totalItems = listState.layoutInfo.totalItemsCount
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0

                if (totalItems > 0 && lastVisibleItemIndex >= totalItems - 1) {
                    coroutineScope.launch {
                        loadJokes(list, 10, dark)
                    }
                }
            }
    }

    LaunchedEffect(reloadCat) {
        try {
            val cat = CatRetrofitInstance.api.getCat().firstOrNull()
            catUrl = cat?.url ?: ""
        } catch (e: Exception) {
            println("Error fetching cat image: ${e.message}")
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(all = 10.dp),
    ) {
        LazyColumn(
            state = listState
        ) {
            item {
                GlideImage(
                    imageModel = { catUrl },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                        .padding(bottom = 16.dp),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    failure = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(onClick = { reloadCat = !reloadCat }),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.kitten),
                                contentDescription = "kitten"
                            )
                        }
                    }
                )
            }
            items(jokeRepository) { joke -> JokeBlock(joke, MaterialTheme.colorScheme.primary) }
            items(list) { joke -> JokeBlock(joke, MaterialTheme.colorScheme.tertiary) }
        }
    }
}

@Composable
fun JokeBlock(joke: Joke, color: Color) {
    var isExpanded by remember { mutableStateOf(false) }
    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = joke.setup,
                color = color,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (isExpanded) {
                Text(
                    text = joke.delivery,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }

}

val jokeRepository = listOf(
    Joke(
        "What does Santa suffer from if he gets stuck in a chimney?",
        "Claustrophobia!"
    ),
    Joke("What’s a math teacher’s favorite place in NYC?", "Times Square."),
    Joke("What do you call a fish wearing a bowtie?", "Sofishticated."),
    Joke(
        "What was the spider doing on the computer?",
        "He was making a web-site.",
    ),
    Joke(
        "Why did the student bring a ladder to school?",
        "Because he wanted to go to high school."
    ),
    Joke(
        "Why did the biologist break up with the physicist?",
        "There was no chemistry.",
    ),
    Joke("What do you call cheese that isn’t yours?", "Nacho cheese."),
)

private suspend fun loadJokes(list: MutableList<Joke>, count: Int, dark: Boolean) {
    try {
        if (dark) {
            val response = RetrofitInstance.api.getDarkJoke(count)
            list.addAll(response.jokes)
        } else {
            val response = RetrofitInstance.api.getAnyJoke(count)
            list.addAll(response.jokes)
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

@Composable
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
fun KittenPreview() {
    AlJokesTheme(darkTheme = true) {
        Kitten()
    }
}

@Composable
@Preview(showBackground = true)
fun JokeBlockPreview() {
    JokeBlock(jokeRepository[0], MaterialTheme.colorScheme.primary)
}