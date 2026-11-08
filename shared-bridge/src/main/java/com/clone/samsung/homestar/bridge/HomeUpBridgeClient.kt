package com.clone.samsung.homestar.bridge

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class HomeUpBridgeClient(private val context: Context) {

    fun isOneUIHomeCloneInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(HomeUpContract.LAUNCHER_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun fetchConfig(): HomeUpConfig {
        return try {
            context.contentResolver.query(
                HomeUpContract.CONTENT_URI,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex("config_json")
                    if (idx >= 0) {
                        return HomeUpConfig.fromJson(cursor.getString(idx))
                    }
                }
                HomeUpConfig()
            } ?: HomeUpConfig()
        } catch (e: Exception) {
            HomeUpConfig()
        }
    }

    fun pushConfig(config: HomeUpConfig): Boolean {
        val json = config.toJson()
        val updatedViaProvider = try {
            val values = ContentValues().apply {
                put("config_json", json)
            }
            val rows = context.contentResolver.update(
                HomeUpContract.CONTENT_URI,
                values,
                null,
                null
            )
            rows > 0
        } catch (e: Exception) {
            false
        }

        // Also send explicit broadcast so OneUIHome updates immediately even in background
        val intent = Intent(HomeUpContract.ACTION_CONFIG_UPDATED).apply {
            setPackage(HomeUpContract.LAUNCHER_PACKAGE)
            putExtra(HomeUpContract.EXTRA_CONFIG_JSON, json)
        }
        context.sendBroadcast(intent)
        return updatedViaProvider
    }
}
