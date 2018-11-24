package com.example.cheonyeji.noonsaoomgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

        // 다이얼로그
        final AlertDialog.Builder notice = new AlertDialog.Builder(SettingActivity.this);
        notice.setTitle("공지사항");
        notice.setMessage("현재 version 0.1이며 차후 다양한 기능이 개발될 예정입니다");
        notice.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog.Builder contact = new AlertDialog.Builder(SettingActivity.this);
        contact.setTitle("Contact Us");
        contact.setMessage("성균관대학교 컴퓨터교육과 Eyetech\n김대흠\n이성제\n천예지 ssaltuck@gmail.com");
        contact.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Button_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice.show();
            }
        });

        Button_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contact.show();
            }
        });

        Button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

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
