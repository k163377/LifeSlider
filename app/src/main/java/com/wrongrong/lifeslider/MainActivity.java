package com.wrongrong.lifeslider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    AdView mAdView;
    Spinner sex_spinner;
    TextView probability_text;
    TextView age_text,life_span_text;
    SeekBar age_seek,life_span_seek;

    int age,lifeSpan;
    boolean isMan;
    final DecimalFormat df = new DecimalFormat("#0.000");

    private void setProbability_text(){
        int start,end;
        byte i;
        float p;
        start=0;

        if(isMan) {
            for (i = 0; i < age; i++) start += Data.DeathTollOfMan[i];
            end = start;
            for (; i < lifeSpan; i++) end += Data.DeathTollOfMan[i];
            p = ((float)(100000-end)/(float)(100000-start))*100;
        }else{
            for (i = 0; i < age; i++) start += Data.DeathTollOfWoman[i];
            end = start;
            for (; i < lifeSpan; i++) end += Data.DeathTollOfWoman[i];
            p = ((float)(100005-end)/(float)(100005-start))*100;
        }
        probability_text.setText(df.format(p));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //数値系初期化
        age = 0;
        lifeSpan = 1;
        isMan = true;
        //AdView初期化
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8906600258681229~4354084398");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //spinner初期化
        sex_spinner = (Spinner) findViewById(R.id.sex_spinner);
        sex_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                Spinner sp = (Spinner) parent;

                if(sp.getSelectedItemPosition()==0 && !(isMan)){//男が選択され、現状が女
                    isMan = true;
                    //小さくなるのでここで弄らないと落ちるかも
                    age = Math.min(112,age);
                    lifeSpan = Math.min(113,lifeSpan);
                    //最大最小をセット
                    age_seek.setMax(112);
                    life_span_seek.setMax(113);
                }else if(sp.getSelectedItemPosition()==1 && isMan){//女が選択され、現状が男
                    isMan = false;
                    age_seek.setMax(115);
                    life_span_seek.setMax(116);
                }

                setProbability_text();
            }
            public void onNothingSelected(AdapterView<?> parent){setProbability_text();}
        });
        //確率表示
        probability_text = (TextView) findViewById(R.id.probability_text);
        //年齢と寿命表示
        age_text = (TextView) findViewById(R.id.age_text);
        life_span_text = (TextView) findViewById(R.id.life_span_text);
        //seek初期化
        age_seek = (SeekBar)findViewById(R.id.age_seek);
        age_seek.setMax(112);
        age_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                age = progress;
                lifeSpan = Math.max(lifeSpan,age+1);
                age_text.setText(String.valueOf(age));
                life_span_seek.setProgress(lifeSpan);
                setProbability_text();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {/* ツマミに触れたときに呼ばれる*/}
            public void onStopTrackingTouch(SeekBar seekBar) {/* ツマミを離したときに呼ばれる*/}
        });

        life_span_seek = (SeekBar)findViewById(R.id.life_span_seek);
        life_span_seek.setMax(113);
        life_span_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                if(progress==0)life_span_seek.setProgress(1);
                else {
                    lifeSpan = progress;
                    age = Math.min(age, lifeSpan - 1);
                    life_span_text.setText(String.valueOf(lifeSpan));
                    age_seek.setProgress(age);
                }
                setProbability_text();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {/* ツマミに触れたときに呼ばれる*/}
            public void onStopTrackingTouch(SeekBar seekBar) {/* ツマミを離したときに呼ばれる*/}
        });

        setProbability_text();
    }
}
