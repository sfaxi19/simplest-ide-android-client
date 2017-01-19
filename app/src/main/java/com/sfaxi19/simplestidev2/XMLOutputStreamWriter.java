package com.sfaxi19.simplestidev2;

import android.app.DownloadManager;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by sfaxi19 on 19.01.17.
 */
public class XMLOutputStreamWriter {

    public void writeRequestData(PrintWriter out, RequestData requestData) throws IOException {
        out.write("\n<packet>\n");
        out.write("\n<classname>\n");
        out.write(requestData.getClassname());
        out.write("\n</classname>\n");
        out.write("\n<tasks>\n");
        out.write("\n<compile>\n");
        out.write(requestData.doComplie() ? "true" : "false");
        out.write("\n</compile>\n");
        out.write("\n<run>\n");
        out.write(requestData.doRunning() ? "true" : "false");
        out.write("\n</run>\n");
        out.write("\n<get-jar>\n");
        out.write(requestData.getJarFile() ? "true" : "false");
        out.write("\n</get-jar>\n");
        out.write("\n</tasks>\n");
        out.write("\n<code>\n");
        out.write(requestData.getCode());
        out.write("\n</code>\n");
        out.write("\n</packet>\n");
        out.flush();
    }
}
