package com.gamingwe.cubewerubiksolver.manual.utils;
import android.view.View;

import xyz.klinker.android.floating_tutorial.util.DensityConverter;

public class AnimationHelper {
    public static void animateGroup(View... views) {
        int startTime = 300;

        for (View v : views) {
            quickViewReveal(v, startTime);
            startTime += 75;
        }
    }

    public static void quickViewReveal(View view, long delay) {
        view.setTranslationX(-1f * DensityConverter.INSTANCE.toDp(view.getContext(), 16));
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .translationX(0f)
                .alpha(1f)
                .setStartDelay(delay)
                .start();
    }
}