package com.example.ejerciciomultimediageolocalizacion

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.model.LatLng
import android.content.Intent
import androidx.viewpager2.widget.ViewPager2
import com.example.ejerciciomultimediageolocalizacion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        sliderAdapter = SliderAdapter(getPlaces())
        viewPager.adapter = sliderAdapter

        sliderAdapter.setOnItemClickListener { place ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("place", place)
            }
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getPlaces(): List<Place> {
        return listOf(
            Place("New York", R.drawable.new_york, LatLng(40.7128, -74.0060), "https://media.istockphoto.com/id/2120055196/es/v%C3%ADdeo/lapso-de-tiempo-de-la-multitud-de-turistas-que-caminan-y-cruzan-la-calle-en-times-square.mp4?s=mp4-640x640-is&k=20&c=JzpyvwzojlQE3BXcwehvEfD2f3ReNhfzxvHV9O8-tZ0="),
            Place("Paris", R.drawable.paris, LatLng(48.8566, 2.3522), "https://media.istockphoto.com/id/2137222621/es/v%C3%ADdeo/eiffel-tower-in-paris.mp4?s=mp4-640x640-is&k=20&c=pNcfuTmjCesX6cZjYAbwNZLiqR50gGj6281TjUJNYls="),
            Place("Tokyo", R.drawable.tokyo, LatLng(35.6895, 139.6917), "https://media.istockphoto.com/id/1735141079/es/v%C3%ADdeo/transporte-r%C3%A1pido-de-tren-shinkansen-de-alta-velocidad-en-tokio-por-la-noche-vista-del.mp4?s=mp4-640x640-is&k=20&c=xW1-fmjoTnSmQK5ddzXsRJJUraBFG1gRamPg58PnFmY="),
            Place("London", R.drawable.london, LatLng(51.5074, -0.1278), "https://cdn.pixabay.com/video/2018/10/19/18792-296338745_large.mp4"),
            Place("Sydney", R.drawable.sydney, LatLng(-33.8688, 151.2093), "https://www.shutterstock.com/shutterstock/videos/3601467551/preview/stock-footage-sydney-new-south-wales-australia-sydney-opera-house-one-of-the-landmarks-of.webm")
        )
    }
}
