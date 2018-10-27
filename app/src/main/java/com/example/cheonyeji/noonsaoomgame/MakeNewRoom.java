package com.example.cheonyeji.noonsaoomgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class MakeNewRoom extends Activity {
    ImageButton Button_unlock, Button_lock, Button_make;
    EditText Password, Roomname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_make_new_room);


        Button_unlock = this.findViewById(R.id.Button_unlock);
        Button_lock = this.findViewById(R.id.Button_lock);
        Button_make = this.findViewById(R.id.Button_make);
        Password = this.findViewById(R.id.password);
        Roomname = this.findViewById(R.id.roomname);

        // 안보이게
        Password.setVisibility(View.GONE);
        Button_lock.setVisibility(View.GONE);

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

                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                startActivity(intent);
            }
        });

    }
}
