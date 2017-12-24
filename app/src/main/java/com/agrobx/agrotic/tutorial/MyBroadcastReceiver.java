package com.agrobx.agrotic.tutorial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;



public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        if (intent.getAction().equals("agrobx.agrotic.tutorial.MyAction") == true){
            Log.i("MBR","MyAction: " + intent.getStringExtra("name"));
        }
        else if (intent.getAction().equals("android.intent.action.BATTERY_LOW") == true){
            Log.i("MBR", "Batterie low !");
        }
        else if (intent.getAction().equals("android.intent.action.BATTERY_OKAY") == true){
            Log.i("MBR", "Batterie ok !");
        }
        else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") == true){
            final Bundle bundle = intent.getExtras();

            if (bundle != null){
                final Object [] pdusObj = (Object []) bundle.get("pdus");
                for (int i =0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte []) pdusObj[i]);
                    String phonenumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phonenumber;
                    String message = currentMessage.getMessageBody();
                    Log.i("MBR", "SMS received: senderNum: " + senderNum + "; message: " + message);
                }
            }
        }


    }

}
