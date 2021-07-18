package com.mospolytech.mospolyhelper.features.utils

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.mospolytech.mospolyhelper.utils.CustomTrust
import okhttp3.OkHttpClient
import java.io.InputStream


@GlideModule
class CustomGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        var client = OkHttpClient.Builder()
        val customTrust = try {
            CustomTrust()
        } catch (e: Exception) {
            null
        }
        customTrust?.let {
            client = client
                .sslSocketFactory(it.sslSocketFactory, it.trustManager)
        }
        val factory = OkHttpUrlLoader.Factory(client.build())
        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.VERBOSE)
    }

}