package com.example.cheonyeji.noonsaoomgame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    Handler msghandler;
    SocketClient client;
    ReceiveThread receive;

    Socket socket;

    SharedPreferences nickname;
    ImageButton Button_item1, Button_item2, Button_item3, Button_send, Button_exit;
    ImageView image_item2;

    private WindowManager.LayoutParams params;
    private float brightness; // 화면 밝기값
    private Timer mTimer1;
    private int time; // 타이머로 잴 시간

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        params = getWindow().getAttributes();

        mTimer1 = new Timer();
        mTimer1.schedule(checktime,0,1000);

        Button_item1 = this.findViewById(R.id.Button_item1);
        Button_item2 = this.findViewById(R.id.Button_item2);
        Button_item3 = this.findViewById(R.id.Button_item3);
        Button_send = this.findViewById(R.id.Button_send);
        Button_exit = this.findViewById(R.id.Button_exit);
        image_item2 = this.findViewById(R.id.image_item2);

        nickname = getSharedPreferences("nickname", MODE_PRIVATE);
        final String userid = nickname.getString("nickname","");

        int port = 7070;
        String ipText = "35.229.61.173";
        client = new SocketClient(ipText, port, userid);
        client.start();

        /*
        // 10초 뒤 패배하는 코드
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                builder.setTitle("경기결과");
                builder.setMessage("아쉽게도 패배했습니다");
                builder.setPositiveButton("대기화면으로 돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        }, 10000 ); */

        final TextView player1name = (TextView)findViewById(R.id.player1name);
        player1name.setText(nickname.getString("nickname",""));

        final TextView player2name = (TextView)findViewById(R.id.player2name);


        image_item2.setVisibility(View.GONE);

        msghandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {

                switch(hdmsg.what){

                    //상대방 ID 받아왔을 때 받는 신호
                    case 5:
                        //Toast.makeText(getApplicationContext(),"상대방은~",Toast.LENGTH_SHORT).show();
                        player2name.setText(hdmsg.obj.toString());
                        break;

                    //게임화면 넘어갈때 받는 신호
                    case 77:
                        Toast.makeText(getApplicationContext(),"게임화면으로 넘어갑니다.",Toast.LENGTH_SHORT).show();
                        //showText.append("게임화면으로 넘어갑니다\n");
                        client.IsChangeGameScreen = true;
                        client.IsGameInProgress = true; // 아마 나중에 최종 준비 하는데로 옮겨야 할 것?!
                        break;


                    //이겼을 때 받는 신호
                    case 66:
                        Toast.makeText(getApplicationContext(),"이겼습니다.",Toast.LENGTH_SHORT).show();
                        //showText.append("이겼습니다\n");
                        client.IsChangeGameScreen = false;
                        client.IsGameInProgress = false;
                        break;


                    //졌을 때 받는 신호
                    case 666:
                        Toast.makeText(getApplicationContext(),"졌습니다.",Toast.LENGTH_SHORT).show();
                        //showText.append("졌습니다\n");
                        client.IsChangeGameScreen = false;
                        client.IsGameInProgress = false;
                        break;

                    //일반 대화
                    case 1111:
                        Toast.makeText(getApplicationContext(),hdmsg.obj.toString(),Toast.LENGTH_SHORT).show();
                        //showText.append(hdmsg.obj.toString() + "\n");
                        break;

                    default:
                        Toast.makeText(getApplicationContext(),"에러ㅜㅜㅜ",Toast.LENGTH_SHORT).show();
                        //showText.append("에러ㅜㅜ\n");

                }
            }
        };


        Button_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time >= 6)
                {
                    Toast.makeText(getApplicationContext(),"1번 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show();
                    // 화면 밝기 밝게!
                    brightness = params.screenBrightness;
                    params.screenBrightness = 1f;
                    getWindow().setAttributes(params);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 화면 밝기 어둡게
                            params.screenBrightness = brightness;
                            getWindow().setAttributes(params);
                        }
                    }, 2000 );
                }

            }
        });


        Button_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time >= 2){
                    Toast.makeText(getApplicationContext(), "2번 아이템이 클릭되었습니다.", Toast.LENGTH_SHORT).show();
                    image_item2.setVisibility(View.VISIBLE);
                    time -= 2;
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            image_item2.setVisibility(View.GONE);
                        }
                    }, 2000 );
                }

            }
        });


        Button_item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time >= 8)
                {
                    Toast.makeText(getApplicationContext(),"3번 아이템이 클릭되었습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GameActivity.this);
                builder.setMessage("게임을 그만할까요?");
                builder.setTitle("Exit Message")
                        .setCancelable(false)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                Toast.makeText(getApplicationContext(),"좋아요. 다시 한번 눈을 부릅!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    client.output.writeUTF("로비화면으로");
                                    client.output.writeUTF(client.userId);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                client.close();
                                Toast.makeText(getApplicationContext(),"로비로 돌아갑니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),LobbyActivity.class);
                                startActivity(intent);
                            }
                        });
                android.support.v7.app.AlertDialog alert = builder.create();
                alert.setTitle("Oops");
                alert.show();
            }
        });


    }



    /*
    // 화면 밝기 값 수정
    protected  void onResume() {
        super.onResume();
        brightness = params.screenBrightness;
        params.screenBrightness = 1f;
        getWindow().setAttributes(params);
    }

    // 기존 밝기로 변경
    protected  void onPause() {
        super.onPause();
        params.screenBrightness = brightness;
        getWindow().setAttributes(params);
    }
    */
    // 타이머 코드
    TimerTask checktime = new TimerTask() {
        @Override
        public void run() {
            time++;
        }
    };

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
                output.writeUTF("게임화면왔음");
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

                    //else if(){
                    //
                    // }

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



