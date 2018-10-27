package com.example.cheonyeji.noonsaoomgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.time.chrono.Era;
import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    //사용하는 요소 선언
    ListView RoomList;
    ImageButton Button_ch, Button_makeRoom, Button_setting, Button_quickStart, Button_send;
    ArrayList<String> rooms;
    SharedPreferences roomname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        RoomList = (ListView)findViewById(R.id.RoomList);

        //Room 정보 담는 리스트 생성 및 어댑터 만들어서 넘겨줌 (우선 이렇게 만들어놓고 나중에 합칠때 코드수정)
        //Room + 변수명으로 해놨다가 사용자가 이름을 설정하면 변수명이 아니라 따로되게! sharedpreference 써서 -> 10.09 미구현

        //기본 방 정보 저장
        rooms = new ArrayList<String>();
        rooms.add("Room 1");
        rooms.add("Room 2");



        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rooms);

        RoomList.setAdapter(adapter);

        // 만약 새로 생성해야 하는 값이 있는지 체크
        roomname = getSharedPreferences("roomname", MODE_PRIVATE);
        String checkroom = roomname.getString("roomname","");
        if(!checkroom.equals("")) // 새로운 방을 만들어야한다면
        {
            // 현재는 이 값이 창을 옮기면 세이브가 안되서 날라가는데 서버에서 받아오는 방식으로 변경하면 문제 없을것으로 보임!!!!!
            rooms.add(roomname.getString("roomname",""));
            // 생성 후 roomname 변수 비우기
            SharedPreferences.Editor editor = roomname.edit();
            editor.remove("roomname");
            editor.commit();
            //리스트 목록 갱신
            ((ArrayAdapter) adapter).notifyDataSetChanged();
        }
        //Listview 클릭 시 이벤트 발생
        RoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"방이 클릭되었습니다",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),GameActivity.class);
                startActivity(intent);
            }
        });

        Button_ch = this.findViewById(R.id.Button_ch);
        Button_makeRoom = this.findViewById(R.id.Button_makeRoom);
        Button_setting = this.findViewById(R.id.Button_setting);
        Button_quickStart = this.findViewById(R.id.Button_quickStart);
        Button_send = this.findViewById(R.id.Button_send);

        Button_makeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MakeNewRoom 화면 이동
                Intent intent = new Intent(getApplicationContext(), MakeNewRoom.class);
                startActivity(intent);
                }
             });

        Button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting 화면 이동
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
            }
        });

    }
}
