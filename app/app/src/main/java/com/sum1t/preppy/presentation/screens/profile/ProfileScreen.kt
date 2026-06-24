package com.sum1t.preppy.presentation.screens.profile

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.ripple
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sum1t.preppy.BuildConfig
import com.sum1t.preppy.ui.components.AnimatedThemedButton
import com.sum1t.preppy.ui.components.ThemedButtonType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutSuccess: () -> Unit = {},
    onRemoteScreen: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showLogoutSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") },
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
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "© ${java.time.Year.now().value} Preppy. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (state.isUserLoginEnabled) {
                item {
                    ProfileGlassHeader(
                        hapticsEnabled = state.isHapticsEnabled,
                        userName = state.name,
                        username = state.username,
                        email = state.email,
                        streak = state.streak
                    ) {
                        onRemoteScreen()
                    }
                }
            }



            item {
                QuickSettingsCard(
                    isDarkMode = state.isDarkMode,
                    isHapticsEnabled = state.isHapticsEnabled,
                    isNotificationsEnabled = state.isNotificationsEnabled,
                    onToggleDarkMode = { viewModel.onToggleTheme() },
                    onToggleHaptics = { viewModel.onToggleHaptics() },
                    onToggleNotifications = { viewModel.onToggleNotifications() },
                    onOpenFullSettings = onSettingsClick
                )
            }

            item {
                ShareAppCard(
                    hapticsEnabled = state.isHapticsEnabled,
                    onClick = {
                        val shareText =
                            "Check out this app! 🚀\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share App via"))
                    }
                )
            }

            if (state.isUserLoginEnabled) {
                item {
                    LogoutCard(
                        hapticsEnabled = state.isHapticsEnabled,
                        onClick = { showLogoutSheet = true }
                    )
                }
            }
        }

        // ✅ 3. LOGOUT BOTTOM SHEET
        if (showLogoutSheet) {
            LogoutBottomSheet(
                hapticsEnabled = state.isHapticsEnabled,
                onDismiss = { showLogoutSheet = false },
                onConfirm = {
                    showLogoutSheet = false
//                    viewModel.logout()
                    onLogoutSuccess()
                }
            )
        }
    }
}


@Composable
fun ShareAppCard(
    hapticsEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .bouncyClickable(
                hapticsEnabled = hapticsEnabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Share App",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Invite friends to try the app",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}


@Composable
fun LogoutCard(
    hapticsEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .bouncyClickable(
                hapticsEnabled = hapticsEnabled
            ) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    text = "Sign out of your account",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun AnimatedAvatar(
    name: String,
    imageUrl: String? = null,
    profileEmoji: String = "👤",
    size: Dp = 72.dp
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "avatar_scale"
    )

    val initials = name.split(" ")
        .take(2)
        .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale) // ✅ ONLY avatar scales
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {

        when {
            imageUrl != null -> {
                Text("IMG")
            }

            else -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = profileEmoji,
                        fontSize = (size.value * 0.35f).sp
                    )

                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileGlassHeader(
    hapticsEnabled: Boolean = true,
    userName: String,
    username: String,
    email: String,
    streak: Int,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .bouncyClickable(
                hapticsEnabled = hapticsEnabled
            ) {
                onClick()
            }
            .clip(RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {

        Column {

            // 🌈 TOP SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AnimatedAvatar(name = userName, size = 72.dp)

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge
                    )

                    // 👤 Username (new)
                    Text(
                        text = "@$username",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // 🔥 BOTTOM SECTION (STATS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                ProfileStatItem(
                    hapticsEnabled = hapticsEnabled,
                    value = streak.toString(),
                    label = "Day Streak",
                    emoji = "🔥"
                )

                ProfileStatItem(
                    hapticsEnabled = hapticsEnabled,
                    value = "Pro",
                    label = "Status",
                    emoji = "⭐"
                )
            }
        }
    }
}

@Composable
fun ProfileStatItem(
    hapticsEnabled: Boolean = true,
    value: String,
    label: String,
    emoji: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.bouncyClickable(
            hapticsEnabled = hapticsEnabled
        ) { }
    ) {

        Text(
            text = emoji,
            fontSize = 20.sp
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutBottomSheet(
    hapticsEnabled: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        scrimColor = Color.Black.copy(alpha = 0.35f) // iOS-style dim overlay
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 👋 Emoji (kept as requested)
            Text(
                text = "👋",
                fontSize = 40.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Logout?",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "You can login anytime.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // 🔘 Buttons side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(modifier = Modifier.weight(1f)) {
                    AnimatedThemedButton(
                        text = "Cancel",
                        type = ThemedButtonType.TERTIARY,
                        enableHaptics = hapticsEnabled,
                        onClick = onDismiss
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    AnimatedThemedButton(
                        text = "Logout",
                        type = ThemedButtonType.PRIMARY,
                        enableHaptics = hapticsEnabled,
                        onClick = onConfirm
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuickSettingsCard(
    isDarkMode: Boolean,
    isHapticsEnabled: Boolean,
    isNotificationsEnabled: Boolean,
    onToggleDarkMode: () -> Unit,
    onToggleHaptics: () -> Unit,
    onToggleNotifications: () -> Unit,
    onOpenFullSettings: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        Column(modifier = Modifier.padding(18.dp)) {

            // 🔷 NAVIGATION ROW (clearly separate)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        onClick = onOpenFullSettings
                    )
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Click to view more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                AnimatedArrow(pressed)
            }

            Spacer(Modifier.height(16.dp))

            // 🔹 SECTION LABEL (key distinction)
            Text(
                text = "Quick Toggles",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(10.dp))

            SettingToggleRows(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                isHapticsEnabled = isHapticsEnabled,
                onToggleHaptics = onToggleHaptics,
                isNotificationsEnabled = isNotificationsEnabled,
                onToggleNotifications = onToggleNotifications
            )

        }
    }
}


@Composable
fun SettingToggleRows(
    isDarkMode: Boolean,
    isHapticsEnabled: Boolean,
    isNotificationsEnabled: Boolean,
    onToggleDarkMode: () -> Unit,
    onToggleHaptics: () -> Unit,
    onToggleNotifications: () -> Unit,
) {
    // 🔲 TOGGLES CONTAINER (separate surface)
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .padding(vertical = 6.dp)
    ) {

        PremiumToggleRow(
            hapticsEnabled = isHapticsEnabled,
            title = "Dark Mode",
            iconOn = Icons.Default.DarkMode,
            iconOff = Icons.Default.LightMode,
            checked = isDarkMode,
            onToggle = onToggleDarkMode
        )

        PremiumToggleRow(
            hapticsEnabled = isHapticsEnabled,
            title = "Haptics",
            iconOn = Icons.Default.Vibration,
            iconOff = Icons.Default.Vibration,
            checked = isHapticsEnabled,
            onToggle = onToggleHaptics
        )

        PremiumToggleRow(
            hapticsEnabled = isHapticsEnabled,
            title = "Notifications",
            iconOn = Icons.Default.Notifications,
            iconOff = Icons.Default.NotificationsNone,
            checked = isNotificationsEnabled,
            onToggle = onToggleNotifications
        )
    }
}

@Composable
fun AnimatedArrow(pressed: Boolean) {
    val offset by animateDpAsState(
        targetValue = if (pressed) 6.dp else 0.dp,
        label = "arrow_offset"
    )

    Icon(
        imageVector = Icons.Default.ArrowForwardIos,
        contentDescription = null,
        modifier = Modifier.offset(x = offset),
        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}

@Composable
fun Modifier.bouncyClickable(
    hapticsEnabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = 0.5f, // 👈 bouncy
            stiffness = 400f
        ),
        label = "bouncy_scale"
    )

    val haptics = LocalHapticFeedback.current

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = androidx.compose.material3.ripple(bounded = true),
            onClick = {
                if (hapticsEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                }
                onClick()
            }
        )
}

@Composable
fun SmoothSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val animatedChecked by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 200f
        ),
        label = "switch_anim"
    )

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier.scale(0.85f),
        thumbContent = {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = 0.9f + (0.2f * animatedChecked)
                        scaleY = 0.9f + (0.2f * animatedChecked)
                    }
            )
        }
    )
}

@Composable
fun activeGradient(checked: Boolean): Brush {
    return if (checked) {
        Brush.horizontalGradient(
            listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color.Transparent, Color.Transparent)
        )
    }
}


@Composable
fun MorphingIcon(
    iconOn: ImageVector,
    iconOff: ImageVector,
    checked: Boolean
) {
    Crossfade(
        targetState = checked,
        animationSpec = tween(250),
        label = "icon_crossfade"
    ) { state ->

        val scale by animateFloatAsState(
            targetValue = if (state) 1.1f else 0.9f,
            animationSpec = spring(
                dampingRatio = 0.4f,
                stiffness = 400f
            )
        )

        Icon(
            imageVector = if (state) iconOn else iconOff,
            contentDescription = null,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            tint = if (state)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun PremiumToggleRow(
    hapticsEnabled: Boolean = true,
    title: String,
    iconOn: ImageVector,
    iconOff: ImageVector,
    checked: Boolean,
    onToggle: () -> Unit
) {
    val bgBrush = activeGradient(checked)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bgBrush)
            .bouncyClickable(
                hapticsEnabled = hapticsEnabled
            ) { onToggle() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔷 Animated Icon Container
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {
            MorphingIcon(
                iconOn = iconOn,
                iconOff = iconOff,
                checked = checked
            )
        }

        Spacer(Modifier.width(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        val haptics = LocalHapticFeedback.current

        SmoothSwitch(
            checked = checked,
            onCheckedChange = {
                onToggle()
                if (hapticsEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                }
            }
        )
    }
}
