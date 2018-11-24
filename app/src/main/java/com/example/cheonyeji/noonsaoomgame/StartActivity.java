

package com.example.cheonyeji.noonsaoomgame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class StartActivity extends AppCompatActivity {

    SharedPreferences nickname;

    Socket socket;
    DataOutputStream output;
    DataInputStream input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("loading.json");
        animationView.loop(true);
        animationView.playAnimation();



        // 2초 딜레이 넣어놓는 코드 (만약 서버에서 아이디 체크하는 시간이 2초가 넘으면 빼셔도 돼요!)

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                    if(SendAndReceive(checkData).equals("접속1성공")) {
                        // RoomActivity로 이동
                        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                        Toast.makeText(getApplicationContext(), nickname.getString("nickname", "") + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "다시 접속해주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                }
            }
        }, 2000 );

    }

    String SendAndReceive(String username) {

        String result = null;

        try {
            int port = 7070;
            String ip = "35.229.61.173";
            this.socket = new Socket(ip, port);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            output.writeUTF("서버접속시도");
            output.writeUTF("접속1");
            output.writeUTF(username);
            result = input.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }


        assert result != null;
        if(result.equals("접속1성공")){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "접속1성공";
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "실패";
    }
}
