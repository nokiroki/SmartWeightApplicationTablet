package ru.ku.yfrsmartweight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class FinalActivity extends AppCompatActivity {

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        bundle = getIntent().getExtras();

        TextView name = findViewById(R.id.final_name);
        TextView date = findViewById(R.id.final_date);
        TextView time = findViewById(R.id.final_time);
        TextView department = findViewById(R.id.final_department);



    }


    private int calculateRating (int mass, int currMass) {
        float rate = Math.abs(((float)(currMass - mass)/mass)*100);
        if (rate >= 0 && rate <= 5) { return 5; }
        else if (rate > 5 && rate <= 15) { return 4; }
        else if (rate > 15 && rate <= 30) {return 3; }
        else if (rate > 30 && rate <= 50) {return 2; }
        else { return 1; }
    }



}
