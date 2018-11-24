package com.example.cheonyeji.noonsaoomgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class LobbyActivity extends Activity {

    Handler msghandler;
    SocketClient client;
    ReceiveThread receive;

    Socket socket;
    SharedPreferences nickname;

    ListView m_ListView;
    CustomAdapter m_Adapter;
    ImageButton btn_done, btn_submit, btn_back;
    EditText messageText;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //버튼 연결
        btn_done = findViewById(R.id.btn_done);
        btn_submit = findViewById(R.id.btn_submit);
        btn_back = findViewById(R.id.btn_back);

        messageText = findViewById(R.id.text_message);

        nickname = getSharedPreferences("nickname", MODE_PRIVATE);
        String userid = nickname.getString("nickname","");

        int port = 7070;
        String ipText = "35.229.61.173";
        client = new SocketClient(ipText, port, userid);
        client.start();


        // 커스텀 어댑터 생성
        m_Adapter = new CustomAdapter();

        // Xml에서 추가한 ListView 연결
        m_ListView = (ListView) findViewById(R.id.listView1);

        // ListView에 어댑터 연결
        m_ListView.setAdapter(m_Adapter);

        final TextView user1_name = (TextView)findViewById(R.id.user1_name);
        user1_name.setText(nickname.getString("nickname",""));
        final TextView user2_name = (TextView)findViewById(R.id.user2_name);

        msghandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {

                switch(hdmsg.what){

                    //상대방 ID 받아왔을 때 받는 신호
                    case 5:
                        //Toast.makeText(getApplicationContext(),"상대방은~",Toast.LENGTH_SHORT).show();
                        user2_name.setText(hdmsg.obj.toString());
                        break;

                    //일반 대화
                    case 1111:
                        m_Adapter.add(hdmsg.obj.toString(), 0); //type이 0이면 상대방쪽
                        m_ListView.setSelection(m_Adapter.getCount()-1); //항상 최신 채팅이 보이도록 함
                        break;

                    case 4:
                        try {
                            client.output.writeUTF("게임화면으로");
                            client.output.writeUTF(client.userId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        client.close();
                        Intent intent = new Intent(getApplicationContext(),GameActivity.class);
                        startActivity(intent);
                        break;

                    default:
                        Toast.makeText(getApplicationContext(),"에러ㅜㅜㅜ",Toast.LENGTH_SHORT).show();
                        //showText.append("에러ㅜㅜ\n");

                }
            }
        };

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"방나가기.",Toast.LENGTH_SHORT).show();
                try {
                    client.output.writeUTF("방나가기");
                    client.output.writeUTF(client.userId);
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                startActivity(intent);
            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    client.output.writeUTF("게임준비완료");
                    client.output.writeUTF(client.userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메세지 전송하기
                String message = messageText.getText().toString();
                messageText.setText("");
                m_Adapter.add(message, 1); //type이 1이면 자신쪽
                m_ListView.setSelection(m_Adapter.getCount()-1);
                try {
                    client.output.writeUTF("채팅보내기");
                    client.output.writeUTF(client.userId);
                    client.output.writeUTF(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    class SocketClient extends Thread {
        //boolean threadAlive;
        String ip;
        int port;
        //String mac;
        String userId;

        boolean IsChangeGameScreen; // 게임 화면으로 넘어갔는지
        boolean IsReadytoStartGame; // (눈인식 완료후) 최종 게임 준비 됐는지
        boolean IsGameInProgress; //  클라이언트가 게임 중인지 아닌지


        int enterRoomNum; //유저가 들어간 방번호

        //InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader br = null;

        private DataOutputStream output = null;

        public SocketClient(String ip, int port, String userid) {
            //threadAlive = true;
            this.ip = ip;
            this.port = port;
            this.userId = userid;

            IsChangeGameScreen = false;
            IsReadytoStartGame = false;
            IsGameInProgress = false;
        }

        @Override
        public void run() {

            try {
                // 연결후 바로 ReceiveThread 시작
                socket = new Socket(ip, port);
                //inputStream = socket.getInputStream();
                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket);
                receive.start();
                output.writeUTF("방들어왔음");
                output.writeUTF(userId);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread extends Thread {
        private Socket socket = null;
        DataInputStream input;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
            try{
                input = new DataInputStream(socket.getInputStream());
            }catch(Exception e){
            }
        }
        // 메세지 수신후 Handler로 전달
        public void run() {
            try {
                while (input != null) {

                    String msg = input.readUTF();

                    if(msg.substring(0,2).equals("99")){
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 5;
                        hdmsg.obj = msg.substring(2, msg.length()); //앞에 99는 빼고 보내기위해
                        msghandler.sendMessage(hdmsg);
                    }

                    else if(msg.equals("98")){
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 4;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                     }

                    else if (msg != null) {
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 1111;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                    }

                    /*

                    //게임시작 누르고 서버에서 처리되고 '화면 넘어감' 신호오면
                    if(msg.equals("77")){
                        //지금은 화면에 글씨 띄워보려고 이러케하지만 실제로는 알람을 띄우거나 로딩화면을 띄우면 될듯
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 77;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                    }

                    //게임에서 이겼음
                    else if(msg.equals("66")){
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 66;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                    }

                    //게임에서 졌음
                    else if(msg.equals("666")){
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 666;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                    }

                    else if (msg != null) {
                        Log.d(ACTIVITY_SERVICE, "test");

                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 1111;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                        Log.d(ACTIVITY_SERVICE,hdmsg.obj.toString());
                    }
                    */

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}