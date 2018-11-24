package com.example.cheonyeji.noonsaoomgame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    ImageButton imageButton_confirm, imageButton_change, Button_back;
    ImageView imageview_user;
    TextView textView_ID, textView_char, textView_record;
    int cnt;

    // cnt를 그러면 sharedpreference로 만들어야하나? -> 성제오빠 코드랑 비교해서 물어보기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageButton_change = this.findViewById(R.id.imageButton_change);
        imageButton_confirm = this.findViewById(R.id.imageButton_confirm);
        Button_back = this.findViewById(R.id.Button_back);
        textView_ID = this.findViewById(R.id.textView_ID);
        textView_char = this.findViewById(R.id.textView_char);
        textView_record = this.findViewById(R.id.textView_record);
        cnt = 1;
        imageview_user=(ImageView) findViewById(R.id.imageview_user);

        for(int i = 1; i<=6;i++)
        {
            String resName = "@drawable/char_" + i;
            int resID = getResources().getIdentifier(resName, "drawable",getPackageName());
            
        }


        imageButton_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 한번 누르면 유저 이미지 바뀌도록
                if(cnt >= 5 ) cnt = 1;
                else cnt+=1;
                imageview_user.setImageResource(getResources().getIdentifier("char_"+cnt, "drawable",  getPackageName()));

            }
        });

        imageButton_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 유저 이미지 cnt값으로 전달
                imageview_user.setImageResource(getResources().getIdentifier("char_"+cnt, "drawable",  getPackageName()));
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
            }
        });

    }
}
