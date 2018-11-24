package com.example.cheonyeji.noonsaoomgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    Socket socket;
    DataOutputStream output;
    DataInputStream input;
    String result;

    // Button
    ImageButton Gobutton;
    EditText Nickname;

    int win = 0; //로컬에서 가져와야함!!!!!!!!!!!!!
    int lose = 0; //로컬에서 가져와야함!!!!!!!!!!!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Gobutton = this.findViewById(R.id.GoButton);
        Nickname = this.findViewById(R.id.editTextNickname);

        Gobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go 버튼이 눌렸을때 입력된 닉네임을 로컬 변수에 저장
                //SharedPreferences nickname = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences nickname = getSharedPreferences("nickname", MODE_PRIVATE);
                SharedPreferences.Editor editor = nickname.edit();
                // 닉네임에 아무것도 입력되지 않았을 때 화면 안 넘어가도록
                editor.putString("nickname",Nickname.getText().toString());
                if (Nickname.getText().toString().equals("") )
                {
                    Toast.makeText(getApplicationContext(),"이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String r = SendAndReceive(Nickname.getText().toString(), "접속2");
                    switch (r) {
                        case "접속2성공":
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                            Toast.makeText(getApplicationContext(), nickname.getString("nickname", "") + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            break;
                        case "중복":
                            Toast.makeText(getApplicationContext(), "이미 있는 닉네임입니다", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "다시 닉네임을 설정해주시기 바랍니다", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

            }
        });
    }

    String SendAndReceive(String username, String enterRoute) {

        try {
            int port = 7070;
            String ip = "35.229.61.173";
            this.socket = new Socket(ip, port);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            output.writeUTF("서버접속시도");
            output.writeUTF(enterRoute);
            output.writeUTF(username);
            result = input.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (result) {
            case "접속1성공":
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "접속1성공";
            case "접속2성공":
                try {
                    output.writeInt(win);
                    output.writeInt(lose);

                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "접속2성공";
            case "Duplicate":
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "중복";
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "실패";
    }
}
