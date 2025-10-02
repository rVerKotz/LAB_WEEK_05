package com.example.lab_week_05

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.lab_week_05.api.CatApiService
import com.example.lab_week_05.model.CatBreedData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    private val catImageView: ImageView by lazy {
        findViewById(R.id.cat_image)
    }

    private val catBreedView: TextView by lazy {
        findViewById(R.id.cat_breed)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<CatBreedData>> {
            override fun onFailure(call: Call<List<CatBreedData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
            }

            override fun onResponse(
                call: Call<List<CatBreedData>>,
                response: Response<List<CatBreedData>>
            ) {
                if (response.isSuccessful) {
                    val images = response.body()
                    if (!images.isNullOrEmpty()) {
                        val firstImage = images[0]

                        // tampilkan gambar
                        Glide.with(this@MainActivity)
                            .load(firstImage.url)
                            .into(catImageView)

                        // tampilkan breed name, atau Unknown kalau tidak ada
                        val breedName = firstImage.breeds?.firstOrNull()?.name ?: "Unknown"
                        catBreedView.text = "Cat Breed: $breedName"
                    }
                } else {
                    Log.e(
                        MAIN_ACTIVITY,
                        "Failed to get response\n" + response.errorBody()?.string().orEmpty()
                    )
                }
            }
        })
    }

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }
}