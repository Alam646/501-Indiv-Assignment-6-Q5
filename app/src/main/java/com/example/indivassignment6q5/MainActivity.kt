package com.example.indivassignment6q5

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.indivassignment6q5.ui.theme.IndivAssignment6Q5Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndivAssignment6Q5Theme {
                LocationPermissionScreen()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionScreen() {
    // 1. Define the permissions we need
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // 2. Request permissions as soon as the app launches
    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    // 3. Simple UI to show status
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (permissionsState.allPermissionsGranted) {
            Text("✅ Location Permission Granted! Ready for Step 3.")
        } else {
            Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                Text("Request Location Permission")
            }
        }
    }
}
