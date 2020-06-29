package com.example.mindchess.common

import android.graphics.drawable.AnimationDrawable

internal fun AnimationDrawable.startWithFade() {
    this.setEnterFadeDuration(10000)
    this.setExitFadeDuration(10000)
    this.start()
}