package com.clone.sec.android.app.launcher

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.clone.samsung.homestar.bridge.HomeUpConfig
import com.clone.samsung.homestar.bridge.HomeUpContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LauncherConfigRepository {
    private const val PREFS_NAME = "one_ui_9_home_up_prefs"
    private const val KEY_JSON = "config_json"

    private val _configFlow = MutableStateFlow(HomeUpConfig())
    val configFlow: StateFlow<HomeUpConfig> = _configFlow.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_JSON, null)
        _configFlow.value = HomeUpConfig.fromJson(saved)
    }

    fun updateConfig(context: Context, newConfig: HomeUpConfig) {
        val json = newConfig.toJson()
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_JSON, json)
            .apply()
        _configFlow.value = newConfig
    }
}

class HomeUpConfigProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        context?.let { LauncherConfigRepository.init(it) }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val cursor = MatrixCursor(arrayOf("config_json"))
        val currentJson = LauncherConfigRepository.configFlow.value.toJson()
        cursor.addRow(arrayOf(currentJson))
        return cursor
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val json = values?.getAsString("config_json") ?: return 0
        val ctx = context ?: return 0
        LauncherConfigRepository.updateConfig(ctx, HomeUpConfig.fromJson(json))
        ctx.contentResolver.notifyChange(HomeUpContract.CONTENT_URI, null)
        return 1
    }

    override fun getType(uri: Uri): String = "vnd.android.cursor.item/vnd.com.clone.homestar.config"
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
