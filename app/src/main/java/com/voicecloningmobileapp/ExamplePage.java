package com.voicecloningmobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;

public class ExamplePage extends AppCompatActivity {

    //GUI variables
    private ViewPager viewPager;
    private SlideAdapter adapter;
    private TextInputEditText text;

    //The media player used to play the output audio
    private MediaPlayer player = null;

    //The input/output paths
    private String textFilePath;
    private String imitationFilePath;

    //The server URL
    private String serviceURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_page);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        serviceURL = "http://192.168.0.112:5000/audio/example/";
        textFilePath = getExternalCacheDir().getAbsolutePath() + "/example.txt";
        imitationFilePath = getExternalCacheDir().getAbsolutePath() + "/example_out.mp3";

        text = (TextInputEditText) findViewById(R.id.textInput);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new SlideAdapter(this);
        adapter.images = new int[]
        {
            R.drawable.obama,
            R.drawable.ramsay,
            R.drawable.hawking
        };

        viewPager.setAdapter(adapter);
    }

    //Method that receives the response from the server
    private void getImitationFromRestApi(String example)
    {
        File text = new File(textFilePath);

        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(serviceURL + example);
            FileBody textBin = new FileBody(text);

            MultipartEntity reqEntity = new MultipartEntity();
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

    //Method that sends a post request containing the text file
    private void sendPostRequest(String example)
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

        player = new MediaPlayer();
        try
        {
            getImitationFromRestApi(example);
            player.setDataSource(imitationFilePath);
            player.prepare();
            player.start();
        }
        catch(Exception e)
        {
            Log.e("[Audio Player]: ", "prepare() failed");
        }
    }

    //Method that resolves what example request to send.
    public void imitate(View v)
    {
        switch(viewPager.getCurrentItem())
        {
            case 0: sendPostRequest("obama");   break;
            case 1: sendPostRequest("ramsay");  break;
            case 2: sendPostRequest("hawking"); break;
            default: Log.e("Error", "Unmaped case: " + viewPager.getCurrentItem()); return;
        }
    }

    //Back button
    public void back(View v)
    {
        startActivity(new Intent(ExamplePage.this, HomePage.class));
    }
}