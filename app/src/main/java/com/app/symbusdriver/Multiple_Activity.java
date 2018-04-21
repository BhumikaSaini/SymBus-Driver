package com.app.symbusdriver;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Multiple_Activity extends AppCompatActivity {

    Handler h;
    Runnable r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_);

        r=new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Multiple_Activity.this,LoginQRActivity.class);
                startActivity(intent);
                
                finish();
            }
        };
        h=new Handler();
        h.postDelayed(r,2000);

    }
}
