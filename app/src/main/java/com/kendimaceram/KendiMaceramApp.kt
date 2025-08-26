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


        ttsEngine.synthesize("Hello Tunahan, this is a trial version of the new application you wrote. This text is being read aloud in English to test the text-to-speech engine, and I am narrating a long passage so that you can understand how efficiently it works.   merhaba tunahan, bu senin yazdığın yeni uygulamanın bir deneme sürümüdür. metinden sese motorunu denemek için yazdığın bu metin ingilizce olarak seslendiriliyor, ne kadar verimli çalıştığını anlaman için uzun bir metin seslendiriyorum.")
    }
}