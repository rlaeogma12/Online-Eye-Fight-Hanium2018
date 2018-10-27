package com.example.cheonyeji.noonsaoomgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingActivity extends AppCompatActivity {
    ImageButton Button_notice, Button_profile, Button_sound, Button_help, Button_contact, Button_logout, Button_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button_notice = this.findViewById(R.id.Button_notice);
        Button_profile = this.findViewById(R.id.Button_profile);
        Button_sound = this.findViewById(R.id.Button_sound);
        Button_help = this.findViewById(R.id.Button_help);
        Button_contact = this.findViewById(R.id.Button_contact);
        Button_logout = this.findViewById(R.id.Button_logout);
        Button_back = this.findViewById(R.id.Button_back);



        // Logout 버튼 클릭시 ID정보 삭제 & 로그인 화면 복귀
        Button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences nickname = getSharedPreferences("nickname", MODE_PRIVATE);
                SharedPreferences.Editor editor = nickname.edit();
                editor.remove("nickname");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // Back 버튼 클릭시 방 대기화면 복귀
        Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();

            }
        });


    }

    private void showExitDialog(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                        startActivity(intent);
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}
