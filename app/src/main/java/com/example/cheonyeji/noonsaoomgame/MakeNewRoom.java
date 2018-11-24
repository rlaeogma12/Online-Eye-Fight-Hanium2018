package com.example.cheonyeji.noonsaoomgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MakeNewRoom extends Activity {

    Socket socket;
    DataOutputStream output;
    DataInputStream input;
    SharedPreferences nickname;

    ImageButton Button_unlock, Button_lock, Button_make, Button_back;
    EditText Password, Roomname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_make_new_room);


        Button_unlock = this.findViewById(R.id.Button_unlock);
        Button_lock = this.findViewById(R.id.Button_lock);
        Button_make = this.findViewById(R.id.Button_make);
        Button_back = this.findViewById(R.id.Button_back);
        Password = this.findViewById(R.id.password);
        Roomname = this.findViewById(R.id.roomname);

        // 안보이게
        Password.setVisibility(View.GONE);
        Button_lock.setVisibility(View.GONE);

        nickname = getSharedPreferences("nickname",MODE_PRIVATE);
        final String userid = nickname.getString("nickname","");

        Button_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_lock.setVisibility(View.VISIBLE);
                Button_unlock.setVisibility(View.GONE);
                Password.setVisibility(View.VISIBLE);
            }
        });

        Button_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_lock.setVisibility(View.GONE);
                Button_unlock.setVisibility(View.VISIBLE);
                Password.setVisibility(View.GONE);
            }
        });

        Button_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // 만들기 버튼이 눌렸을때 입력된 비밀번호, 방이름을 로컬 변수에 저장
                //SharedPreferences nickname = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences password = getSharedPreferences("password", MODE_PRIVATE);
                SharedPreferences.Editor editor = password.edit();
                SharedPreferences roomname = getSharedPreferences("roomname", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = roomname.edit();
                editor.putString("password",Password.getText().toString());
                editor2.putString("roomname",Roomname.getText().toString());

                editor.commit();
                editor2.commit();
                */

                if (Roomname.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "방 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    String Result = SendAndReceive(Roomname.getText().toString(), userid);
                    switch (Result) {
                        case "방이름겹침":
                            Toast.makeText(getApplicationContext(), "방 이름이 이미 존재합니다", Toast.LENGTH_SHORT).show();
                            break;
                        case "방만들기성공":
                            Toast.makeText(getApplicationContext(), "방 만들기 성공!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "다시 만들어주세요", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

        Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                startActivity(intent);
            }
        });

    }

    String SendAndReceive(String roomname, String Userid) {

        String result = null;

        try {
            int port = 7070;
            String ip = "35.229.61.173";
            this.socket = new Socket(ip, port);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            output.writeUTF("방만들기");
            output.writeUTF(roomname);
            result = input.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        assert result != null;
        switch (result) {
            case "방이름겹침":
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "방이름겹침";

            case "방만들기가능":
                try {
                    output.writeUTF(Userid);
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "방만들기성공";

        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "실패";
    }
}
