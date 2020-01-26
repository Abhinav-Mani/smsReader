package com.binarybeasts.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.binarybeasts.smsreader.Models.OrderRequest;
import com.binarybeasts.smsreader.Models.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class IncomingSms extends BroadcastReceiver {

    private DatabaseReference RootRef;
    private static final String TAG = "IncomingSms";
    final SmsManager smsManager = SmsManager.getDefault();
    final String[] requestMessage = {"No Any Product found"};

    public IncomingSms() {
        super();
        RootRef = FirebaseDatabase.getInstance().getReference();
        Log.i("Voila", "IncomingSms: Entered");
        //showData();
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

                    Log.i(TAG, "onReceive: "+ senderNum+" "+message);

//                    if(Pattern.matches("((([a-zA-Z]+)[ ]+([a-zA-Z0-9]+))[\\n]*){4,6}",message)){
//                    Log.i(TAG, "onReceive: "+ senderNum+" "+message);
                    checkTheFormat(senderNum,message);
//                    insertIntoDatabase(senderNum,message);
//                    Toast.makeText(context, senderNum+" "+message , Toast.LENGTH_LONG).show();}
//                    else {
//                        Toast.makeText(context, "Not of same pattern", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG, "onReceive: ",e );
        }
    }

    private void checkTheFormat(String senderNum, String message) {
        if(Pattern.matches("^SELL{1}[\\n]{1}((([a-zA-Z]+)[ ]+([a-zA-Z0-9]+))[\\n]*){4,6}",message) || message.equals("Format Did not Match")){
            String messages[] = message.split("\\s+");
            Log.d(TAG, "checkTheFormat: "+messages.length);
            HashMap<String,String> producthash = new HashMap<>();
            for(int i=1;i<messages.length;i+=2){
                Log.i(TAG, "checkTheFormat: "+messages[i]);
                producthash.put(messages[i],messages[i+1]);
            }
            try{
                Products products;
                producthash.put("CON","CALL");
                if(producthash.get("NM")!=null && producthash.get("PR") !=null && producthash.get("LOC") !=null && producthash.get("Q") !=null && producthash.get("D") !=null && senderNum!=null ){
                    if(producthash.get("CON") != null) {
                        products = new Products(producthash.get("NM"),producthash.get("PR"),producthash.get("LOC"),producthash.get("Q"),producthash.get("D"),senderNum,producthash.get("CON"));
                    }else{
                        products = new Products(producthash.get("NM"),producthash.get("PR"),producthash.get("LOC"),producthash.get("Q"),producthash.get("D"),senderNum);
                    }
                    insertIntoDatabase(products);
                    Log.i(TAG, "checkTheFormat: model"+products); 
                }else {
                    Log.i(TAG, "checkTheFormat: Entry missing");
                }
            }catch (Exception e){
                
                Log.i(TAG, "checkTheFormat: Product Error"+e);
                sendSMS(senderNum,"Format Did not Match");
            }

            Set set = producthash.entrySet();
            for (Object o : set) {
                Map.Entry mentry = (Map.Entry) o;
                Log.i(TAG, "key is " + mentry.getKey() + " and value is " + mentry.getValue());
            }
        }else if(message.equals("Show Me")){
            showData(senderNum);
        }
        else
        {
            Log.d(TAG, "checkTheFormat: Did not Match"+" "+senderNum);
            //sendSMS(senderNum,"Format Did not Match");
        }
    }

    private void showData(final String senderNum){
        Log.i("retrieval", "showData: Entered");
        RootRef.child("Products").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Products newProducts = dataSnapshot.getValue(Products.class);
//                919262237472
                if(newProducts.getPhoneNo().equals(senderNum)){
                    final String nameOfRequestProduct = newProducts.getProductName();
                    Log.i("retrieval", "name: "+ newProducts.getProductName());
                    Log.i("retrieval", "price: "+newProducts.getPrice());
                    Log.i("retrieval", "location: "+newProducts.getLocation());
                    Log.i("retrieval", "PhoneNo: "+newProducts.getPhoneNo());
                    Log.i("retrieval", "onChildAdded: Good"+" "+dataSnapshot.getKey());
//                    _ids.add(dataSnapshot.getKey());
                    try {
                        RootRef.child("Requests").child(Objects.requireNonNull(dataSnapshot.getKey()))
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        OrderRequest requestedProduct = dataSnapshot.getValue(OrderRequest.class);
                                        requestMessage[0] = "Name: "+nameOfRequestProduct + "\n"+ "Quantity: "+ requestedProduct.getAmount() +"\n"+ "Price: "+requestedProduct.getProductPrice()+"\n"+ "DeliverPrice: "+requestedProduct.getDeliverPrice();
                                        Log.i("Received", "onChildAdded: "+ requestMessage[0]);
                                        Log.i("Received", "Quantity: " + requestedProduct.getAmount());
                                        Log.i("Received", "price: " + requestedProduct.getProductPrice());
                                        Log.i("Received", "DeliverPrice: " + requestedProduct.getDeliverPrice());
                                        Log.i("Received", "PhoneNo: " + requestedProduct.getFrom());
                                        sendSMS(senderNum, requestMessage[0]);
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        sendSMS("+917903149292",requestMessage[0]);
                                    }
                                });
                    }catch (Exception e){
                        Log.i("received", "onChildAdded: this product not found");
                    }

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void insertIntoDatabase(Products products) {
        try {
            RootRef.child("Products").push().setValue(products)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG, "onComplete: Inserted Successfully");
                            }else{
                                Log.i(TAG, "onComplete: Not Inserted Successfully");
                            }
                        }
                    });
        }catch (Exception e){
            Log.i(TAG, "insertIntoDatabase: "+ e);
        }

    }

    private void sendSMS(String sendNum,String message){
//        ArrayList<PendingIntent> sentPendingIntent = new ArrayList<>();
//        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<>();
//        PendingIntent sentPI = PendingIntent.getBroadcast(this,0,new Intent(this,))
        Log.d("SendSMS", "sendSMS: Entered");
        smsManager.sendTextMessage(sendNum,null,message,null,null);
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}

// ((([a-zA-Z]+)[ ]+([a-zA-Z0-9]+))[\n]){2}