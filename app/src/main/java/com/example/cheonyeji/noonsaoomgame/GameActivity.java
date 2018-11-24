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
    ImageButton Button_item1, Button_item2, Button_item3, Button_send, Button_exit;
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
        Button_exit = this.findViewById(R.id.Button_exit);
        image_item2 = this.findViewById(R.id.image_item2);

        /*
        // 10초 뒤 패배하는 코드
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
        }, 10000 ); */



        image_item2.setVisibility(View.GONE);


        Button_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time >= 6)
                {
                    Toast.makeText(getApplicationContext(),"1번 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show();
                    // 화면 밝기 밝게!
                    brightness = params.screenBrightness;
                    params.screenBrightness = 1f;
                    getWindow().setAttributes(params);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 화면 밝기 어둡게
                            params.screenBrightness = brightness;
                            getWindow().setAttributes(params);
                        }
                    }, 2000 );
                }

            }
        });


        Button_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time >= 2){
                    Toast.makeText(getApplicationContext(), "2번 아이템이 클릭되었습니다.", Toast.LENGTH_SHORT).show();
                    image_item2.setVisibility(View.VISIBLE);
                    time -= 2;
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
                if(time >= 8)
                {
                    Toast.makeText(getApplicationContext(),"3번 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        nickname = getSharedPreferences("nickname", MODE_PRIVATE);
        TextView player1name = (TextView)findViewById(R.id.player1name);
        player1name.setText(nickname.getString("nickname",""));


        Button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GameActivity.this);
                builder.setMessage("게임을 그만할까요?");
                builder.setTitle("Exit Message")
                        .setCancelable(false)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                Toast.makeText(getApplicationContext(),"좋아요. 다시 한번 눈을 부릅!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"로비로 돌아갑니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                                startActivity(intent);
                            }
                        });
                android.support.v7.app.AlertDialog alert = builder.create();
                alert.setTitle("Oops");
                alert.show();
            }
        });


    }



    /*
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
    */
    // 타이머 코드
    TimerTask checktime = new TimerTask() {
        @Override
        public void run() {
            time++;
        }
    };

}



