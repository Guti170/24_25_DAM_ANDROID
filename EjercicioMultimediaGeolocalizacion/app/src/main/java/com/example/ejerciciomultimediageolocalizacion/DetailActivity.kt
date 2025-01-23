package com.example.ejerciciomultimediageolocalizacion

import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val place = intent.getParcelableExtra<Place>("place")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        videoView = findViewById(R.id.videoView)
        videoView.setVideoPath(place?.videoUrl)
        videoView.setOnPreparedListener { player ->
            player.isLooping = true
        }
        videoView.start()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val location = intent.getParcelableExtra<LatLng>("location")
        location?.let {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            map.addMarker(MarkerOptions().position(it).title("Marker in ${it.latitude}, ${it.longitude}"))
        }
    }
}


