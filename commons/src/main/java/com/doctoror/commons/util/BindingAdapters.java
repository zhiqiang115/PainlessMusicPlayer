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
package com.doctoror.commons.util;

import android.databinding.BindingAdapter;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.ViewAnimator;

/**
 * Common binding adapters
 */
public final class BindingAdapters {

    private BindingAdapters() {
        throw new UnsupportedOperationException();
    }

    @BindingAdapter("displayedChild")
    public static void setDisplayedChild(@NonNull final ViewAnimator viewAnimator,
            final int child) {
        if (viewAnimator.getDisplayedChild() != child) {
            viewAnimator.setDisplayedChild(child);
        }
    }

    @BindingAdapter("srcRes")
    public static void setImageResource(@NonNull final ImageView imageView,
            @DrawableRes final int src) {
        imageView.setImageResource(src);
    }

    @BindingAdapter("colorFilter")
    public static void setColorFiler(@NonNull final ImageView imageView,
            @ColorInt final int color) {
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}
