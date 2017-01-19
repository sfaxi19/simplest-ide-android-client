package com.sfaxi19.simplestidev2;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sfaxi19 on 26.11.16.
 */
public class MyTextWatcher implements TextWatcher {

    private static final String LOGTAG = "TextWatcherLog";
    private static final String LOGTAG2 = "parsingLog";
    private EditText editText;
    private HashMap<String, ArrayList<Integer>> syntax;
    private final static int COLOR_CODE_ORANGE = Color.rgb(239, 170, 78);
    private final static int COLOR_CODE_COMMENT = Color.rgb(110, 110, 110);


    public MyTextWatcher(EditText editText) {
        this.editText = editText;
        syntax = new HashMap<>();
        syntax.put("import", new ArrayList<Integer>());
        syntax.put("class", new ArrayList<Integer>());
        syntax.put("public", new ArrayList<Integer>());
        syntax.put("private", new ArrayList<Integer>());
        syntax.put("static", new ArrayList<Integer>());
        syntax.put("extends", new ArrayList<Integer>());
        syntax.put("final", new ArrayList<Integer>());
        syntax.put("return", new ArrayList<Integer>());
        syntax.put("void", new ArrayList<Integer>());
        syntax.put("int", new ArrayList<Integer>());
        syntax.put("float", new ArrayList<Integer>());
        syntax.put("char", new ArrayList<Integer>());
        syntax.put("new", new ArrayList<Integer>());
        syntax.put("this", new ArrayList<Integer>());
        syntax.put("implements", new ArrayList<Integer>());
        syntax.put("boolean", new ArrayList<Integer>());
        syntax.put("if", new ArrayList<Integer>());
        syntax.put("else", new ArrayList<Integer>());
        syntax.put("while", new ArrayList<Integer>());
        syntax.put("for", new ArrayList<Integer>());
        syntax.put("continue", new ArrayList<Integer>());
        syntax.put("true", new ArrayList<Integer>());
        syntax.put("false", new ArrayList<Integer>());
        syntax.put("try", new ArrayList<Integer>());
        syntax.put("catch", new ArrayList<Integer>());

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //System.out.println("before text changed: " + textView.getText());
        Log.d(LOGTAG, "beforeTextChanged: start:" + start + "  count:" + count + " after:" + after);

    }

    private boolean isDelimiter(char d) {
        return (d == (',') || d == (' ') || d == ('.') || d == ('\n') ||
                d == ('\t') || d == (';') || d == ('(') || d == (')'));
    }


    private void parsingText(String text, Spannable spanText) {
        int start = 0;
        int end = 0;
        int startComment = 0;
        int endComment = 0;
        boolean word = false;
        boolean comment = false;
        if (text.length() == 0) return;
        if (!isDelimiter(text.charAt(0))) {
            word = true;
        }
        for (int i = 0; i < text.length(); i++) {
            if(i!=text.length()-1){
                if(text.substring(i,i+2).equals("//")){
                    startComment = i;
                    comment = true;
                    word = false;
                }
            }else{
                if(comment&&text.charAt(i)!='\n'){
                    endComment = i + 1;
                    spanText.setSpan(new ForegroundColorSpan(COLOR_CODE_COMMENT), startComment, endComment, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                }
            }
            if((text.charAt(i)=='\n')&&(comment)){
                endComment = i;
                spanText.setSpan(new ForegroundColorSpan(COLOR_CODE_COMMENT), startComment, endComment, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                comment = false;
            }

            if(comment) continue;

            if (isDelimiter(text.charAt(i))) {
                if (word) {
                    end = i;
                    String subString = text.substring(start, end);
                    Log.d(LOGTAG, subString);
                    if (syntax.containsKey(subString)) {
                        syntax.get(subString).add(start);
                        spanText.setSpan(new ForegroundColorSpan(COLOR_CODE_ORANGE), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    word = false;
                }
            } else {
                if (!word) {
                    start = i;
                    word = true;
                }
            }
        }
        Log.d(LOGTAG, "Parsing text finished");
    }

    private void parsingSyntax(Editable text) {
        StringBuffer sb = new StringBuffer();
        Spannable spanText = new SpannableString(text.toString());
        parsingText(text.toString(), spanText);
        editText.setText(spanText);
    }

    int count = 0;
    int before = 0;
    int start = 0;

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(LOGTAG, "onTextChanged: start:" + start + " before:" + before + "  count:" + count + "  tatal_l:" + total_l);
        this.count = count;
        this.before = before;
        this.start = start;
    }

    int total_l = 0;
    int location = 0;
    boolean isChanges = false;

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(LOGTAG, "afterTextChanged.");
        if ((count != before)&&(!isChanges)) {
            Log.d(LOGTAG2,
                    "------------------\n" +
                    "Count = " + count + "\n" +
                    "Before = " + before + "\n" +
                    "Start = " + start + "\n" +
                    "------------------");
            location = editText.getSelectionStart();
            Log.d(LOGTAG2, "selection location: " + location);
            if ((count == 1) && (before == 0) &&
                    ((editText.getText().charAt(start) == '\n') || (editText.getText().charAt(start) == '}'))) {
                String changes = parsingParagraph(editText.getText().toString());
                total_l = changes.length();
                Log.d(LOGTAG2, "pre setText:" + location);
                isChanges=true;
                editText.setText(changes);
                Log.d(LOGTAG2, "post setText:" + location);

            }
            Log.d(LOGTAG2, "pre syntax location:" + location);
            parsingSyntax(editText.getText());
            Log.d(LOGTAG2, "set location:" + location);
            editText.setSelection(location);
            isChanges=false;
        }
    }

    private String parsingParagraph(String text) {
        Log.d(LOGTAG2, "parsingParagraph...");
        StringBuffer sb = new StringBuffer();
        int paragCounter = 0;
        if (editText.getText().charAt(start) == '\n') {
            for (int i = 0; i < text.length(); i++) {
                sb.append(text.charAt(i));
                if (i < start) {
                    if (text.charAt(i) == '{') {
                        paragCounter++;
                    }
                    if (text.charAt(i) == '}') {
                        paragCounter--;
                    }
                } else if (paragCounter > 0) {
                    Log.d(LOGTAG2, "paragCounter = " + paragCounter);
                    for (int j = 0; j < paragCounter; j++) {
                        sb.append("        ");
                        location += 8;
                    }
                    paragCounter = -1;
                }
            }
        } else {
            int pos = start - 1;
            int spaceCounter = 0;
            while ((text.charAt(pos) == ' ') && (pos >= 0)) {
                spaceCounter++;
                Log.d(LOGTAG2, "space_counter = " + spaceCounter);
                pos--;
            }
            if ((spaceCounter >= 8) && (spaceCounter % 8 == 0)) {
                location -= 7;
                char[] buffer = new char[start - 7];
                text.getChars(0, start - 7, buffer, 0);
                char[] buffer2 = new char[text.length() - start];
                text.getChars(start, text.length(), buffer2, 0);
                sb.append(String.valueOf(buffer) + String.valueOf(buffer2));
                Log.d(LOGTAG2, sb.toString() + "\nlength = " + sb.length() + "\nlocation = " + location);
            }else{
                return text;
            }
        }
        return sb.toString();
    }

}