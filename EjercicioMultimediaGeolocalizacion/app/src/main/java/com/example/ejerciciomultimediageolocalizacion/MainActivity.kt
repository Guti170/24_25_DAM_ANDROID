package com.example.ejerciciomultimediageolocalizacion

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.model.LatLng
import android.content.Intent
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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
            Place("New York", R.drawable.new_york, LatLng(40.7128, -74.0060), "https://www.youtube.com/watch?v=h3fUgOKFMNU&t=11s"),
            Place("Paris", R.drawable.paris, LatLng(48.8566, 2.3522), "https://www.youtube.com/watch?v=REDVbTQxMXo"),
            Place("Tokyo", R.drawable.tokyo, LatLng(35.6895, 139.6917), "https://www.youtube.com/watch?v=6DQxRQb9dCE"),
            Place("London", R.drawable.london, LatLng(51.5074, -0.1278), "https://www.youtube.com/watch?v=X8zLJlU_-60"),
            Place("Sydney", R.drawable.sydney, LatLng(-33.8688, 151.2093), "https://www.youtube.com/watch?v=2QVwEWIKMI8")
        )
    }
}
