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
package com.doctoror.fuckoffmusicplayer.wear.remote;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;

import com.doctoror.commons.util.ByteStreams;
import com.doctoror.commons.util.Log;
import com.doctoror.commons.wear.DataPaths;
import com.doctoror.commons.wear.nano.WearPlaybackData;
import com.doctoror.commons.wear.nano.WearSearchData;
import com.doctoror.fuckoffmusicplayer.wear.media.eventbus.EventSearchResults;
import com.doctoror.fuckoffmusicplayer.wear.media.MediaHolder;
import com.doctoror.fuckoffmusicplayer.wear.queue.QueueHolder;

import org.greenrobot.eventbus.EventBus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@link WearableListenerService} implementation for wear app
 */
public final class WearableListenerServiceImpl extends WearableListenerService {

    private static final String TAG = "WearableListenerService";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        switch (messageEvent.getPath()) {
            case DataPaths.Messages.SEARCH_RESULT:
                WearSearchData.Results searchResults = null;
                final byte[] data = messageEvent.getData();
                if (data != null && data.length != 0) {
                    try {
                        searchResults = WearSearchData.Results.parseFrom(data);
                    } catch (InvalidProtocolBufferNanoException e) {
                        Log.w(TAG, e);
                    }
                }
                if (searchResults == null) {
                    searchResults = new WearSearchData.Results();
                }
                EventBus.getDefault().post(new EventSearchResults(searchResults));
                break;
        }
    }

    @Override
    public void onDataChanged(final DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);
        for (final DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                final DataItem item = event.getDataItem();
                if (item != null) {
                    final String path = item.getUri().getPath();
                    switch (path) {
                        case DataPaths.Paths.MEDIA:
                            onMediaItemChanged(item);
                            readAlbumArt(item);
                            break;

                        case DataPaths.Paths.PLAYBACK_STATE:
                            onPlaybackStateItemChanged(item);
                            break;

                        case DataPaths.Paths.PLAYBACK_POSITION:
                            onPlaybackPositionItemChanged(item);
                            break;

                        case DataPaths.Paths.QUEUE:
                            onQueueChanged(item);
                            break;
                    }
                }
            }
        }
    }

    private void onMediaItemChanged(@NonNull final DataItem mediaItem) {
        final byte[] data = mediaItem.getData();
        if (data == null) {
            MediaHolder.getInstance(this).setMedia(null);
            return;
        }

        try {
            final WearPlaybackData.Media media = WearPlaybackData.Media.parseFrom(data);
            MediaHolder.getInstance(this).setMedia(media);
        } catch (InvalidProtocolBufferNanoException e) {
            Log.w(TAG, e);
        }
    }

    private void readAlbumArt(@NonNull final DataItem mediaItem) {
        final Map<String, DataItemAsset> assets = mediaItem.getAssets();
        if (assets != null) {
            readAlbumArt(assets.get(DataPaths.Assets.ALBUM_ART));
        }
    }

    private void readAlbumArt(@Nullable final DataItemAsset asset) {
        if (asset != null) {
            if (!mGoogleApiClient.isConnected()) {
                final ConnectionResult result = mGoogleApiClient
                        .blockingConnect(5, TimeUnit.SECONDS);
                if (!result.isSuccess()) {
                    return;
                }
            }
            // convert asset into a file descriptor and block until it's ready
            final InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                    mGoogleApiClient, asset).await().getInputStream();
            if (assetInputStream != null) {
                byte[] albumArt = null;
                try {
                    albumArt = ByteStreams.toByteArray(assetInputStream);
                } catch (IOException e) {
                    Log.w(TAG, e);
                } finally {
                    try {
                        assetInputStream.close();
                    } catch (IOException e) {
                        Log.w(TAG, e);
                    }
                }
                MediaHolder.getInstance(this).setAlbumArt(albumArt);
            }
        }
    }

    private void onPlaybackStateItemChanged(@NonNull final DataItem stateItem) {
        final byte[] data = stateItem.getData();
        if (data == null) {
            MediaHolder.getInstance(this).setPlaybackState(null);
            return;
        }
        try {
            final WearPlaybackData.PlaybackState s = WearPlaybackData.PlaybackState.parseFrom(data);
            MediaHolder.getInstance(this).setPlaybackState(s);
        } catch (InvalidProtocolBufferNanoException e) {
            Log.w(TAG, e);
        }
    }

    private void onPlaybackPositionItemChanged(@NonNull final DataItem positionItem) {
        final byte[] data = positionItem.getData();
        if (data == null) {
            MediaHolder.getInstance(this).setPlaybackPosition(null);
            return;
        }
        try {
            final WearPlaybackData.PlaybackPosition s = WearPlaybackData.PlaybackPosition
                    .parseFrom(data);
            MediaHolder.getInstance(this).setPlaybackPosition(s);
        } catch (InvalidProtocolBufferNanoException e) {
            Log.w(TAG, e);
        }
    }

    private void onQueueChanged(@NonNull final DataItem stateItem) {
        final byte[] data = stateItem.getData();
        if (data == null) {
            QueueHolder.getInstance(this).setQueue(null);
            return;
        }
        try {
            final WearPlaybackData.Queue p = WearPlaybackData.Queue.parseFrom(data);
            QueueHolder.getInstance(this).setQueue(p);
        } catch (InvalidProtocolBufferNanoException e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}
