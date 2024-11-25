package com.tinkoff.aljokes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tinkoff.aljokes.data.Joke
import com.tinkoff.aljokes.data.RetrofitInstance
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kitten()
        }
    }
}

@Composable
fun Kitten() {
    val list = remember { mutableStateListOf<Joke>() }

    LaunchedEffect(Unit) {
        loadJokes(list, 10)
    }

    Column(
        modifier = Modifier
            .padding(all = 10.dp),
    ) {
        LazyColumn {
            item {
                Image(
                    painter = painterResource(R.drawable.kitten),
                    contentDescription = "Kitten",
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center,
                )
            }
            items(jokeRepository) { joke -> JokeBlock(joke, Color.Magenta) }
            items(list) { joke -> JokeBlock(joke, Color.Red) }
            item { LoadMoreButton(list) }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun KittenPreview() {
    Kitten()
}

@Composable
fun LoadMoreButton(list: MutableList<Joke>) {
    val coroutineScope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    Button(
        onClick = {
            loading = true
            coroutineScope.launch {
                loadJokes(list, 10)
                loading = false
            }
        },
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp),
                color = MaterialTheme.colorScheme.onPrimary, // Индикатор контрастного цвета
                strokeWidth = 2.dp
            )
        } else {
            Text("Load More")
        }
    }
}

@Composable
fun JokeBlock(joke: Joke, color: Color) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = joke.setup,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                ),
            )
            Text(
                text = joke.delivery,
                style = TextStyle(
                    fontSize = 16.sp,
                )
            )
        }
    }

}

//@Composable
//@Preview(showSystemUi = true)
//fun JokeBlockPreview() {
//    JokeBlock(jokeRepository[0], Color.Red)
//}

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

private suspend fun loadJokes(list: MutableList<Joke>, count: Int) {
    try {
        val response = RetrofitInstance.api.getJoke(count)
        list.addAll(response.jokes)
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
