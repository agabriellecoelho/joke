package com.example.joke

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joke.ui.theme.JokeTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : ComponentActivity() {

    var textoPiada by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = textoPiada, fontSize = 36.sp)
                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = {
                        fetchJoke()
                    }) { Text(text = "BUSCAR PIADA") }
            }
        }


    }

    private fun fetchJoke() {
        val call = ApiClient.apiService.getJoke(language = "pt")

        call.enqueue(object : Callback<JokeResponse> {
            override fun onResponse(call: Call<JokeResponse>, response: Response<JokeResponse>) {
                if (response.isSuccessful) {
                    val jokeResponse = response.body()
                    jokeResponse?.let {
                        // Exibindo a piada (setup e delivery)
                        Log.d("tag", it.toString())
                        textoPiada = "${it.setup} - ${it.delivery}"
                    }
                } else {
                    //textViewJoke.text = "Erro ao obter piada"
                    Log.e("tag", "Erro ao obter piada.")
                }
            }

            override fun onFailure(call: Call<JokeResponse>, t: Throwable) {
                // textViewJoke.text = "Falha na requisição: ${t.message}"
                Log.e("tag", "Erro ao obter piada.")
            }
        })
    }
}

object RetrofitClient {
    private const val BASE_URL = "https://v2.jokeapi.dev/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: JokeApiService by lazy {
        RetrofitClient.retrofit.create(JokeApiService::class.java)
    }
}

data class JokeResponse(
    val setup: String?,
    val delivery: String?,
    val error: Boolean?,
)

interface JokeApiService {

    @GET("joke/Any")
    fun getJoke(
        @Query("lang") language: String = "pt",
    ): Call<JokeResponse>
}