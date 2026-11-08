package com.clone.sec.android.app.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clone.samsung.homestar.bridge.HomeUpConfig
import com.clone.samsung.homestar.bridge.HomeUpContract
import kotlin.math.roundToInt

data class AppShortcut(
    val id: String,
    val label: String,
    val badgeColor: Color,
    val glyph: String,
    val packageName: String? = null,
    val initialX: Float = 0f,
    val initialY: Float = 0f,
    val rotationDeg: Float = 0f
)

class OneUIHomeActivity : ComponentActivity() {

    private val configReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == HomeUpContract.ACTION_CONFIG_UPDATED) {
                val json = intent.getStringExtra(HomeUpContract.EXTRA_CONFIG_JSON)
                if (!json.isNullOrBlank()) {
                    LauncherConfigRepository.updateConfig(context, HomeUpConfig.fromJson(json))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LauncherConfigRepository.init(this)

        val filter = IntentFilter(HomeUpContract.ACTION_CONFIG_UPDATED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(configReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(configReceiver, filter)
        }

        setContent {
            MaterialTheme {
                val config by LauncherConfigRepository.configFlow.collectAsState()
                OneUI9LauncherRoot(
                    config = config,
                    onUpdateConfig = { LauncherConfigRepository.updateConfig(this, it) },
                    onOpenHomeUpApp = { launchCompanionHomeUp() }
                )
            }
        }
    }

    private fun launchCompanionHomeUp() {
        val launchIntent = packageManager.getLaunchIntentForPackage(HomeUpContract.HOME_UP_PACKAGE)
        if (launchIntent != null) {
            startActivity(launchIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(configReceiver)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OneUI9LauncherRoot(
    config: HomeUpConfig,
    onUpdateConfig: (HomeUpConfig) -> Unit,
    onOpenHomeUpApp: () -> Unit
) {
    val context = LocalContext.current
    var showAppDrawer by remember { mutableStateOf(false) }
    var showRecents by remember { mutableStateOf(false) }
    var showEdgePanel by remember { mutableStateOf(false) }
    var showHomeUpQuickDialog by remember { mutableStateOf(false) }
    var showExpandedFolder by remember { mutableStateOf(false) }
    var activeWidgetIndex by remember { mutableIntStateOf(0) }

    val homeApps = remember {
        listOf(
            AppShortcut("gallery", "Gallery", Color(0xFFE91E63), "✿", initialX = 24f, initialY = 260f),
            AppShortcut("camera", "Camera", Color(0xFFD32F2F), "◉", initialX = 110f, initialY = 260f),
            AppShortcut("playstore", "Play Store", Color(0xFF1976D2), "▶", initialX = 196f, initialY = 260f),
            AppShortcut("goodlock", "Good Lock", Color(0xFF7B1FA2), "⊞", HomeUpContract.HOME_UP_PACKAGE, initialX = 282f, initialY = 260f),
            AppShortcut("homeup", "Home Up 9", Color(0xFF00897B), "⌂", HomeUpContract.HOME_UP_PACKAGE, initialX = 24f, initialY = 360f),
            AppShortcut("settings", "Settings", Color(0xFF546E7A), "⚙", initialX = 110f, initialY = 360f),
            AppShortcut("clock", "Clock", Color(0xFF3949AB), "◷", initialX = 196f, initialY = 360f),
            AppShortcut("notes", "Samsung Notes", Color(0xFFF57C00), "✎", initialX = 282f, initialY = 360f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B132B),
                        Color(0xFF1C2541),
                        Color(0xFF3A506B)
                    )
                )
            )
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (config.useSettingsDialogOnHomeScreen) {
                        showHomeUpQuickDialog = true
                    }
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("13:12", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (config.liveDataSpeedStatusBar) {
                    Text(
                        "48.6 MB/s ↓",
                        color = Color(0xFF80DEEA),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text("5G  100%", color = Color.White, fontSize = 13.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 96.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .height(150.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(alpha = 0.16f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(28.dp))
                        .combinedClickable(
                            onClick = {
                                if (config.stackWidgetsEnabled) {
                                    activeWidgetIndex = (activeWidgetIndex + 1) % 3
                                }
                            }
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                when (activeWidgetIndex) {
                                    0 -> "Weather • Kolkata"
                                    1 -> "Galaxy AI Briefing"
                                    else -> "Device Care • One UI 9"
                                },
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                            if (config.stackWidgetsEnabled) {
                                Text("Stack ${activeWidgetIndex + 1}/3 ⇄", color = Color(0xFF80DEEA), fontSize = 11.sp)
                            }
                        }
                        Text(
                            when (activeWidgetIndex) {
                                0 -> "29°C Sunny"
                                1 -> "Home Up v18 Active"
                                else -> "Memory Optimized"
                            },
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Tap widget to cycle One UI 9 Stack • Long-press home for Home Up dialog",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }

                if (config.enlargedFoldersEnabled) {
                    val folderAlpha = (100 - config.folderBackgroundTransparency).coerceIn(10, 95) / 100f
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .clip(RoundedCornerShape(config.folderCornerRadiusDp.dp))
                            .background(Color.White.copy(alpha = folderAlpha * 0.45f))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(config.folderCornerRadiusDp.dp))
                            .combinedClickable(onClick = { showExpandedFolder = true })
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Galaxy Suite", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MiniFolderDot(Color(0xFF00897B), "⌂")
                                MiniFolderDot(Color(0xFF7B1FA2), "⊞")
                                MiniFolderDot(Color(0xFF1976D2), "✦")
                            }
                            Text("Tap to open (${config.folderGridCols}x${config.folderGridRows})", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (config.diyHomeScreenEnabled) {
                Text(
                    "DIY Home Screen Active (Freeform Drag & Rotate)",
                    color = Color(0xFF80DEEA),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    homeApps.forEach { app ->
                        var offsetX by remember { mutableFloatStateOf(app.initialX) }
                        var offsetY by remember { mutableFloatStateOf(app.initialY - 220f) }
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .pointerInput(app.id) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y
                                    }
                                }
                        ) {
                            LauncherIconItem(
                                app = app,
                                scale = config.iconScalePercent / 100f,
                                showLabel = config.showIconLabels,
                                onClick = {
                                    if (app.packageName == HomeUpContract.HOME_UP_PACKAGE) {
                                        onOpenHomeUpApp()
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(config.homeGridCols.coerceIn(3, 7)),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeApps) { app ->
                        LauncherIconItem(
                            app = app,
                            scale = config.iconScalePercent / 100f,
                            showLabel = config.showIconLabels,
                            onClick = {
                                if (app.packageName == HomeUpContract.HOME_UP_PACKAGE) {
                                    onOpenHomeUpApp()
                                }
                            }
                        )
                    }
                }
            }
        }

        if (config.edgePanelEnabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(10.dp)
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(Color.White.copy(alpha = 0.65f))
                    .combinedClickable(onClick = { showEdgePanel = !showEdgePanel })
            )
        }

        AnimatedVisibility(
            visible = showEdgePanel,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
        ) {
            val panelAlpha = (config.edgePanelOpacity.coerceIn(15, 95)) / 100f
            Column(
                modifier = Modifier
                    .width(92.dp)
                    .clip(RoundedCornerShape(config.edgePanelCornerRadiusDp.dp))
                    .background(Color.White.copy(alpha = panelAlpha))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(config.edgePanelCornerRadiusDp.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Edge 9", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                homeApps.take(5).forEach { app ->
                    LauncherIconItem(
                        app = app,
                        scale = 0.85f,
                        showLabel = true,
                        onClick = {
                            showEdgePanel = false
                            if (app.packageName == HomeUpContract.HOME_UP_PACKAGE) onOpenHomeUpApp()
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionPill("Apps", onClick = { showAppDrawer = !showAppDrawer })
                ActionPill("Home Up", onClick = onOpenHomeUpApp)
                ActionPill("Quick Tweak", onClick = { showHomeUpQuickDialog = true })
                ActionPill("Recents", onClick = { showRecents = !showRecents })
            }
        }

        if (showHomeUpQuickDialog) {
            AlertDialog(
                onDismissRequest = { showHomeUpQuickDialog = false },
                title = { Text("Home Up Quick Settings (One UI 9)") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("DIY Home Screen (Freeform)")
                            Switch(
                                checked = config.diyHomeScreenEnabled,
                                onCheckedChange = { onUpdateConfig(config.copy(diyHomeScreenEnabled = it)) }
                            )
                        }
                        Text("Home Grid Columns: ${config.homeGridCols}")
                        Slider(
                            value = config.homeGridCols.toFloat(),
                            onValueChange = { onUpdateConfig(config.copy(homeGridCols = it.roundToInt())) },
                            valueRange = 3f..7f,
                            steps = 3
                        )
                        Text("Icon Size: ${config.iconScalePercent}%")
                        Slider(
                            value = config.iconScalePercent.toFloat(),
                            onValueChange = { onUpdateConfig(config.copy(iconScalePercent = it.roundToInt())) },
                            valueRange = 75f..125f
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showHomeUpQuickDialog = false
                        onOpenHomeUpApp()
                    }) {
                        Text("Open Full Home Up App")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showHomeUpQuickDialog = false }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
fun MiniFolderDot(color: Color, glyph: String) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(glyph, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun ActionPill(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherIconItem(
    app: AppShortcut,
    scale: Float,
    showLabel: Boolean,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "iconScale"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(animatedScale)
            .combinedClickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(app.badgeColor),
            contentAlignment = Alignment.Center
        ) {
            Text(app.glyph, color = Color.White, fontSize = 24.sp)
        }
        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
