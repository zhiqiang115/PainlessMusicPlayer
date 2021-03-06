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
package com.doctoror.fuckoffmusicplayer.library.genres;

import com.doctoror.fuckoffmusicplayer.R;
import com.doctoror.fuckoffmusicplayer.db.genres.GenresProvider;
import com.doctoror.fuckoffmusicplayer.util.DrawableUtils;
import com.doctoror.fuckoffmusicplayer.util.ThemeUtils;
import com.doctoror.fuckoffmusicplayer.widget.CursorRecyclerViewAdapter;
import com.doctoror.fuckoffmusicplayer.widget.viewholder.SingleLineItemIconViewHolder;
import com.l4digital.fastscroll.FastScroller;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by Yaroslav Mytkalyk on 17.10.16.
 */
final class GenresRecyclerAdapter
        extends CursorRecyclerViewAdapter<SingleLineItemIconViewHolder>
        implements FastScroller.SectionIndexer {

    interface OnGenreClickListener {

        void onGenreClick(int position, long id, @Nullable String genre);
    }

    @NonNull
    private final LayoutInflater mLayoutInflater;

    private final Drawable mIcon;

    private OnGenreClickListener mClickListener;

    GenresRecyclerAdapter(final Context context) {
        super(null);
        mLayoutInflater = LayoutInflater.from(context);
        mIcon = DrawableUtils.getTintedDrawable(context, R.drawable.ic_library_music_black_40dp,
                ThemeUtils.getColorStateList(context.getTheme(), android.R.attr.textColorPrimary));
    }

    void setOnGenreClickListener(@Nullable final OnGenreClickListener clickListener) {
        mClickListener = clickListener;
    }

    private void onItemClick(final int position) {
        final Cursor item = getCursor();
        if (item != null && item.moveToPosition(position)) {
            onGenreClick(
                    position,
                    item.getLong(GenresProvider.COLUMN_ID),
                    item.getString(GenresProvider.COLUMN_NAME));
        }
    }

    private void onGenreClick(final int position, final long id,
            @NonNull final String genre) {
        if (mClickListener != null) {
            mClickListener.onGenreClick(position, id, genre);
        }
    }

    @Override
    public void onBindViewHolder(final SingleLineItemIconViewHolder viewHolder,
            final Cursor cursor) {
        viewHolder.text.setText(cursor.getString(GenresProvider.COLUMN_NAME));
    }

    @Override
    public SingleLineItemIconViewHolder onCreateViewHolder(final ViewGroup parent,
            final int viewType) {
        final SingleLineItemIconViewHolder vh = new SingleLineItemIconViewHolder(
                mLayoutInflater.inflate(R.layout.list_item_single_line_icon, parent, false));
        vh.icon.setImageDrawable(mIcon);
        vh.itemView.setOnClickListener(v -> onItemClick(vh.getAdapterPosition()));
        return vh;
    }

    @Override
    public String getSectionText(final int position) {
        final Cursor c = getCursor();
        if (c != null && c.moveToPosition(position)) {
            final String genre = c.getString(GenresProvider.COLUMN_NAME);
            if (!TextUtils.isEmpty(genre)) {
                return String.valueOf(genre.charAt(0));
            }
        }
        return null;
    }
}
