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
import android.widget.Toast;

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
    int levelEng;
    int levelNor;
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

        // Get all buttons, add to buttonList
        for (int i = 1; i < 8; i++) {
            int id = getResources().getIdentifier("btn"+i, "id", getPackageName());
            buttons.add((Button) findViewById(id));
        }
        // Load letters and anagrams, create wordStages.
        for(int i = 1; i < 10; i++) {
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
        int id = 0;
        for (WordStage s: stages) {
            id++;
        }
        activeStage = stages.get(0);

        // Load game state and overwrite base state if exists.
        SharedPreferences prefs = this.getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        levelNor = prefs.getInt("levelNor", 0);
        levelEng = prefs.getInt("levelEng", 0);
        Gson gson = new Gson();
        int saveLanguage = prefs.getInt("language", 0);
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
        } else {
            // Get level based on language
            if(language == 0) {
                activeStage = stages.get(levelNor);
            } else {
                activeStage = stages.get(levelEng);
            }
        }
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

        // Level Display
        TextView tv2 = (TextView) findViewById(R.id.levelOutput);
        if(language == 0) tv2.setText("NIVÅ " + levelNor);
        if(language == 1) tv2.setText("NIVÅ " + levelEng);

    }

    public void click(View view) {
        /*
        * Denne funksjonen kjører hver gang brukeren trykker på en av tastatur-knappene.
        * Legger til den respektive bokstaven i utdataen og starter animasjoner for å indikere trykk.
        * */
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
            if(!activeStage.getDiscoveredWords().contains(word.toLowerCase()) && !activeStage.getDiscoveredWords().contains(word.toUpperCase())) {
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
        /*
        * Denne funksjonen kjører når brukeren trykker på "sjekk"-knappen.
        * Sjekker om ordet oppfyller alle regler og krav, hvis JA så sjekker
        * den om ordet ikke er allerede funnet. Deretter sjekker den om ordet er
        * et løsningsord og iverkesetter riktig respons basert på det.
        *
        * */
        animateViewShake(view);

        // Lengde sjekk
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

        // Allerede funnet sjekk
        if(activeStage.getDiscoveredWords().contains(tv.getText())) {
            setErrorMessage("Du har allerede funnet dette ordet.");
            return;
        }

        // Hovedbokstav er med sjekk.
        String s = tv.getText().toString();
        if(!s.contains(activeStage.getMainLetter().toString())) {
            setErrorMessage("Ordet må inneholde bokstaven '" + activeStage.getMainLetter() + "'.");
            return;
        }

        // Ord er løsningsord sjekk.
        String inputString = (String) tv.getText();
        if(activeStage.getWords().contains(inputString.toLowerCase()) || activeStage.getWords().contains(inputString.toUpperCase())) {
            activeStage.setScore(activeStage.getScore() + 1);
            activeStage.addDiscoveredWord((String) tv.getText());
            tv.setTextColor(Color.GREEN);
            animateViewShake((TextView) findViewById(R.id.titleOutput));
            animateSuccess(tv);
            updateUi();
        } else {
            setErrorMessage("Ordet er ikke i listen.");
        }


        // Hvis scoren er lik maksscoren så har brukeren funnet alle ordene i dette nivået
        // og spillet går videre til neste nivå.
        if(activeStage.getScore() == activeStage.getMaxScore()) {
            // PLAY STAGE END SCREEN
            switch(language) {
                case 0:
                    levelNor++;
                    activeStage = stages.get(levelNor);
                    break;
                case 1:
                    levelEng++;
                    activeStage = stages.get(levelEng);
                    break;
            }
            activeStage.setScore(0);
            updateUi();
        }
    }

    // Denne kjører når brukeren klikker for å se hvilke ord de har funnet så langt.
    public void showProgress(View view) {
        String out = "";
        if(activeStage.getDiscoveredWords().size() > 0) {
            out = "Du har funnet følgende ord så langt: \n";
            for(String s:activeStage.getDiscoveredWords()) {
                out += s + " ";
            }
        } else {
            out = "Du har ikke funnet noen ord enda.";
        }
        Toast toats = Toast.makeText(this, out, Toast.LENGTH_LONG);
        toats.show();
    }

    // Kjører når brukeren trykker på "klarer"-knappen.
    // Sletter inndataen som brukeren har skrevet så langt.
    public void clear(View view) {
        TextView tv = (TextView) findViewById(R.id.guessDisplay);
        tv.setText("");
        setErrorMessage("");
        animateViewShake(view);
    }


    // Hjelpefunksjon for å klarere hint-teksten.
    public void clearHint(View view) {
        TextView tv2 = (TextView) findViewById(R.id.hintOutput);
        tv2.setText("");
    }

    // Hjelpefunksjon for å endre feilmeldingen.
    public void setErrorMessage(String msg) {
        TextView errorOut = (TextView) findViewById(R.id.errorMsg);
        errorOut.setTextColor(Color.RED);
        errorOut.setText(msg);
    }


    /* LAGRING
    * Denne funksjonen kjører når applikasjonen skrues av eller legges i android sin
    * definisjon av "pause". Her lagrer vi nåværende spillstatus og data til senere.
    * */

    @Override
    public void onPause() {
        SharedPreferences prefs = this.getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String activeStageJSON = gson.toJson(activeStage);

        prefs.edit().putString("activeStage", activeStageJSON).apply();

        prefs.edit().putInt("levelNor", levelNor).apply();
        prefs.edit().putInt("levelEng", levelEng).apply();

        String stagesJSON = gson.toJson(stages);
        prefs.edit().putString("stages", stagesJSON).apply();
        prefs.edit().putInt("stageScore", activeStage.getScore());
        prefs.edit().putInt("language", language).apply();
        super.onPause();
    }

    /* ANIMASJONER
    /* Noen animasjoner for å gi tydeligere tilbakemeldinger til brukeren når de interagerer
    /* med applikasjonen.
     */
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