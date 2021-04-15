package com.voicecloningmobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomePage extends AppCompatActivity {

    //GUI variables
    private ViewPager viewPager;
    private SlideAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new SlideAdapter(this);
        adapter.images = new int[]
        {
            R.drawable.digital,
            R.drawable.cover,
            R.drawable.robot
        };

        viewPager.setAdapter(adapter);
    }

    //MainActivity call
    public void tryForYourself(View v)
    {
        startActivity(new Intent(HomePage.this, MainActivity.class));
    }

    //ExamplePage call
    public void example(View v)
    {
        startActivity(new Intent(HomePage.this, ExamplePage.class));
    }
}