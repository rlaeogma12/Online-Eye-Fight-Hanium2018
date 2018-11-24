package com.example.cheonyeji.noonsaoomgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    //Button
    ImageButton LoginButton;
    ImageButton QuitButton;
    SharedPreferences nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("loading.json");
        animationView.loop(true);
        animationView.playAnimation();

        LoginButton = this.findViewById(R.id.LoginButton);
        QuitButton = this.findViewById(R.id.QuitButton);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = getSharedPreferences("nickname",MODE_PRIVATE);
                String checkData = nickname.getString("nickname","");
                if ( checkData.equals("") ) // 만약 비어있다면 (즉, 닉네임을 입력한 적이 없다면)
                {
                    // LoginActivity로 이동하는 intent 생성
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    // 액티비티 시작
                    startActivity(intent);
                }
                else // 닉네임 입력 기록이 있다면
                {
                    // RoomActivity로 이동
                    Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                    Toast.makeText(getApplicationContext(),nickname.getString("nickname","")+"님 환영합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                }
            }
        );

        QuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("재미있는 게임이 기다리고 있답니다!");
                builder.setTitle("진짜 종료할거예요?")
                        .setCancelable(false)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                Toast.makeText(getApplicationContext(),"좋아요!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"다음에 다시 만나길 바랄게요!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("앱을 종료하시겠습니까?");
                alert.show();
            }
        });


    }


}
