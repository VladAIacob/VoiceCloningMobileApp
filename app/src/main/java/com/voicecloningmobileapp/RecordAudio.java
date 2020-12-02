package com.voicecloningmobileapp;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class RecordAudio implements Runnable{

    private static final int AUDIO_LENGTH = 10;
    private final String path;
    private final ProgressBar progressBar;
    private final Context context;
    private MediaRecorder recorder;

    public RecordAudio(ProgressBar progressBar,final String path, Context context)
    {
        this.progressBar = progressBar;
        this.context = context;
        this.path = path;
    }

    private void setProgress(final int progress)
    {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
            }
        });
    }

    private void setVisibility(final int code)
    {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(code);
            }
        });
    }

    public void run()
    {
        try
        {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(path);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
        }
        catch (Exception e)
        {
            Log.e("[Audio Recorder]: ", "prepare() failed");
        }
        recorder.start();

        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime <= AUDIO_LENGTH * 1000)
        {
            long currentTime = System.currentTimeMillis();
            System.out.println((int)((currentTime - startTime) / (10L * AUDIO_LENGTH)));
            setProgress((int)((currentTime - startTime) / (10L * AUDIO_LENGTH)));
        }

        recorder.stop();
        recorder.release();
        setVisibility(View.INVISIBLE);
    }
}
