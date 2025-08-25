package com.kendimaceram.app

import android.app.Application
import com.kendimaceram.app.data.OnDeviceTtsEngine
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KendiMaceramApp : Application() {
    @Inject
    lateinit var ttsEngine: OnDeviceTtsEngine

    override fun onCreate() {
        super.onCreate()

        // Motorun yüklenmesi için bekleme
        Thread.sleep(4000)


        ttsEngine.synthesize("Merhaba Tunahan, bugün umarım kendini iyi hissediyorsundur.")
    }
}