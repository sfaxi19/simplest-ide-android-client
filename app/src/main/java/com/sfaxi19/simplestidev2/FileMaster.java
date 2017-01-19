package com.sfaxi19.simplestidev2;

import java.io.*;
import java.util.HashMap;

/**
 * Created by sfaxi19 on 12.10.16.
 */
public class FileMaster {

    public static String loadTextFromFile(String filepath) throws IOException {
        File f = new File(filepath);
        BufferedReader fin = new BufferedReader(new FileReader(f));
        StringBuffer text = new StringBuffer();
        String line;
        while ((line = fin.readLine()) != null) {
            text.append(line);
        }
        fin.close();
        return text.toString();
    }

    public static String loadTextFromFile(File f) throws IOException {
        BufferedReader fin = new BufferedReader(new FileReader(f));
        StringBuffer text = new StringBuffer();
        String line;
        while ((line = fin.readLine()) != null) {
            text.append(line).append("\n");
        }
        fin.close();
        return text.toString();
    }



    public static void saveTextToFile(String filepath, String filename, String data) throws IOException {
            File file = new File(filepath, filename);
            try {
                DataOutputStream oos = null;
                FileOutputStream outFile = new FileOutputStream(file);
                oos = new DataOutputStream(outFile);
                oos.writeUTF(data);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
