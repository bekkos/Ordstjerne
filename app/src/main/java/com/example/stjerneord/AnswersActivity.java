package com.example.stjerneord;

import static android.graphics.Color.WHITE;
import static android.graphics.Color.rgb;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stjerneord.models.WordStage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class AnswersActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(rgb(44, 62, 80));

        LinearLayout ly = (LinearLayout) findViewById(R.id.outputLayout);
        ArrayList<WordStage> stagesnb = new ArrayList<>();
        ArrayList<WordStage> stagesen = new ArrayList<>();
        for(int i = 1; i < 10; i++) {
            String resName = "nb_array_" + i;
            int resId = AnswersActivity.this.getResources().getIdentifier(
                    resName,
                    "array",
                    AnswersActivity.this.getPackageName()
            );
            WordStage wordStage = new WordStage(getResources().getStringArray(resId));
            stagesnb.add(wordStage);
        }
        for(int i = 1; i < 100; i++) {
            String resName = "en_array_" + i;
            int resId = AnswersActivity.this.getResources().getIdentifier(
                    resName,
                    "array",
                    AnswersActivity.this.getPackageName()
            );
            WordStage wordStage = new WordStage(getResources().getStringArray(resId));
            stagesen.add(wordStage);
        }

        TextView divider = new TextView(this);
        divider.setTextColor(WHITE);
        divider.setText("\n\n-- NORSK BOKMÅL --\n");
        ly.addView(divider);
        int id = 0;
        for(WordStage s:stagesnb) {
            TextView tvTitle = new TextView(this);
            tvTitle.setTextColor(WHITE);
            tvTitle.setText("Nivå " + id);
            ly.addView(tvTitle);
            TextView tv = new TextView(this);
            tv.setText(s.getWords().toString().toUpperCase(Locale.ROOT));
            tv.setTextColor(WHITE);
            ly.addView(tv);
            TextView spacing = new TextView(this);
            spacing.setText("   ");
            ly.addView(spacing);
            id++;
        }
        TextView divider2 = new TextView(this);
        divider2.setTextColor(WHITE);
        divider2.setText("\n\n-- ENGELSK --\n");
        ly.addView(divider2);
        id = 0;
        for(WordStage s:stagesen) {
            TextView tvTitle = new TextView(this);
            tvTitle.setTextColor(WHITE);
            tvTitle.setText("Nivå " + id);
            ly.addView(tvTitle);
            TextView tv = new TextView(this);
            tv.setText(s.getWords().toString());
            tv.setTextColor(WHITE);
            ly.addView(tv);
            TextView spacing2 = new TextView(this);
            spacing2.setText("   ");
            ly.addView(spacing2);
            id++;
        }
    }
}
