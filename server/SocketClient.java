package com.example.sungjelee.tcpserver1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SocketClient {

    HashMap<String, DataOutputStream> clients;

    ArrayList<User> userList = new ArrayList<User>();
    Map<String, User> userMap;

    ArrayList<Room> roomList = new ArrayList<Room>();
    Map<String, Room> roomMap;

    private ServerSocket ServerSocket = null;
    int [] RoomArr = {0, 0, 1, 2, 2, 0};

    int assignRoomNum = 1;
    boolean useAssignNum = true;
    //boolean connect = false;

    public static void main(String[] args) {

        new SocketClient().start();

    }

    public SocketClient() {

        // 연결부 hashmap 생성자(Key, value) 선언
        clients = new HashMap<String, DataOutputStream>();
        userMap = new HashMap<String, User>();
        roomMap = new HashMap<String, Room>();

        // clients 동기화
        Collections.synchronizedMap(clients);
        Collections.synchronizedMap(userMap);
        Collections.synchronizedList(userList);
        Collections.synchronizedMap(roomMap);
        Collections.synchronizedList(roomList);

    }

    private void start() {

        // Port 값은 편의를위해 5001로 고정 (Random값으로 변경가능)
        int port = 7070;
        Socket socket = null;

        try {
            // 서버소켓 생성후 while문으로 진입하여 accept(대기)하고 접속시 ip주소를 획득하고 출력한뒤
            // MultiThread를 생성한다.
            ServerSocket = new ServerSocket(port);
            System.out.println("접속대기중");
            while (true) {
                socket = ServerSocket.accept();
                InetAddress ip = socket.getInetAddress();
                System.out.println(ip + "  connected");
                new MultiThread(socket).start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    class MultiThread extends Thread {

        Socket socket = null;

        String userid = null;
        //String msg = null;
        int win = 0;
        int lose = 0;

        String enterRoute = null;

        String str;
        String Uid;
        String mgtemp;
        String Rnumber;
        User Utmp;
        Room Rtmp;

        String roomNum_S;
        int roomNum;

        boolean enterServer = false;

        DataInputStream input;
        DataOutputStream output;



        public MultiThread(Socket socket) {
            this.socket = socket;
            try {
                // 객체를 주고받을 Stream생성자를 선언한다.
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
            }
        }



        public void run() {

            User newUser;

            while (input != null) {
                try {
                    str = input.readUTF();

                    switch(str){

                        case "서버접속시도" :
                            enterRoute = input.readUTF();

                            //로그아웃 안해서 로그인 없이 바로 접속되는 경우
                            if(enterRoute.equals("접속1")){
                                userid = input.readUTF();
                                output.writeUTF("접속1성공");
                                Utmp = userMap.get(userid);
                                win = Utmp.Win;
                                lose = Utmp.Lose;
                            }

                            //처음 로그인할때
                            else if(enterRoute.equals("접속2")){
                                userid = input.readUTF();

                                if(userMap.containsKey(userid)){
                                    output.writeUTF("Duplicate");
                                    System.out.println("ID가 겹칩니다");
                                    break;
                                }
                                else{
                                    output.writeUTF("접속2성공");
                                    win = input.readInt();
                                    lose = input.readInt();
                                    newUser = new User(userid, win, lose);
                                    userList.add(newUser);
                                    userMap.put(userid, newUser);
                                    clients.put(userid, output);
                                }
                            }
                            System.out.println("ID : " + userid + ", Win : " + win + ", Lose : " + lose);

                            System.out.println(userid + "  서버 접속"); //""클라이언트에게 접속 사실 알려야 함""

                            //유저 접속하면 현재 유저 상황 출력
                            System.out.println();
                            System.out.println("[현재 유저 상황]");
                            for(Object object : userList) {
                                User r = (User)object;
                                System.out.println(r.user_id + "(승:" + r.Win + "/패:"+ r.Lose + ", 방:" + r.roomNum + ")");
                            }
                            System.out.println();

                            break;

                        //방 들어가기 버튼 눌렀을때
                        case "11":
                            Uid = input.readUTF();
                            Rnumber = input.readUTF();
                            if(roomMap.containsKey(Rnumber)){
                                Rtmp = roomMap.get(Rnumber);
                                if(Rtmp.getUserNumber() < 2){
                                    Utmp = userMap.get(Uid);
                                    Rtmp.inputUser(Utmp);
                                    System.out.println(Uid + " " + Rnumber + "번 방 접속");
                                    sendMsg(String.valueOf(Utmp.roomNum), "99"+ Utmp.user_id + "(승:" + Utmp.Win + ", 패:" + Utmp.Lose + ")님이 방에 입장하셨습니다.");

                                }
                                else {
                                    sendMsgtoMe(Uid, "97"); //방이 꽉찼음 -> 클라이언트에게 보냄
                                    System.out.println(Rnumber + "번 방은 꽉 찼습니다");
                                }
                            }
                            else{
                                sendMsgtoMe(Uid, "98"); //방이 아직없음 -> 클라이언트에게 보냄
                                System.out.println("아직 " + Rnumber + "번 방은 없습니다");
                            }
                            break;

                        //채팅 전송 버튼 눌렀을 때
                        case "22":
                            Uid = input.readUTF();
                            mgtemp = input.readUTF();
                            Utmp = userMap.get(Uid);
                            Rnumber = String.valueOf(Utmp.roomNum);
                            sendMsg(Rnumber, mgtemp);
                            System.out.println(mgtemp);
                            break;

                        // 방만들기 버튼 눌렀을 때
                        case "33":
                            Uid = input.readUTF();
                            useAssignNum = false;
                            Room newRoom = new Room(assignRoomNum++); //assignRoomNum 동기화 해줘야하나??
                            Utmp = userMap.get(Uid);
                            newRoom.inputUser(Utmp);
                            roomList.add(newRoom);
                            roomMap.put(String.valueOf(Utmp.roomNum), newRoom);
                            System.out.println(Uid + "님이 " + Utmp.roomNum + "번 방 만들었습니다");

                            //현재 방 상황 출력하기
                            System.out.println();
                            System.out.println("[현재 방 상황]");
                            for(Object object : roomList) {
                                Room r = (Room)object;
                                System.out.println(r.room_number + "번 방(유저 "+ r.getUserNumber() + "명)");
                            }
                            System.out.println();

                            sendMsg(String.valueOf(Utmp.roomNum), "99"+ Utmp.user_id + "(승:" + Utmp.Win + ", 패:" + Utmp.Lose + ")님이 방에 입장하셨습니다.");

                            break;

                        //방 나가기 버튼 눌렀을 때
                        case "44":
                            Uid = input.readUTF();
                            Utmp = userMap.get(Uid);
                            exitRoom(Uid, Utmp); //방나가는 class
                            sendMsgtoMe(Uid, "96"); //방 나감 처리 완료 클라이언트에게 알려주기

                            //현재 방 상황 출력하기
                            System.out.println();
                            System.out.println("[현재 방 상황]");
                            for(Object object : roomList) {
                                Room r = (Room)object;
                                System.out.println(r.room_number + "번 방(유저 "+ r.getUserNumber() + "명)");
                            }
                            System.out.println();
                            break;

                        //게임시작 버튼 눌렀을 때
                        case "77":
                            Uid = input.readUTF();
                            Utmp = userMap.get(Uid);
                            Rnumber = String.valueOf(Utmp.roomNum);
                            Rtmp = roomMap.get(Rnumber);
                            if(Rtmp.getUserNumber() == 2){
                                //&& !Utmp.IsChangeGameScreen && !Rtmp.IsChangeGameScreen){ //일단 방에 2명이 안되면 게임 진행 불가 && 나말고 다른 플레이어가 게임 시작을 눌렀을 수도 있음
                                if(Utmp.IsChangeGameScreen==false && Rtmp.IsChangeGameScreen == false){ //Rtmp.IsChangeGameScreen 동기화 어떻게??
                                    Rtmp.IsChangeGameScreen = true; //게임 화면으로 넘어갔다는 뜻
                                    Rtmp.ReadytoChangeGameScreen();
                                    sendMsg(Rnumber, "77");
                                }
                            }
                            else{
                                sendMsgtoMe(Uid, "아직 방에 2명이 안찼습니다.");
                            }

                            break;

                        //(잠깐 만든거임) 눈감음 버튼 눌렀을 때
                        case "78":
                            if(!Rtmp.gameInProgress){
                                Uid = input.readUTF();
                                break;
                            }
                            Rtmp.gameInProgress = false;
                            Uid = input.readUTF();
                            Rtmp.WinLose(Uid);
                            Rtmp.gameIsReady = false;
                            sendMsgWinLose(Rnumber, "66");

                            break;


                        //종료버튼 눌렀을 때 유저리스트에서 유저정보 삭제?
                        case "55":
                            Uid = input.readUTF();
                            Utmp = userMap.get(Uid);

                            //만약에 들어가있는 방이 있었다면 방에서 나간 처리해줌
                            if(Utmp.roomNum != 0){
                                exitRoom(Uid, Utmp);
                            }


                            userMap.remove(Uid); //서버가 가지고 있는 유저 맵에서 해당 유저 제거해버리기
                            userList.remove(Utmp); //서버가 가지고 있는 유저 리스트에서 해당 유저 제거해버리기
                            sendMsgtoMe(Uid, "88"); //유저 삭제 처리 완료

                            //유저 종료하면 현재 유저 상황 출력
                            System.out.println();
                            System.out.println("[현재 유저 상황]");
                            for(Object object : userList) {
                                User r = (User)object;
                                System.out.println(r.user_id + "(승:" + r.Win + "/패:"+ r.Lose + ", 방:" + r.roomNum + ")");
                            }
                            System.out.println();
                            break;

                        default :
                            System.out.println("에러1");
                    }
                } catch (IOException e) {

                    System.out.println("에러2"); //나중에 이런건 안나오게 지우기!
                    break;
                }
            }


            //""클라이언트가 방에 들어오려고 하면
               /* while (input != null) {
                    try {
                        roomNum_S = input.readUTF();
                        roomNum = Integer.parseInt(roomNum_S);
                        if(RoomArr[roomNum] < 2){
                            RoomArr[roomNum]++;
                            System.out.println(userid + "님이 " + roomNum_S + "번 방에 접속");
                            sendMsgtoMe("99"); // 방에 접속했음
                            break;
                        }
                        else{
                            sendMsgtoMe("방이 꽉 찼습니다"); // 이미 방이 꽉차서 방에 접속 못함
                        }
                    } catch (IOException e) {
                        sendMsg("Nothing");
                        break;
                    }
                }*/

            // 채팅메세지수신시
                /*while (input != null) {
                    try {
                        String temp = input.readUTF();
                        sendMsg(temp);
                        System.out.println(temp);
                    } catch (IOException e) {
                        //sendMsg("No massege");
                        break;
                    }
                }*/
        }

        // 메세지수신후 클라이언트에게 Return 할 sendMsg 메소드
        private void sendMsg (String roomnumber, String msg) {

            Room room = roomMap.get(roomnumber);

            //해당 방에 들어있는 유저 아이디들만 받음
            Iterator<String> itt = room.getUserIDList().iterator();

            // clients의 Key값을 받아서 String 배열로선언
            Iterator<String> it = clients.keySet().iterator();

            // Return 할 key값이 없을때까지
            while (itt.hasNext()) {
                try {
                    String uid = itt.next();
                    OutputStream dos = clients.get(uid);
                    // System.out.println(msg);
                    DataOutputStream output = new DataOutputStream(dos);
                    output.writeUTF(msg);

                } catch (IOException e) {
                    System.out.println("에러4");
                }
            }
        }

        // room에 들어가거나 못들어갔을 때 본인에게만 여부 알려주기위한 메소드
        private void sendMsgtoMe(String Uid, String msg) {

            // clients의 Key값을 받아서 String 배열로선언
            //Iterator<String> it = clients.keySet().iterator();
            try {
                OutputStream dos = clients.get(Uid); //메세지 보낼 유저의 key(아이디)로 값을 얻어옴
                DataOutputStream output = new DataOutputStream(dos);
                output.writeUTF(msg);
            } catch (IOException e) {
                System.out.println("에러5");
            }

            // Return 할 key값이 없을때까지
            /*while (it.hasNext()) {

                try {
                    OutputStream dos = clients.get(it.next());
                    // System.out.println(msg);
                    DataOutputStream output = new DataOutputStream(dos);
                    output.writeUTF(msg);

                } catch (IOException e) {
                    System.out.println(e);
                }
            }*/
        }

        private void exitRoom(String Uid, User Utmp) {

            String rN = String.valueOf(Utmp.roomNum); //미리 방번호 알아둠

            //들어가 있는 방이 있어야 진행
            if(Utmp.roomNum != 0){
                Rtmp = roomMap.get(rN);
                Rtmp.deleteUser(Utmp);
            }
            else{
                System.out.println("들어가 있는 방이 없습니다.");
            }

            //만약 빈방이 되어버리면
            if(Rtmp.getUserNumber()==0){
                roomMap.remove(Rtmp.room_number); //서버가 가지고 있는 방 맵에서 해당 방 제거해버리기
                roomList.remove(Rtmp); //서버가 가지고 있는 방 리스트에서 해당 방 제거해버리기
            }
            else{ //빈방이 아직 안됐으면 누가 나갔는지 알려주기
                sendMsg(rN, Utmp.user_id + "님이 나갔습니다.");
            }

        }

        // 이겼는지 졌는지 보내주기
        private void sendMsgWinLose(String roomnumber, String msg) {

            Room room = roomMap.get(roomnumber);

            //해당 방에 들어있는 유저 아이디들만 받음
            Iterator<String> itt = room.getUserIDList().iterator();

            // Return 할 key값이 없을때까지
            while (itt.hasNext()) {
                try {
                    String uid = itt.next();
                    OutputStream dos = clients.get(uid);
                    // System.out.println(msg);
                    DataOutputStream output = new DataOutputStream(dos);

                    User Utemp = userMap.get(uid);
                    if(Utemp.didWin && !Utemp.didLose){
                        output.writeUTF(msg);

                        //결과 보냈으면 사용자 상태 초기화
                        Utemp.didWin = false;
                        Utemp.IsChangeGameScreen = false;
                        Utemp.IsReadytoStartGame = false;
                    }

                    else if(!Utemp.didWin && Utemp.didLose){
                        output.writeUTF(msg+"6"); // 666보내면 졌다는 뜻

                        //결과 보냈으면 사용자 상태 초기화
                        Utemp.didLose = false;
                        Utemp.IsChangeGameScreen = false;
                        Utemp.IsReadytoStartGame = false;
                    }

                } catch (IOException e) {
                    System.out.println("에러4");
                }
            }

            //해당 방의 정보도 초기화
            room.IsChangeGameScreen = false;
        }


    }
}
