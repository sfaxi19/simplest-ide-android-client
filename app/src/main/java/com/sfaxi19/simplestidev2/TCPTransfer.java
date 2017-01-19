package com.sfaxi19.simplestidev2;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by sfaxi19 on 05.01.17.
 */

public class TCPTransfer implements Runnable {

    public final static char COMPILE = 0x01;
    public final static char RUN = 0x02;
    public final static char GET_JAR_FILE = 0x04;
    private char[] taskByte = new char[1];
    private static String PATH = new File("").getAbsolutePath();
    private static final String LOGTAG = "ClientLog";
    private MainActivity mainActivity = null;
    private XMLOutputStreamWriter writer;
    private XMLInputStreamParser parser;
    private RequestData requestData;
    private String serverip;
    private int port;

    public TCPTransfer(RequestData requestData, String serverip, int port, MainActivity mainActivity) {
        this.serverip = serverip;
        this.port = port;
        this.requestData = requestData;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try {
            mainActivity.cleanDebugData.sendEmptyMessage(0);
            mainActivity.setButtonText.sendEmptyMessage(mainActivity.STOP);
            Socket socket = new Socket(serverip, port);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            writer = new XMLOutputStreamWriter();
            writer.writeRequestData(out, requestData);
            Log.d(LOGTAG, "Address: " + socket.getLocalAddress().toString() + "\nPort: " + socket.getLocalPort());
            Log.d(LOGTAG, "Listening...");
            parser = new XMLInputStreamParser(mainActivity);
            parser.parsingInputStream(in);
            out.close();
            socket.close();
        } catch (ConnectException con_ex) {
            Message msg = new Message();
            msg.obj = con_ex.getMessage();
            mainActivity.writeDebugData.sendMessage(msg);
        } catch (IOException e) {
            Message msg = new Message();
            msg.obj = "...so bad\n";
            mainActivity.writeDebugData.sendMessage(msg);
            e.printStackTrace();
        }
        mainActivity.setButtonText.sendEmptyMessage(mainActivity.RUN);
        Log.d(LOGTAG, "...finish");
    }

}
