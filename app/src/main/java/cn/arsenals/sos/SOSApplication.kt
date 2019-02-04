package cn.arsenals.sos

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.security.KeyStore.getApplicationContext


class SOSApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }
}
