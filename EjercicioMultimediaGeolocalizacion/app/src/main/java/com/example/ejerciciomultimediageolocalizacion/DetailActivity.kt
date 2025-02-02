package com.example.ejerciciomultimediageolocalizacion

import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.ejerciciomultimediageolocalizacion.databinding.ActivityDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityDetailBinding
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val place = intent.getParcelableExtra<Place>("place")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        videoView = binding.videoView
        initializePlayer(place?.videoUrl)
    }

    private fun initializePlayer(videoUrl: String?) {
        if (videoUrl != null) {
            videoView.setVideoPath(videoUrl)
            val mediaController = MediaController(this)
            videoView.setMediaController(mediaController)
            mediaController.setAnchorView(videoView)
            videoView.setOnPreparedListener { player ->
                player.isLooping = true
            }
            videoView.start()
        }
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