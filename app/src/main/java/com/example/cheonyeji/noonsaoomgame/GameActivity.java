package com.example.cheonyeji.noonsaoomgame;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    SharedPreferences nickname;
    ImageButton Button_item1, Button_item2, Button_item3, Button_send;
    private WindowManager.LayoutParams params;
    private float brightness; // 화면 밝기값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        params = getWindow().getAttributes();


        Button_item1 = this.findViewById(R.id.Button_item1);
        Button_item2 = this.findViewById(R.id.Button_item2);
        Button_item3 = this.findViewById(R.id.Button_item3);
        Button_send = this.findViewById(R.id.Button_send);

        Button_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"1번 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        Button_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"2번 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show();

            }
        });

        Button_item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"3번 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        nickname = getSharedPreferences("nickname", MODE_PRIVATE);
        TextView player1name = (TextView)findViewById(R.id.player1name);
        player1name.setText(nickname.getString("nickname",""));
    }

    // 화면 밝기 값 수정
    protected  void onResume() {
        super.onResume();
        brightness = params.screenBrightness;
        params.screenBrightness = 1f;
        getWindow().setAttributes(params);
    }

    // 기존 밝기로 변경
    protected  void onPause() {
        super.onPause();
        params.screenBrightness = brightness;
        getWindow().setAttributes(params);
    }
}
