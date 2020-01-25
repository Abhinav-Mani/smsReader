package com.binarybeasts.smsreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MessageListener{

    TextView showMessage;
    EditText fill;
    Button checkButton;

    private static final String TAG = "MainActivity";
    public static final int PERMISSION_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showMessage = findViewById(R.id.showMessage);
        fill = findViewById(R.id.fill);
        checkButton = findViewById(R.id.checkbutton);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = fill.getText().toString();
                String message[] = s.split("\\s+");
                String k="";
                for(int i=0;i<message.length;i++){
                    k= k+ message[i]+"next";
                }
                showMessage.setText(k);
            }
        });

        MessageReceiver.bindListener(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreate: Not given");
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_SMS)){
                
            }else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS},PERMISSION_CODE);
            }
        }else {
            Log.d(TAG, "onCreate: Given");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onRequestPermissionsResult: Yes");
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS},PERMISSION_CODE);
                }
                return;
            }
        }
    }

    @Override
    public void messageReceived(String message) {
        Log.i(TAG, "messageReceived: "+message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
