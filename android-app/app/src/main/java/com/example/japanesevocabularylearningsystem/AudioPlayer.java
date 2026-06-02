package com.example.japanesevocabularylearningsystem;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlayer {

    private static MediaPlayer current;

    public static void play(String url) {
        if (url == null || url.isEmpty()) return;

        if (current != null) {
            current.release();
            current = null;
        }

        try {
            MediaPlayer mp = new MediaPlayer();
            mp.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            mp.setDataSource(url);
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setOnCompletionListener(player -> {
                player.release();
                if (current == player) current = null;
            });
            mp.setOnErrorListener((player, what, extra) -> {
                player.release();
                if (current == player) current = null;
                return true;
            });
            mp.prepareAsync();
            current = mp;
        } catch (Exception e) {
            Log.e("AudioPlayer", "play error: " + url, e);
        }
    }

    public static void release() {
        if (current != null) {
            current.release();
            current = null;
        }
    }
}