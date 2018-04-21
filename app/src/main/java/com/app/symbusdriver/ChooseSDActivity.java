package com.app.symbusdriver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseSDActivity extends AppCompatActivity {

    Button BSBtoSIT,BSITtoSB,BCLogout;
    String strDest, busNo;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sd);
        Intent i= getIntent();
        busNo= i.getStringExtra("busNo");
        BSBtoSIT=(Button)findViewById(R.id.BSBtoSIT);
        BCLogout=(Button)findViewById(R.id.BChooseLogout);
        BSITtoSB=(Button)findViewById(R.id.BSITtoSB);
        BCLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent servIntent = new Intent(this,MyService.class);
                //stopService(servIntent);//isrunning
                finish();
            }
        });
        BSITtoSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strDest="SBRoad";
                Intent i=new Intent(ChooseSDActivity.this,MapsActivity.class);
                i.putExtra("busNo", busNo);
                i.putExtra("destn", strDest);
                startActivity(i);
                finish();
            }
        });
        BSBtoSIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strDest="SIT";
                Intent i=new Intent(ChooseSDActivity.this,MapsActivity.class);
                i.putExtra("busNo", busNo);
                i.putExtra("destn", strDest);
                startActivity(i);
                finish();
            }
        });
    }
}
