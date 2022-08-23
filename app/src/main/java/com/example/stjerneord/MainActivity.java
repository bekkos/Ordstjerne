package com.example.stjerneord;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stjerneord.models.WordStage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Button> buttons = new ArrayList<>();
    List<WordStage> stages = new ArrayList<>();
    WordStage activeStage;
    int level;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Get all buttons, add to buttonList
        for (int i = 1; i < 8; i++) {
            int id = getResources().getIdentifier("btn"+i, "id", getPackageName());
            buttons.add((Button) findViewById(id));
        }
        // Load letters and anagrams, create wordStages.
        for(int i = 0; i < 5; i++) {
            WordStage wordStage = new WordStage(getResources().getStringArray(R.array.no_array_1 + i));
            stages.add(wordStage);
        }
        activeStage = stages.get(0);
        WordStage buffer = stages.get(0);
        stages.remove(0);
        stages.add(buffer);
        level = 0;
        updateUi();
    }

    public void updateUi() {
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

    public void check(View view) {
        animateViewShake(view);
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
            activeStage = stages.get(0);
            WordStage buffer = stages.get(0);
            stages.remove(0);
            stages.add(buffer);
            level++;
            updateUi();
        }


    }

    public void clear(View view) {
        TextView tv = (TextView) findViewById(R.id.guessDisplay);
        tv.setText("");
        setErrorMessage("");
        animateViewShake(view);
    }

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
                a.setTextColor(Color.BLACK);
                view.setScaleX(1f);
                view.setScaleY(1f);
                clear(view);
            }
        });
    }

    public void setErrorMessage(String msg) {
        TextView errorOut = (TextView) findViewById(R.id.errorMsg);
        errorOut.setTextColor(Color.RED);
        errorOut.setText(msg);
    }
}