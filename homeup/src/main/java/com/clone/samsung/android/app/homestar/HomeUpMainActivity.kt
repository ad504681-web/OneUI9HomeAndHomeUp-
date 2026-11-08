package com.clone.samsung.android.app.homestar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clone.samsung.homestar.bridge.HomeUpBridgeClient
import com.clone.samsung.homestar.bridge.HomeUpConfig
import com.clone.samsung.homestar.bridge.TaskChangerLayout
import kotlin.math.roundToInt

class HomeUpMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bridgeClient = HomeUpBridgeClient(this)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                var config by remember { mutableStateOf(bridgeClient.fetchConfig()) }
                val launcherConnected = remember { bridgeClient.isOneUIHomeCloneInstalled() }

                fun updateAndSync(updated: HomeUpConfig) {
                    config = updated
                    bridgeClient.pushConfig(updated)
                }

                HomeUpDashboardScreen(
                    config = config,
                    launcherConnected = launcherConnected,
                    onConfigChange = ::updateAndSync
                )
            }
        }
    }
}

@Composable
fun HomeUpDashboardScreen(
    config: HomeUpConfig,
    launcherConnected: Boolean,
    onConfigChange: (HomeUpConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1117))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Home Up v18.0.00.38", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            if (launcherConnected)
                "Connected to One UI 9 Home APK on One UI 6 • Real-Time IPC Active"
            else
                "Waiting for One UI 9 Home APK (com.clone.sec.android.app.launcher)...",
            color = if (launcherConnected) Color(0xFF4DB6AC) else Color(0xFFFFB74D),
            fontSize = 13.sp
        )

        // Master Enable Card
        HomeUpCard(title = "Master Switch & Home Screen Dialog") {
            SwitchRow("Enable Home Up", config.homeUpEnabled) {
                onConfigChange(config.copy(homeUpEnabled = it))
            }
            SwitchRow("Use settings dialog on home screen (One UI 8/9)", config.useSettingsDialogOnHomeScreen) {
                onConfigChange(config.copy(useSettingsDialogOnHomeScreen = it))
            }
            SwitchRow("DIY Home Screen (One UI 9 Freeform Placement)", config.diyHomeScreenEnabled) {
                onConfigChange(config.copy(diyHomeScreenEnabled = it))
            }
        }

        // Grid & Icons Card
        HomeUpCard(title = "Home Screen & App Drawer Grid") {
            Text("Home Screen Columns: ${config.homeGridCols}", color = Color.White)
            Slider(
                value = config.homeGridCols.toFloat(),
                onValueChange = { onConfigChange(config.copy(homeGridCols = it.roundToInt())) },
                valueRange = 3f..7f,
                steps = 3
            )
            Text("Home Screen Rows: ${config.homeGridRows}", color = Color.White)
            Slider(
                value = config.homeGridRows.toFloat(),
                onValueChange = { onConfigChange(config.copy(homeGridRows = it.roundToInt())) },
                valueRange = 4f..7f,
                steps = 2
            )
            Text("Icon Scale: ${config.iconScalePercent}%", color = Color.White)
            Slider(
                value = config.iconScalePercent.toFloat(),
                onValueChange = { onConfigChange(config.copy(iconScalePercent = it.roundToInt())) },
                valueRange = 75f..125f
            )
            SwitchRow("Show App Icon Labels", config.showIconLabels) {
                onConfigChange(config.copy(showIconLabels = it))
            }
            SwitchRow("Vertical App Drawer List", config.verticalAppDrawer) {
                onConfigChange(config.copy(verticalAppDrawer = it))
            }
        }

        // Frosted Glass Folder & Edge Panel (One UI 9)
        HomeUpCard(title = "Frosted Glass Folders & Edge Panel (One UI 9)") {
            SwitchRow("Enlarged Folders (3x3 Tap-direct)", config.enlargedFoldersEnabled) {
                onConfigChange(config.copy(enlargedFoldersEnabled = it))
            }
            Text("Folder Transparency: ${config.folderBackgroundTransparency}%", color = Color.White)
            Slider(
                value = config.folderBackgroundTransparency.toFloat(),
                onValueChange = { onConfigChange(config.copy(folderBackgroundTransparency = it.roundToInt())) },
                valueRange = 0f..95f
            )
            Text("Edge Panel Blur Intensity: ${config.edgePanelBlurIntensity}%", color = Color.White)
            Slider(
                value = config.edgePanelBlurIntensity.toFloat(),
                onValueChange = { onConfigChange(config.copy(edgePanelBlurIntensity = it.roundToInt())) },
                valueRange = 0f..100f
            )
            Text("Edge Panel Corner Radius: ${config.edgePanelCornerRadiusDp}dp", color = Color.White)
            Slider(
                value = config.edgePanelCornerRadiusDp.toFloat(),
                onValueChange = { onConfigChange(config.copy(edgePanelCornerRadiusDp = it.roundToInt())) },
                valueRange = 8f..44f
            )
        }

        // Gesture Animation Tuning & Task Changer
        HomeUpCard(title = "Home Gesture Animation & Task Changer") {
            Text("Animation Speed Multiplier: ${"%.2f".format(config.gestureAnimationSpeed)}x", color = Color.White)
            Slider(
                value = config.gestureAnimationSpeed,
                onValueChange = { onConfigChange(config.copy(gestureAnimationSpeed = it)) },
                valueRange = 0.5f..1.8f
            )
            Text("Spring Damping Ratio: ${"%.2f".format(config.gestureDampingRatio)}", color = Color.White)
            Slider(
                value = config.gestureDampingRatio,
                onValueChange = { onConfigChange(config.copy(gestureDampingRatio = it)) },
                valueRange = 0.4f..1.0f
            )
            Text("Task Changer Layout: ${config.taskChangerLayout.label}", color = Color.White, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskChangerLayout.values().forEach { layout ->
                    FilterChip(
                        selected = config.taskChangerLayout == layout,
                        onClick = { onConfigChange(config.copy(taskChangerLayout = layout)) },
                        label = { Text(layout.name, fontSize = 10.sp) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeUpCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1A1F2C))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, color = Color(0xFF80DEEA), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        content()
    }
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
