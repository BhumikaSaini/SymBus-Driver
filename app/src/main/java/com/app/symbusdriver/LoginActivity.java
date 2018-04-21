package com.app.symbusdriver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class LoginActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zXingScannerView=new ZXingScannerView(this);
        setContentView(zXingScannerView);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(this, "Permission is granted!", Toast.LENGTH_SHORT).show();
             }
            else
            {
                requestPermission();
            }
        }
    }
    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode,String permission[],int grantResults[])
    {
        switch (requestCode)
        {
            case REQUEST_CAMERA:
                if (grantResults.length>0)
                {
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted)
                    {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                        {
                          if (shouldShowRequestPermissionRationale(CAMERA))
                          {
                              displayAlertMessage("You need to allow access for both permission",  new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                                      {
                                          requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                      }
                                  }
                              });
                                return;
                          }
                        }
                    }
                }
                break;
        }
    }
    public void onResume()
    {
        super.onResume();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                if(zXingScannerView==null)
                {
                    zXingScannerView=new ZXingScannerView(this);
                    setContentView(zXingScannerView);
                }
                zXingScannerView.setResultHandler(this);
                zXingScannerView.startCamera();
            }
            else {
                requestPermission();
            }
        }
    }
    public void onDestroy()
    {
        super.onDestroy();
        zXingScannerView.stopCamera();
    }
    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener)
    {
       new AlertDialog.Builder(LoginActivity.this).setMessage(message).setPositiveButton("OK",listener).setNegativeButton("Cancel",null).create().show();
    }
    @Override
    public void handleResult(Result result) {

        String scanResult=result.getText();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zXingScannerView.resumeCameraPreview(LoginActivity.this);
            }
        });
        builder.setNegativeButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                Intent intent=new Intent(LoginActivity.this,ChooseSDActivity.class);
                startActivity(intent);
            }
        });
        builder.setMessage(scanResult);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
