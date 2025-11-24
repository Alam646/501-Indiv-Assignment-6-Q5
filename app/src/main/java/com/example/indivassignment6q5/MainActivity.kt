package com.example.indivassignment6q5

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.indivassignment6q5.ui.theme.IndivAssignment6Q5Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndivAssignment6Q5Theme {
                LocationInfoScreen()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationInfoScreen() {
    val context = LocalContext.current
    
    // State to hold location data
    var locationInfo by remember { mutableStateOf("Fetching location...") }
    
    // Permissions
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Helper Function to Fetch Location
    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val lat = location.latitude
                        val lng = location.longitude
                        
                        // Reverse Geocoding (Coords -> Address)
                        val geocoder = Geocoder(context, Locale.getDefault())
                        try {
                            // Note: This is the sync version. In production, use async or a background thread.
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(lat, lng, 1)
                            val address = if (!addresses.isNullOrEmpty()) {
                                addresses[0].getAddressLine(0)
                            } else {
                                "Address not found"
                            }
                            locationInfo = "Lat: $lat\nLng: $lng\n\nAddress:\n$address"
                        } catch (e: Exception) {
                            locationInfo = "Lat: $lat\nLng: $lng\n\nError fetching address."
                        }
                    } else {
                        locationInfo = "Location unavailable. Try opening Google Maps to fix GPS."
                    }
                }
                .addOnFailureListener {
                    locationInfo = "Failed to get location: ${it.message}"
                }
        }
    }

    // Initial Request
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            getCurrentLocation()
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    // UI
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (permissionsState.allPermissionsGranted) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = locationInfo,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { 
                    locationInfo = "Updating..."
                    getCurrentLocation() 
                }) {
                    Text("Refresh Location")
                }
            }
        } else {
            Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                Text("Grant Location Permission")
            }
        }
    }
}
