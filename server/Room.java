package com.example.sungjelee.tcpserver1;

import java.util.ArrayList;

public class Room {

    ArrayList<User> userlist = new ArrayList<User>();
    ArrayList<String> userIDlist = new ArrayList<String>();
    String room_number;
    boolean gameInProgress; //게임 진행 중
    boolean gameIsReady; //눈 인식까지 되고 최종 게임 시작 되었는지
    boolean IsChangeGameScreen; //게임 화면으로 넘어갔는지
    //boolean IsGameOver; //게임 종료

    public Room(int num) {
        room_number = String.valueOf(num);
        gameInProgress = false;
        gameIsReady = false;
        IsChangeGameScreen = false;
        //IsGameOver = true;
    }

    void inputUser(User user) {
        userlist.add(user); //유저를 넣음
        userIDlist.add(user.user_id); //유저 id들만 따로 받음
        user.roomNum = Integer.parseInt(room_number);
    }

    void deleteUser(User user){
        userlist.remove(user);
        userIDlist.remove(user.user_id);
        user.roomNum = 0;
    }

    int getUserNumber(){
        return userlist.size();
    }

    ArrayList<User> getUserList(){
        return userlist;
    }

    ArrayList<String> getUserIDList(){
        return userIDlist;
    }

    void ReadytoChangeGameScreen(){
        userlist.get(0).IsChangeGameScreen = true;
        userlist.get(1).IsChangeGameScreen = true;

        //요건 최종 게임 시작되면 세팅되는 걸로 나중에 옮겨야 될 것
        gameInProgress = true;
        gameIsReady = true;
        //IsGameOver = false;
    }

    void WinLose(String Uid){
        if(userlist.get(0).user_id == Uid){
            userlist.get(0).didLose = true;
            userlist.get(0).Lose += 1;
            userlist.get(1).didWin = true;
            userlist.get(1).Win += 1;
        }

        else{
            userlist.get(1).didLose = true;
            userlist.get(1).Lose += 1;
            userlist.get(0).didWin = true;
            userlist.get(0).Win += 1;
        }

    }
}
