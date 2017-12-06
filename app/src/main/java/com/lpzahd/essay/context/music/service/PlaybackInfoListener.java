package com.lpzahd.essay.context.music.service;

import android.support.v4.media.session.PlaybackStateCompat;

/**
 * 作者 : 迪
 * 时间 : 2017/12/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public abstract class PlaybackInfoListener {

    public abstract void onPlaybackStateChange(PlaybackStateCompat state);

    public void onPlaybackCompleted() {}
}
