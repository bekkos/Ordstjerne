package com.example.stjerneord;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> letters = new ArrayList<>();
    List<Button> buttons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        letters.add("G");
        letters.add("R");
        letters.add("A");
        letters.add("N");
        letters.add("Ø");
        letters.add("K");
        letters.add("L");

        for (int i = 1; i < 8; i++) {
            int id = getResources().getIdentifier("btn"+i, "id", getPackageName());
            buttons.add((Button) findViewById(id));
        }
        for(int x = 0; x < buttons.size(); x++) {
            buttons.get(x).setText(letters.get(x));
        }

    }

    public void test(View view) {
        Button btn = (Button) findViewById(view.getId());
        CharSequence letter = btn.getText();
        TextView tv = (TextView) findViewById(R.id.guessDisplay);
        tv.setText((String) tv.getText() + letter);
    }

    public void check(View view) {
        TextView tv = (TextView) findViewById(R.id.guessDisplay);
        if(tv.getText().length() <= 3) {
            setErrorMessage("Order må være mer enn 3 bokstaver.");
            return;
        }
        if(tv.getText().length() > 7) {
            setErrorMessage("Order kan ikke være mer enn 7 bokstaver.");
            return;
        }
        setErrorMessage("");
    }

    public void clear(View view) {
        TextView tv = (TextView) findViewById(R.id.guessDisplay);
        tv.setText("");
        setErrorMessage("");
    }

    public void setErrorMessage(String msg) {
        TextView errorOut = (TextView) findViewById(R.id.errorMsg);
        errorOut.setTextColor(Color.RED);
        errorOut.setText(msg);
    }
}