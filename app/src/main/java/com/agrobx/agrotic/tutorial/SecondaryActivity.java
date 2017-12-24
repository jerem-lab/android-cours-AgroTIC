package com.agrobx.agrotic.tutorial;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SecondaryActivity extends AppCompatActivity implements View.OnClickListener {

    public void onClick(View _buttonView) {
        if (_buttonView.getId() == R.id.OKButtonId) {
            // recupere contenu que l'on rentre dans le edit view et le rencoi vers la premiere activity quand on clic sur OK
            EditText welcomeEditText = (EditText) findViewById(R.id.editWelcomeId);
            Intent data = new Intent();
            data.putExtra("WelcomeTextKey", welcomeEditText.getText().toString());
            setResult(RESULT_OK, data);
            super.finish();
        } else if (_buttonView.getId() == R.id.cancelButtonId) {
            Log.i("myTag", "click cancel !!");
            setResult(RESULT_CANCELED);
            super.finish();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);


        Button btnOK = (Button) findViewById(R.id.OKButtonId);
        btnOK.setOnClickListener(this);

        Button btnCancel = (Button) findViewById(R.id.cancelButtonId);
        btnCancel.setOnClickListener(this);





        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
//        Log.i("mytag", extras.getString("WelcomeTextKey"));

        // on recupere un texte venant d'une autre appli
        String welcomeText = null;
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action)){
            if ("text/plain".equals(type)){
                welcomeText = intent.getStringExtra(Intent.EXTRA_TEXT);
//                Log.i("myTag", welcomeText);

            }
        }
        else {
            welcomeText = extras.getString("WelcomeTextKey");
        }

        TextView textView = (TextView) findViewById(R.id.editWelcomeId);
        textView.setText(welcomeText);

        // on utilise la nouvelle version de l'action bar : la toolbar
        final Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(topToolBar);

        getSupportActionBar().setLogo(R.drawable.logo_agrobx);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
