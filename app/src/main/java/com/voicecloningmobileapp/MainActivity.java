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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText text;

    private Button record;
    private Button imitate;

    private ProgressBar progress;

    private Thread recordingThread = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private String audioFilePath;
    private String imitationFilePath;
    private String textFilePath;

    private String serviceURL;

    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        serviceURL = "http://192.168.0.112:5000/audio/create";

        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/voiceClonningRecording.mp3";
        textFilePath = getExternalCacheDir().getAbsolutePath() + "/voiceClonningRecording.txt";
        imitationFilePath = getExternalCacheDir().getAbsolutePath() + "/voiceClonningRecording_out.mp3";

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
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(textFilePath));
            writer.write(text.getText().toString());
            writer.close();
        }
        catch(Exception e)
        {
            Log.e("Debug", e.getMessage(), e);
        }

        if(!new File(audioFilePath).exists())
        {
            System.out.println("No file at " + audioFilePath);
            return;
        }
        player = new MediaPlayer();
        try
        {
            getImitationFromRestApi();
            player.setDataSource(imitationFilePath);
            player.prepare();
            player.start();
        }
        catch(Exception e)
        {
            Log.e("[Audio Player]: ", "prepare() failed");
        }
    }

    private void browse(View v)
    {

    }

    private void getImitationFromRestApi()
    {
        File audio = new File(audioFilePath);
        File text = new File(textFilePath);


        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(serviceURL);
            FileBody audioBin = new FileBody(audio);
            FileBody textBin = new FileBody(text);

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("file", audioBin);
            reqEntity.addPart("file", textBin);
            post.setEntity(reqEntity);

            HttpResponse response = client.execute(post);
            HttpEntity resEntity = response.getEntity();
            OutputStream stream = new FileOutputStream(imitationFilePath);
            resEntity.writeTo(stream);
            stream.close();

        }
        catch (Exception e)
        {
            Log.e("Debug", e.getMessage(), e);
        }
    }

}