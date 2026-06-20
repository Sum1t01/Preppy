package com.sum1t.preppy.presentation.screens.setting

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sum1t.preppy.presentation.screens.profile.ProfileViewModel
import com.sum1t.preppy.presentation.screens.profile.QuickSettingsCard
import com.sum1t.preppy.presentation.screens.profile.SettingToggleRows
import com.sum1t.preppy.presentation.screens.profile.bouncyClickable
import com.sum1t.preppy.presentation.ui.theme.ThemePalette
import com.sum1t.preppy.presentation.ui.theme.themePreviewColor
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },

                navigationIcon = {

                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 0.dp,
                end = 0.dp,
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🔷 QUICK TOGGLES (reused)
            item {

                PreferencesSection(
                    isDarkMode = state.isDarkMode,
                    isHapticsEnabled = state.isHapticsEnabled,
                    isNotificationsEnabled = state.isNotificationsEnabled,
                    onToggleDarkMode = {
                        viewModel.onEvent(SettingsEvent.ToggleDarkMode)
                    },
                    onToggleHaptics = { viewModel.onToggleHaptics() },
                    onToggleNotifications = { viewModel.onToggleNotifications() },
                )

            }


            // 🎨 THEME SECTION
            item {
                ThemeSelectionCard(
                    selected = state.selectedTheme,
                    hapticsEnabled = state.isHapticsEnabled,
                    onSelect = {
                        viewModel.onEvent(SettingsEvent.ThemeSelected(it))
                    }
                )
            }


            // 🔐 OTHER SETTINGS
//            item {
//                SettingsGroup(
//                    title = "Account",
//                    items = listOf(
//                        SettingsItem("Privacy", Icons.Default.Lock),
//                        SettingsItem("Security", Icons.Default.Shield),
//                        SettingsItem("Data & Storage", Icons.Default.Storage)
//                    )
//                )
//            }


            item {
                SettingsGroup(
                    hapticsEnabled = state.isHapticsEnabled,
                    title = "App",
                    items = listOf(
//                        SettingsItem("Notifications", Icons.Default.Notifications),
//                        SettingsItem("Language", Icons.Default.Language),
                        SettingsItem("About", Icons.Default.Info)
                    )
                )
            }
        }
    }
}


@Composable
fun PreferencesSection(
    isDarkMode: Boolean,
    isHapticsEnabled: Boolean,
    isNotificationsEnabled: Boolean,
    onToggleDarkMode: () -> Unit,
    onToggleHaptics: () -> Unit,
    onToggleNotifications: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(14.dp))

            SettingToggleRows(
                isDarkMode = isDarkMode,
                isHapticsEnabled = isHapticsEnabled,
                isNotificationsEnabled = isNotificationsEnabled,
                onToggleDarkMode = onToggleDarkMode,
                onToggleHaptics = onToggleHaptics,
                onToggleNotifications = onToggleNotifications
            )
        }
    }
}


@Composable
fun ThemeSelectionCard(
    selected: ThemePalette,
    hapticsEnabled: Boolean = true,
    onSelect: (ThemePalette) -> Unit
) {

    val themes = listOf(
        ThemePalette.DEFAULT_GREEN,
        ThemePalette.OCEAN_BLUE,
        ThemePalette.SUNSET_ORANGE,
        ThemePalette.MONOCHROME,
        ThemePalette.FOREST,
        ThemePalette.NEON_PURPLE,
        ThemePalette.COFFEE,
        ThemePalette.MIDNIGHT
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(themes) { theme ->
                    ThemeItem(
                        theme = theme,
                        hapticsEnabled = hapticsEnabled,
                        selected = selected,
                        onSelect = onSelect
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeItem(
    theme: ThemePalette,
    selected: ThemePalette,
    hapticsEnabled: Boolean = true,
    onSelect: (ThemePalette) -> Unit
) {
    val isSelected = theme == selected
    val color = themePreviewColor(theme)

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.06f else 1f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = 450f
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            )
            .bouncyClickable(
                hapticsEnabled = hapticsEnabled
            ) {
                onSelect(theme)
            },
        contentAlignment = Alignment.Center
    ) {

        // 🔵 color dot
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color)
        )

        // ✔ simple selection ring
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}


data class SettingsItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun SettingsGroup(
    hapticsEnabled: Boolean = true,
    title: String,
    items: List<SettingsItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(10.dp))

            items.forEach { item ->
                SettingsRow(
                    hapticsEnabled = hapticsEnabled,
                    item = item
                )
            }
        }
    }
}


@Composable
fun SettingsRow(
    hapticsEnabled: Boolean = true,
    item: SettingsItem
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClickable(
                hapticsEnabled = hapticsEnabled
            ) { }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}