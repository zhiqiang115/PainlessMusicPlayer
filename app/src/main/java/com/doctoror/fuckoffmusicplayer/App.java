/*
 * Copyright (C) 2016 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.fuckoffmusicplayer;

import com.google.android.exoplayer2.audio.AudioTrack;

import com.doctoror.commons.util.Log;
import com.doctoror.fuckoffmusicplayer.di.DaggerHolder;
import com.doctoror.fuckoffmusicplayer.settings.Settings;
import com.doctoror.fuckoffmusicplayer.settings.Theme;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Inject;

/**
 * Application
 */
public final class App extends Application {

    @Inject
    Settings mSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        initLogger();
        initStrictMode();
        initDagger();
        initTheme();
        initAudioTrack();
    }

    private void initLogger() {
        Log.setLogV(BuildConfig.DEBUG);
        Log.setLogD(BuildConfig.DEBUG);
        Log.setLogI(BuildConfig.DEBUG);
        Log.setLogW(BuildConfig.DEBUG);
        Log.setLogWtf(BuildConfig.DEBUG);
    }

    private void initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }

    private void initDagger() {
        DaggerHolder.getInstance(this).mainComponent().inject(this);
    }

    private void initTheme() {
        AppCompatDelegate.setDefaultNightMode(Theme.getDayNightMode(mSettings.getThemeType()));
    }

    private void initAudioTrack() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AudioTrack.enablePreV21AudioSessionWorkaround = true;
        }
    }
}
