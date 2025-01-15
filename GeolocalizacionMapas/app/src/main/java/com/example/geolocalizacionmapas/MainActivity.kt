package com.example.geolocalizacionmapas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.maps.model.PolylineOptions
import android.Manifest

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnPoiClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    private val LOCATION_REQUEST_CODE: Int = 0
    private lateinit var mapView: MapView
    private lateinit var layout: Layout
    private lateinit var map: GoogleMap

    private lateinit var edLong : EditText
    private lateinit var edLat : EditText

    var alMarcadores = ArrayList<Marker>()

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        edLong = findViewById(R.id.edLongitud)
        edLat = findViewById(R.id.edLatitud)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(37.7749, -122.4194)

        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        map.setOnPoiClickListener(this)
        map.setOnMapLongClickListener (this)
        map.setOnMarkerClickListener(this)

        enableMyLocation()
        createMarker()
        pintarCirculo()
        pintarRuta()
    }


    //----------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }

    //-----------------------------------------------------------------------------------------------------

    private fun createMarker() {
        val coordenadaMaestre = LatLng(38.991030,-3.920489 )
        val markerMaestre = map.addMarker(
            MarkerOptions().position(coordenadaMaestre).title("Mi instituto favorito!").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).snippet("IES MAESTRE DE CALATRAVA"))
        alMarcadores.add(markerMaestre!!)

        val coordenadaCR = LatLng(38.98491, -3.92862)
        //map.addMarker(MarkerOptions().position(paris).title("Paris").icon(sizeIcon(R.drawable.paris)))
        val markCR = map.addMarker(MarkerOptions().position(coordenadaCR).title("Ciudad Real").icon(sizeIcon(R.drawable.cr)).alpha(0.8f).draggable(true))
        alMarcadores.add(markCR!!)

        //------------ Zoom hacia un marcador ------------
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenadaMaestre, 18f),
            3000,
            null
        )

        //Esto la mueve sin efecto zoom.
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerCIFP, 18f))
    }

    fun sizeIcon(idImage:Int): BitmapDescriptor {
        val altura = 60
        val anchura = 60

        var draw = ContextCompat.getDrawable(this,idImage) as BitmapDrawable
        val bitmap = draw.bitmap  //Aquí tenemos la imagen.

        //Le cambiamos el tamaño:
        val smallBitmap = Bitmap.createScaledBitmap(bitmap, anchura, altura, false)
        return BitmapDescriptorFactory.fromBitmap(smallBitmap)

    }
//-----------------------------------------------------------------------------------------------------
    //----------------------------------------- Eventos en el mapa ----------------------------------------
    //-----------------------------------------------------------------------------------------------------

    @SuppressLint("MissingPermission")
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Recentrando", Toast.LENGTH_SHORT).show()
        irUbicacioActual()

        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
        this.edLong.setText(p0.longitude.toString())
        this.edLat.setText(p0.latitude.toString())
    }

    override fun onPoiClick(p0: PointOfInterest) {
        Toast.makeText(this@MainActivity, "Pulsado.", Toast.LENGTH_LONG).show()
        val dialogBuilder = AlertDialog.Builder(this@MainActivity)
        dialogBuilder.run {
            setTitle("Información del lugar.")
            setMessage("Id: " + p0!!.placeId + "\nNombre: " + p0!!.name + "\nLatitud: " + p0!!.latLng.latitude.toString() + " \nLongitud: " + p0.latLng.longitude.toString())
            setPositiveButton("Aceptar"){ dialog: DialogInterface, i:Int ->
                Toast.makeText(this@MainActivity, "Salir", Toast.LENGTH_LONG).show()
            }
        }
        dialogBuilder.create().show()
        this.edLong.setText(p0.latLng.longitude.toString())
        this.edLat.setText(p0.latLng.latitude.toString())
    }

    override fun onMapLongClick(p0: LatLng) {
        var marcador = map.addMarker(MarkerOptions().position(p0!!).title("Nuevo marcador"))
        alMarcadores.add(marcador!!)
        this.edLong.setText(p0!!.longitude.toString())
        this.edLat.setText(p0!!.latitude.toString())
        Log.e("ACSCO","Marcador añadido, marcadores actuales: ${alMarcadores.toString()}")
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        Toast.makeText(this, "Estás en ${p0!!.title}, ${p0!!.position}", Toast.LENGTH_SHORT).show()
        this.edLong.setText(p0.position.longitude.toString())
        this.edLat.setText(p0.position.latitude.toString())
        p0.remove()  //---> Para borrarlo cuando hago click sobre él solo hay que descomentar esto.
        alMarcadores.removeAt(alMarcadores.indexOf(p0))
        Log.e("ACSCO","Marcador eliminado, marcadores actuales: ${alMarcadores.toString()}")

        return true;
    }

    @SuppressLint("MissingPermission")
    private fun irUbicacioActual() {
        val latLng = LatLng(38.98491, -3.92862)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f)) //--> Mueve la cámara a esa posición, sin efecto. El valor real indica el nivel de Zoom, de menos a más.

    }

    //------------------------------------------------------------------------------------------------------

    @SuppressLint("MissingPermission")
    fun pintarRuta(){
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val latLng = LatLng(38.98491, -3.92862)
        val markerInstituto = LatLng(38.991030,-3.920489)

        map.addPolyline(PolylineOptions().run{
            add(latLng, markerInstituto)
            color(Color.BLUE)
            width(9f)
        })

        val loc1 = Location("")
        loc1.latitude = latLng.latitude
        loc1.longitude = latLng.longitude
        val loc2 = Location("")
        loc2.latitude = markerInstituto.latitude
        loc2.longitude = markerInstituto.longitude
        val distanceInMeters = loc1.distanceTo(loc2)
        Log.e("ACSCO", distanceInMeters.toString())
    }

    fun pintarCirculo(){
        val markerInstituto = LatLng(38.991030,-3.920489)

        map.addCircle(CircleOptions().run{
            center(markerInstituto)
            radius(9.0)
            strokeColor(Color.BLUE)
            fillColor(Color.GREEN)
        })
    }
}