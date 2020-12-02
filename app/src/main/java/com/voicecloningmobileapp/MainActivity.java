package com.voicecloningmobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText text;

    private Button record;
    private Button imitate;

    private ProgressBar progress;

    private Thread recordingThread = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private String audioFilePath;

    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/voiceClonningRecording.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        text = (TextInputEditText) findViewById(R.id.textInput);
        record = (Button) findViewById(R.id.button);
        imitate = (Button) findViewById(R.id.button2);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        progress.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    public void record(View v)
    {
        if(recordingThread != null && recordingThread.isAlive()) return;

        progress.setProgress(0);
        progress.setVisibility(View.VISIBLE);

        recordingThread = new Thread(new RecordAudio(progress, audioFilePath, this));
        recordingThread.start();
    }

    public void imitate(View v)
    {
        if(!new File(audioFilePath).exists())
        {
            System.out.println("No file at " + audioFilePath);
            return;
        }
        player = new MediaPlayer();
        try
        {
            player.setDataSource(audioFilePath);
            player.prepare();
            player.start();
        }
        catch(Exception e)
        {
            Log.e("[Audio Player]: ", "prepare() failed");
        }
    }
}