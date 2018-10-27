package com.example.cheonyeji.noonsaoomgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    SharedPreferences nickname;
    ImageButton Button_item1, Button_item2, Button_item3, Button_send;
    ImageView image_item2;
    private WindowManager.LayoutParams params;
    private float brightness; // 화면 밝기값
    private Timer mTimer1;
    private int time; // 타이머로 잴 시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        params = getWindow().getAttributes();

        mTimer1 = new Timer();
        mTimer1.schedule(checktime,0,1000);

        Button_item1 = this.findViewById(R.id.Button_item1);
        Button_item2 = this.findViewById(R.id.Button_item2);
        Button_item3 = this.findViewById(R.id.Button_item3);
        Button_send = this.findViewById(R.id.Button_send);
        image_item2 = this.findViewById(R.id.image_item2);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                builder.setTitle("경기결과");
                builder.setMessage("아쉽게도 패배했습니다");
                builder.setPositiveButton("대기화면으로 돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        }, 10000 );

        image_item2.setVisibility(View.GONE);
        Button_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"1번 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });


        Button_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time >= 2){
                    Toast.makeText(getApplicationContext(), "2번 아이템이 클릭되었습니다.", Toast.LENGTH_SHORT).show();
                    image_item2.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            image_item2.setVisibility(View.GONE);
                        }
                    }, 2000 );
                }

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

    // 타이머 코드
    TimerTask checktime = new TimerTask() {
        @Override
        public void run() {
            time++;
        }
    };

}



