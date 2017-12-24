package com.agrobx.agrotic.tutorial;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyfirstActivity extends AppCompatActivity implements View.OnClickListener {
    int i = 0;
    public void onClick(View _buttonView) {

        if (_buttonView.getId() == R.id.plus1_ButtonId) {
            i += 1;
            Log.i("myTag", Integer.toString(i));
        }
        else if (_buttonView.getId() == R.id.plus2_ButtonId) {
            i += 2;
            Log.i("myTag", Integer.toString(i));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_first_activity);
        TextView textView = (TextView) findViewById(R.id.textViewid);
        textView.setText("Ma première textview !");

        Resources res = getResources();
        String[] strings = res.getStringArray(R.array.testStringArray);
        for (String s : strings) {
            Log.i("MyTag", s);
        }

        Button btn1 = (Button) findViewById(R.id.plus1_ButtonId);
        btn1.setOnClickListener(this); // methode 1 qui va faire appel à onClick ligne 13

        Button btn2 = (Button) findViewById(R.id.plus2_ButtonId);
        btn2.setOnClickListener(this);

        // methode 2
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.i("myTag", "click 2 !!");
//                }
//            });

//        Log.d("MyTag", "zbraaaaaaaa !");


    }

//    public void validate(View _buttonView) {
//        if (_buttonView.getId() == R.id.validateButtonId) {
//            Log.i("myTag", "click !!");
//        }
//    }
}
