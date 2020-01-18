package com.example.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class IncomingSms extends BroadcastReceiver {

    private DatabaseReference RootRef;
    private static final String TAG = "IncomingSms";
    final SmsManager smsManager = SmsManager.getDefault();

    public IncomingSms() {
        super();
        RootRef = FirebaseDatabase.getInstance().getReference();
        Log.i("Voila", "IncomingSms: Entered");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if(bundle!=null){
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for(int i=0;i<pdusObj.length;i++){
                    SmsMessage currentMessage  = SmsMessage.createFromPdu((byte[])pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    if(Pattern.matches("((([a-zA-Z]+)[ ]+([a-zA-Z0-9]+))[\\n]*){4,6}",message)){
                    Log.i(TAG, "onReceive: "+ senderNum+" "+message);
//                    checkTheFormat(senderNum,message);
                    insertIntoDatabase(senderNum,message);
                    Toast.makeText(context, senderNum+" "+message , Toast.LENGTH_LONG).show();}
                    else {
                        Toast.makeText(context, "Not of same pattern", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG, "onReceive: ",e );
        }
    }

    private void checkTheFormat(String senderNum, String message) {
        String messages[] = message.split("\\s+");
        for(int i=0;i<messages.length;i++){

        }
    }

    private void insertIntoDatabase(String senderNum, String message) {
        Date date = new Date();
        long time = date.getTime();
        Log.i(TAG, "insertIntoDatabase: "+time);
        HashMap<String,Object> itemsMap = new HashMap<>();
        itemsMap.put("Number",senderNum);

        itemsMap.put("Message",message);
        RootRef.child(String.valueOf(time)).updateChildren(itemsMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i(TAG, "onComplete: Inserted Successfully");
                        }else{
                            Log.i(TAG, "onComplete: Something is fishy");
                        }
                    }
                });
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}

// ((([a-zA-Z]+)[ ]+([a-zA-Z0-9]+))[\n]){2}