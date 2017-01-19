package com.sfaxi19.simplestidev2;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by sfaxi19 on 19.01.17.
 */
public class XMLInputStreamParser {

    private RequestData requestData = new RequestData();
    private MainActivity mainActivity;
    private static String LOGTAG = "streamParserLog";

    public XMLInputStreamParser(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public RequestData parsingInputStream(BufferedReader in) throws IOException {
        while (!Thread.interrupted()) {
            if (in.ready()) {
                String tmp = in.readLine();
                Log.d(LOGTAG, tmp);
                if (tmp.equals("<compile-messages>")) {
                    parsingCompileMessages(in);
                    continue;
                }
                if (tmp.equals("<code-run>")) {
                    parsingCodeRun(in);
                    continue;
                }
                if (tmp.equals("<get-jar>")) {
                    parsingGetJar(in);
                    continue;
                }
                if (tmp.equals("</packet>")) {
                    break;
                }
            }
        }
        return requestData;
    }

    private String parsingCompileMessages(BufferedReader in) throws IOException {
        StringBuffer compileBuffer = new StringBuffer();
        while (!Thread.interrupted()) {
            if (in.ready()) {
                String tmp = in.readLine();
                Log.d(LOGTAG, tmp);
                if (tmp.equals("</compile-messages>")) {
                    return compileBuffer.toString();
                }
                writeToConsole(tmp + "\n");
                compileBuffer.append(tmp).append("\n");
            }
        }
        return null;
    }

    private String parsingCodeRun(BufferedReader in) throws IOException {
        StringBuffer runBuffer = new StringBuffer();
        while (!Thread.interrupted()) {
            if (in.ready()) {
                String tmp = in.readLine();
                Log.d(LOGTAG, tmp);
                if (tmp.equals("</code-run>")) {
                    return runBuffer.toString();
                }
                writeToConsole(tmp + "\n");
                runBuffer.append(tmp).append("\n");
            }
        }
        return null;
    }

    private void parsingGetJar(BufferedReader in) throws IOException {
        String filename = "data1";
        while (!Thread.interrupted()) {
            if (in.ready()) {
                String tmp = in.readLine();
                if (tmp.equals("<file-name>")) {
                    filename = in.readLine();
                    continue;
                }
                if (tmp.equals("<file>")) {
                    File sdPath = mainActivity.getFilesDir();
                    sdPath = new File(sdPath.getAbsolutePath() + "/simplest_ide/jar/");
                    sdPath.mkdirs();
                    File sdFile = new File(sdPath, filename);
                    BufferedWriter out = new BufferedWriter(new FileWriter(sdFile));
                    while (!Thread.interrupted()) {
                        tmp = in.readLine();
                        if(tmp.equals("</file>")) {
                            out.close();
                            Log.d(LOGTAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
                            return;
                        }
                        out.write(tmp);
                        out.flush();
                    }

                }
            }

        }

    }

    private void writeToConsole(String receiveData) {
        Message msg = new Message();
        msg.obj = receiveData;
        mainActivity.writeDebugData.sendMessage(msg);
    }
}
