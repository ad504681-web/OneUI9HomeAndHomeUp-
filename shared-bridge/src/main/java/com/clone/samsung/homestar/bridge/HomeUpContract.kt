package com.clone.samsung.homestar.bridge

import android.net.Uri
import org.json.JSONObject

object HomeUpContract {
    const val LAUNCHER_PACKAGE = "com.clone.sec.android.app.launcher"
    const val HOME_UP_PACKAGE = "com.clone.samsung.android.app.homestar"
    const val AUTHORITY = "com.clone.sec.android.app.launcher.homeup.provider"
    const val ACTION_CONFIG_UPDATED = "com.clone.samsung.homestar.ACTION_CONFIG_UPDATED"
    const val EXTRA_CONFIG_JSON = "extra_config_json"

    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/config")
}

enum class TaskChangerLayout(val label: String) {
    STACK("Tilt Stack (One UI 9)"),
    GRID("Grid"),
    LIST("List"),
    VERTICAL_LIST("Vertical List"),
    SLIM_LIST("Slim List")
}

data class HomeUpConfig(
    val homeUpEnabled: Boolean = true,
    val useSettingsDialogOnHomeScreen: Boolean = true,
    val diyHomeScreenEnabled: Boolean = false,
    val homeGridCols: Int = 5,
    val homeGridRows: Int = 6,
    val appsGridCols: Int = 4,
    val appsGridRows: Int = 6,
    val folderGridCols: Int = 3,
    val folderGridRows: Int = 3,
    val iconScalePercent: Int = 100,
    val showIconLabels: Boolean = true,
    val loopHomePages: Boolean = true,
    val verticalAppDrawer: Boolean = false,
    val appDrawerBlurIntensity: Int = 75,
    val popupFolderEnabled: Boolean = true,
    val folderBackgroundTransparency: Int = 68,
    val folderCornerRadiusDp: Int = 32,
    val enlargedFoldersEnabled: Boolean = true,
    val stackWidgetsEnabled: Boolean = true,
    val liveDataSpeedStatusBar: Boolean = true,
    val edgePanelEnabled: Boolean = true,
    val edgePanelBlurIntensity: Int = 82,
    val edgePanelOpacity: Int = 45,
    val edgePanelCornerRadiusDp: Int = 28,
    val edgePanelGlassPillMode: Boolean = true,
    val gestureAnimationSpeed: Float = 1.0f,
    val gestureDampingRatio: Float = 0.78f,
    val gestureStiffness: Float = 380f,
    val bezierX1: Float = 0.22f,
    val bezierY1: Float = 1.0f,
    val bezierX2: Float = 0.36f,
    val bezierY2: Float = 1.0f,
    val taskChangerLayout: TaskChangerLayout = TaskChangerLayout.STACK,
    val taskChangerMiniMode: Boolean = false,
    val taskChangerCenterRunningApp: Boolean = true,
    val multiFingerGesturesEnabled: Boolean = true,
    val twoFingerSwipeLeftAction: String = "Take Screenshot",
    val threeFingerSwipeUpAction: String = "Quick Split Screen",
    val fourFingerSwipeRightAction: String = "Open Galaxy AI Overlay"
) {
    fun toJson(): String {
        val obj = JSONObject()
        obj.put("homeUpEnabled", homeUpEnabled)
        obj.put("useSettingsDialogOnHomeScreen", useSettingsDialogOnHomeScreen)
        obj.put("diyHomeScreenEnabled", diyHomeScreenEnabled)
        obj.put("homeGridCols", homeGridCols)
        obj.put("homeGridRows", homeGridRows)
        obj.put("appsGridCols", appsGridCols)
        obj.put("appsGridRows", appsGridRows)
        obj.put("folderGridCols", folderGridCols)
        obj.put("folderGridRows", folderGridRows)
        obj.put("iconScalePercent", iconScalePercent)
        obj.put("showIconLabels", showIconLabels)
        obj.put("loopHomePages", loopHomePages)
        obj.put("verticalAppDrawer", verticalAppDrawer)
        obj.put("appDrawerBlurIntensity", appDrawerBlurIntensity)
        obj.put("popupFolderEnabled", popupFolderEnabled)
        obj.put("folderBackgroundTransparency", folderBackgroundTransparency)
        obj.put("folderCornerRadiusDp", folderCornerRadiusDp)
        obj.put("enlargedFoldersEnabled", enlargedFoldersEnabled)
        obj.put("stackWidgetsEnabled", stackWidgetsEnabled)
        obj.put("liveDataSpeedStatusBar", liveDataSpeedStatusBar)
        obj.put("edgePanelEnabled", edgePanelEnabled)
        obj.put("edgePanelBlurIntensity", edgePanelBlurIntensity)
        obj.put("edgePanelOpacity", edgePanelOpacity)
        obj.put("edgePanelCornerRadiusDp", edgePanelCornerRadiusDp)
        obj.put("edgePanelGlassPillMode", edgePanelGlassPillMode)
        obj.put("gestureAnimationSpeed", gestureAnimationSpeed.toDouble())
        obj.put("gestureDampingRatio", gestureDampingRatio.toDouble())
        obj.put("gestureStiffness", gestureStiffness.toDouble())
        obj.put("bezierX1", bezierX1.toDouble())
        obj.put("bezierY1", bezierY1.toDouble())
        obj.put("bezierX2", bezierX2.toDouble())
        obj.put("bezierY2", bezierY2.toDouble())
        obj.put("taskChangerLayout", taskChangerLayout.name)
        obj.put("taskChangerMiniMode", taskChangerMiniMode)
        obj.put("taskChangerCenterRunningApp", taskChangerCenterRunningApp)
        obj.put("multiFingerGesturesEnabled", multiFingerGesturesEnabled)
        obj.put("twoFingerSwipeLeftAction", twoFingerSwipeLeftAction)
        obj.put("threeFingerSwipeUpAction", threeFingerSwipeUpAction)
        obj.put("fourFingerSwipeRightAction", fourFingerSwipeRightAction)
        return obj.toString()
    }

    companion object {
        fun fromJson(json: String?): HomeUpConfig {
            if (json.isNullOrBlank()) return HomeUpConfig()
            return try {
                val obj = JSONObject(json)
                HomeUpConfig(
                    homeUpEnabled = obj.optBoolean("homeUpEnabled", true),
                    useSettingsDialogOnHomeScreen = obj.optBoolean("useSettingsDialogOnHomeScreen", true),
                    diyHomeScreenEnabled = obj.optBoolean("diyHomeScreenEnabled", false),
                    homeGridCols = obj.optInt("homeGridCols", 5),
                    homeGridRows = obj.optInt("homeGridRows", 6),
                    appsGridCols = obj.optInt("appsGridCols", 4),
                    appsGridRows = obj.optInt("appsGridRows", 6),
                    folderGridCols = obj.optInt("folderGridCols", 3),
                    folderGridRows = obj.optInt("folderGridRows", 3),
                    iconScalePercent = obj.optInt("iconScalePercent", 100),
                    showIconLabels = obj.optBoolean("showIconLabels", true),
                    loopHomePages = obj.optBoolean("loopHomePages", true),
                    verticalAppDrawer = obj.optBoolean("verticalAppDrawer", false),
                    appDrawerBlurIntensity = obj.optInt("appDrawerBlurIntensity", 75),
                    popupFolderEnabled = obj.optBoolean("popupFolderEnabled", true),
                    folderBackgroundTransparency = obj.optInt("folderBackgroundTransparency", 68),
                    folderCornerRadiusDp = obj.optInt("folderCornerRadiusDp", 32),
                    enlargedFoldersEnabled = obj.optBoolean("enlargedFoldersEnabled", true),
                    stackWidgetsEnabled = obj.optBoolean("stackWidgetsEnabled", true),
                    liveDataSpeedStatusBar = obj.optBoolean("liveDataSpeedStatusBar", true),
                    edgePanelEnabled = obj.optBoolean("edgePanelEnabled", true),
                    edgePanelBlurIntensity = obj.optInt("edgePanelBlurIntensity", 82),
                    edgePanelOpacity = obj.optInt("edgePanelOpacity", 45),
                    edgePanelCornerRadiusDp = obj.optInt("edgePanelCornerRadiusDp", 28),
                    edgePanelGlassPillMode = obj.optBoolean("edgePanelGlassPillMode", true),
                    gestureAnimationSpeed = obj.optDouble("gestureAnimationSpeed", 1.0).toFloat(),
                    gestureDampingRatio = obj.optDouble("gestureDampingRatio", 0.78).toFloat(),
                    gestureStiffness = obj.optDouble("gestureStiffness", 380.0).toFloat(),
                    bezierX1 = obj.optDouble("bezierX1", 0.22).toFloat(),
                    bezierY1 = obj.optDouble("bezierY1", 1.0).toFloat(),
                    bezierX2 = obj.optDouble("bezierX2", 0.36).toFloat(),
                    bezierY2 = obj.optDouble("bezierY2", 1.0).toFloat(),
                    taskChangerLayout = runCatching {
                        TaskChangerLayout.valueOf(obj.optString("taskChangerLayout", "STACK"))
                    }.getOrDefault(TaskChangerLayout.STACK),
                    taskChangerMiniMode = obj.optBoolean("taskChangerMiniMode", false),
                    taskChangerCenterRunningApp = obj.optBoolean("taskChangerCenterRunningApp", true),
                    multiFingerGesturesEnabled = obj.optBoolean("multiFingerGesturesEnabled", true),
                    twoFingerSwipeLeftAction = obj.optString("twoFingerSwipeLeftAction", "Take Screenshot"),
                    threeFingerSwipeUpAction = obj.optString("threeFingerSwipeUpAction", "Quick Split Screen"),
                    fourFingerSwipeRightAction = obj.optString("fourFingerSwipeRightAction", "Open Galaxy AI Overlay")
                )
            } catch (e: Exception) {
                HomeUpConfig()
            }
        }
    }
}
