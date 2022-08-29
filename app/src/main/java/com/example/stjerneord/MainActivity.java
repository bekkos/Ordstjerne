package com.example.stjerneord;

import static android.graphics.Color.rgb;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stjerneord.models.WordStage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    List<Button> buttons = new ArrayList<>();
    List<WordStage> stages = new ArrayList<>();
    WordStage activeStage;
    int level;
    int language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(rgb(44, 62, 80));

        // Check Language Setting
        SharedPreferences prefsSettings = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        language = prefsSettings.getInt("language", 0);
        System.out.println("-- f  --");
        System.out.println(language);


        // Get all buttons, add to buttonList
        for (int i = 1; i < 8; i++) {
            int id = getResources().getIdentifier("btn"+i, "id", getPackageName());
            buttons.add((Button) findViewById(id));
        }
        // Load letters and anagrams, create wordStages.
        for(int i = 1; i < 42; i++) {
            String resName = "nb_array_" + i;
             if(language == 1) {
                resName = "en_array_" + i;
            }
            int resId = MainActivity.this.getResources().getIdentifier(
                    resName,
                    "array",
                    MainActivity.this.getPackageName()
            );
            WordStage wordStage = new WordStage(getResources().getStringArray(resId));
            stages.add(wordStage);
        }
        activeStage = stages.get(0);
        level = 0;


        // LOAD
        SharedPreferences prefs = this.getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        int saveLanguage = prefs.getInt("language", 0);
        System.out.println("--- j");
        System.out.println(saveLanguage);
        if(language == saveLanguage) {
            // Load activeStage
            String bufferActiveStage = prefs.getString("activeStage", "");
            if(!bufferActiveStage.equals("")) {
                activeStage = gson.fromJson(bufferActiveStage, WordStage.class);

            }

            // Load stages
            String bufferStages = prefs.getString("stages", "");
            if(!bufferStages.equals("")) {
                Type type = new TypeToken<List<WordStage>>(){}.getType();
                stages = gson.fromJson(bufferStages, type);
            }
        }
        level = prefs.getInt("level", 0);

        updateUi();
    }

    public void updateUi() {
        /*
        Denne funksjonen oppdaterer alt av UI og kjøres etter endringer i spillets "state". F.eks
        ved endring av score eller overgang til nytt nivå.
         */

        // Draw buttons
        for(int x = 0; x < buttons.size(); x++) {
            buttons.get(x).setText("" + activeStage.getLetters().charAt(x));
        }

        // Draw score
        TextView tv = (TextView) findViewById(R.id.scoreOutput);
        tv.setText(activeStage.getScore() + " / " + activeStage.getMaxScore());

        // Progressbar
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressOutput);
        pb.setMax(activeStage.getMaxScore());
        pb.setProgress(activeStage.getScore());

        // Level UI
        TextView tv2 = (TextView) findViewById(R.id.levelOutput);
        tv2.setText("NIVÅ " + level);
    }

    public void click(View view) {
        Button btn = (Button) findViewById(view.getId());
        CharSequence letter = btn.getText();
        TextView tv = (TextView) findViewById(R.id.guessDisplay);
        tv.setText((String) tv.getText() + letter);
        animateViewShake(view);

    }

    public void hint(View view) {
        /*
        Funksjonen leter etter ord som ikke allerede har blitt funnet, obstrukterer de og
        viser de til spilleren. Funksjonen kjøres hovedsaklig ved bruker-input.
        */
        while(true) {
            if(activeStage.getDiscoveredWords().size() == activeStage.getWords().size()) break;
            String word = activeStage.getWords().get(new Random().nextInt(activeStage.getWords().size()));
            if(!activeStage.getDiscoveredWords().contains(word)) {
                //
                String out = "";
                for(int i = 0; i < word.length(); i++) {
                    if(i == 0 || i == word.length() / 2) {
                        out += "*";
                    } else {
                        out += word.charAt(i);
                    }
                }
                // Put out at hint location
                TextView tv = (TextView) findViewById(R.id.hintOutput);
                tv.setText(out);
                break;
            }
        }
    }

    public void check(View view) {
        animateViewShake(view);
        TextView tv = (TextView) findViewById(R.id.guessDisplay);
        if(tv.getText().length() <= 3) {
            setErrorMessage("Ordet må være mer enn 3 bokstaver.");
            return;
        }
        if(tv.getText().length() > 7) {
            setErrorMessage("Ordet kan ikke være mer enn 7 bokstaver.");
            return;
        }
        setErrorMessage("");

        if(activeStage.getDiscoveredWords().contains(tv.getText())) {
            setErrorMessage("Du har allerede funnet dette ordet.");
            return;
        }

        String s = tv.getText().toString();

        if(!s.contains(activeStage.getMainLetter().toString())) {
            setErrorMessage("Ordet må inneholde bokstaven '" + activeStage.getMainLetter() + "'.");
            return;
        }

        if(activeStage.getWords().contains(tv.getText())) {
            activeStage.setScore(activeStage.getScore() + 1);
            activeStage.addDiscoveredWord((String) tv.getText());
            tv.setTextColor(Color.GREEN);
            animateViewShake((TextView) findViewById(R.id.titleOutput));
            animateSuccess(tv);
            updateUi();
        } else {
            setErrorMessage("Ordet er ikke i listen.");
        }

        if(activeStage.getScore() == activeStage.getMaxScore()) {
            // PLAY STAGE END SCREEN
            activeStage.setScore(0);
            activeStage = stages.get(1);
            stages.remove(0);
            level++;
            updateUi();
        }





    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("level", level);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            level = savedInstanceState.getInt("level");
        }
    }


    /* TOOLBOX */
    public void clear(View view) {
        TextView tv = (TextView) findViewById(R.id.guessDisplay);
        tv.setText("");
        setErrorMessage("");
        animateViewShake(view);
    }

    public void clearHint(View view) {
        TextView tv2 = (TextView) findViewById(R.id.hintOutput);
        tv2.setText("");
    }


    public void setErrorMessage(String msg) {
        TextView errorOut = (TextView) findViewById(R.id.errorMsg);
        errorOut.setTextColor(Color.RED);
        errorOut.setText(msg);
    }


    /* LAGRING */

    @Override
    public void onPause() {

        SharedPreferences prefs = this.getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String activeStageJSON = gson.toJson(activeStage);
        // Save active stage object
        prefs.edit().putString("activeStage", activeStageJSON).apply();
        // Save level variable
        prefs.edit().putInt("level", level).apply();
        // Save stages object
        String stagesJSON = gson.toJson(stages);
        prefs.edit().putString("stages", stagesJSON).apply();

        prefs.edit().putInt("language", language).apply();


        super.onPause();

    }

    /* ANIMASJONER */
    public void animateViewSpin(View view) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationY", 180);
        animation.setDuration(250);
        animation.start();
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator animation2 = ObjectAnimator.ofFloat(view, "rotationY", 0);
                animation2.setDuration(250);
                animation2.start();
            }
        });
    }

    public void animateViewShake(View view) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "scaleX", 1.1f);
        animation.setDuration(250);
        animation.start();
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator animation2 = ObjectAnimator.ofFloat(view, "scaleX", 1f);
                animation2.setDuration(250);
                animation2.start();
            }
        });
    }

    public void animateSuccess(View view) {
        ObjectAnimator anim_scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1.3f);
        ObjectAnimator anim_scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1.3f);
        anim_scaleUpX.setDuration(1000);
        anim_scaleUpY.setDuration(1000);
        AnimatorSet success = new AnimatorSet();
        success.play(anim_scaleUpX).with(anim_scaleUpY);
        success.start();
        success.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                TextView a = (TextView) view;
                a.setTextColor(Color.WHITE);
                view.setScaleX(1f);
                view.setScaleY(1f);
                clear(view);
                clearHint(view);
            }
        });
    }
}