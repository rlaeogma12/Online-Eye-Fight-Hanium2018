package com.example.cheonyeji.noonsaoomgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // Button
    ImageButton Gobutton;
    EditText Nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                    Toast.makeText(getApplicationContext(),nickname.getString("nickname","")+"님 환영합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

            }
        });
    }
}
