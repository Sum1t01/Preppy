package com.sum1t.preppy.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sum1t.preppy.common.navigation.Navigation
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetThemePaletteUseCase
import com.sum1t.preppy.presentation.ui.theme.PreppyTheme
import com.sum1t.preppy.presentation.ui.theme.ThemePalette
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val getDarkModeUseCase: GetDarkModeUseCase by inject()
    private val getThemePaletteUseCase: GetThemePaletteUseCase by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by getDarkModeUseCase.invoke().collectAsState(
                initial = isSystemInDarkTheme()
            )

            val themePalette by getThemePaletteUseCase.invoke().collectAsState(
                initial = ThemePalette.DEFAULT_GREEN
            )


            val notificationPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Handle result (optional: send to ViewModel)
                }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            PreppyTheme(
                palette = themePalette,
                darkTheme = isDarkMode
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(modifier = Modifier) {
                        Navigation()
                    }
                }
            }
        }
    }
}