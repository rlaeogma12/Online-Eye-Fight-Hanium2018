package com.example.sungjelee.tcpserver1;

public class User {

    String user_id;
    int Win;
    int Lose;
    int roomNum;
    boolean IsChangeGameScreen; //게임 화면으로 넘어갔는지?
    boolean IsReadytoStartGame; //마지막 최종 게임 시작 준비가 되었는지

    boolean didWin; //방금 게임 이겼나?
    boolean didLose;//방금 게임 졌나?

    public User(String user, int win, int lose) {
        user_id = user;
        Win = win;
        Lose = lose;
        roomNum = 0;
        IsChangeGameScreen = false;
        IsReadytoStartGame = false;
        didWin = false;
        didLose = false;
    }



}


