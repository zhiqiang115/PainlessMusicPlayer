/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.fuckoffmusicplayer.library.albums;

import com.doctoror.fuckoffmusicplayer.media.manager.MediaManagerService;
import com.doctoror.fuckoffmusicplayer.library.DeleteItemDialogFragment;

import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Yaroslav Mytkalyk on 09.01.17.
 */
public final class DeleteAlbumDialogFragment extends DeleteItemDialogFragment {


    public static void show(@NonNull final Context context,
            @NonNull final FragmentManager fragmentManager,
            @NonNull final String tag,
            final long albumId,
            @Nullable final String albumName) {
        DeleteItemDialogFragment.show(context,
                DeleteAlbumDialogFragment.class,
                fragmentManager,
                tag,
                albumId,
                albumName);
    }

    @Override
    protected void performDelete() {
        MediaManagerService.deleteAlbum(getActivity(), getTargetId());
    }
}
