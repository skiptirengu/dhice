package com.skiptirengu.dhice.util;

import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class Animations {
    public static final int LONG = 300;
    public static final int HALF = 250;
    public static final int SHORT = 200;

    public static void animate(Techniques technique, int duration, View target) {
        YoYo.with(technique).duration(duration).playOn(target);
    }

    public static void animate(Techniques technique, View target) {
        YoYo.with(technique).duration(LONG).playOn(target);
    }

    public static void delayed(Techniques technique, int duration, View target, YoYo.AnimatorCallback callback) {
        YoYo.with(technique).duration(duration).onEnd(callback).playOn(target);
    }

    public static void delayed(Techniques technique, View target, YoYo.AnimatorCallback callback) {
        YoYo.with(technique).duration(LONG).onEnd(callback).playOn(target);
    }
}
