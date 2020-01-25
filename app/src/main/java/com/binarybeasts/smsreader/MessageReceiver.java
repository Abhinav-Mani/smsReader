package com.binarybeasts.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;

public class MessageReceiver extends BroadcastReceiver {

    private static MessageListener mListener;

    public MessageReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])pdus[i]);
            String message = "Sender: "+ smsMessage.getDisplayOriginatingAddress()+"\n"+
                    "Email From: "+smsMessage.getEmailFrom()+"\n"+
                    "Email Body: "+smsMessage.getEmailBody()+"\n"+
                    "Display Message Body: "+smsMessage.getDisplayMessageBody()+"\n"+
                    "Time in millseconds: "+smsMessage.getTimestampMillis()+"\n"+
                    "Message: "+smsMessage.getMessageBody();

            mListener.messageReceived(message);
        }
    }
    public static void bindListener(MessageListener listener){
        mListener = listener;
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}
