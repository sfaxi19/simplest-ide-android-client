package com.sfaxi19.simplestidev2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOGTAG = "MainLog";
    private TextView textView_console;
    private EditText editText_code;
    private Button buttonRun;
    private CheckBox checkBoxGet;
    private File PATH = null;
    private RequestData requestData;
    private String ip_address = "";
    private int port = 0;
    public static final int STOP = 0;
    public static final int RUN = 1;
    int heightDisplay;
    int widthDisplay;
    int height;
    private Thread sender1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PATH = getFilesDir();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        requestData = new RequestData();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        editText_code = (EditText) findViewById(R.id.editText_code);
        if (editText_code != null) {
            editText_code.addTextChangedListener(new MyTextWatcher(editText_code));
        }
        checkBoxGet = (CheckBox) findViewById(R.id.checkBoxGet);
        textView_console = (TextView) findViewById(R.id.textView_console);
        textView_console.setKeyListener(null);
        buttonRun = (Button) findViewById(R.id.buttonRun);
        buttonRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonRun.getText().equals("run")) {
                    if (sender1 != null) sender1.interrupt();
                    requestData.setCode(editText_code.getText().toString());
                    requestData.setGetJar(checkBoxGet.isChecked());
                    requestData.setCompile(true);
                    requestData.setRun(true);
                    if(ip_address.equals("")) getStringData(MainActivity.this, 1);
                    if(port==0) getStringData(MainActivity.this, 2);
                    if (requestData.getClassname().equals("")) {
                        getStringData(MainActivity.this, 0);
                    } else {
                        sender1 = new Thread(new TCPTransfer(requestData, ip_address, port, MainActivity.this));
                        sender1.start();
                    }
                } else {
                    sender1.interrupt();
                }
            }
        });
        final Button buttonLoad = (Button) findViewById(R.id.buttonLoad);
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFileDialog dialog = new OpenFileDialog(MainActivity.this).setFileListener(new OpenFileDialog.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) {
                        try {
                            editText_code.setText(FileMaster.loadTextFromFile(file));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.showDialog();
            }
        });

        final Button buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestData.getClassname().equals("")) {
                    getStringData(MainActivity.this, 0);
                } else {
                    writeFileSD(
                            (requestData != null) ? (requestData.getClassname() + ".java") : "class1.java",
                            editText_code.getText().toString());
                }
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        widthDisplay = display.getWidth();
        heightDisplay = display.getHeight();
        Log.d(LOGTAG, widthDisplay + " x " + heightDisplay);
        final TextView resize = (TextView) findViewById(R.id.textView_resize);
        resize.setOnTouchListener(new View.OnTouchListener() {
            int down;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        down = (int) event.getY();
                        height = editText_code.getHeight();
                        break;
                    case MotionEvent.ACTION_MOVE: // движение
                        int y = (int) event.getY();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                editText_code.getLayoutParams().height + (y - down));
                        params.setMargins(10, 0, 10, 0);
                        editText_code.setLayoutParams(params);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rename) {
            getStringData(this, 0);
            return true;
        }
        if (id == R.id.action_ip) {
            getStringData(this, 1);
            return true;
        }
        if (id == R.id.action_port) {
            getStringData(this, 2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Handler setButtonText = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RUN) {
                setButtonStart();
            } else {
                setButtonStop();
            }
        }
    };

    private void setButtonStart() {
        buttonRun.setText("run");
        buttonRun.setTextColor(this.getResources().getColor(R.color.grean_button));
    }

    private void setButtonStop() {
        buttonRun.setText("stop");
        buttonRun.setTextColor(this.getResources().getColor(R.color.red_button));
    }

    public Handler writeDebugData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = (String) msg.obj;
            textView_console.setText(textView_console.getText().toString() + data);
        }
    };

    public Handler cleanDebugData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            textView_console.setText("");
        }
    };

    void writeFileSD(String name, String data) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOGTAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = getFilesDir();//Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/simplest_ide/");
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, name);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            bw.write(data);
            bw.close();
            Log.d(LOGTAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getStringData(MainActivity mainActivity, int mod) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mainActivity);
        if(mod==0) alert.setTitle("Введите имя файла");
        if(mod==1) alert.setTitle("Введите ip сервера");
        if(mod==2) alert.setTitle("Введите port");
        alert.setMessage("Ввод:");
        final int mode = mod;
        // Set an EditText view to get user input
        final EditText input = new EditText(mainActivity);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                switch (mode) {
                    case 0:
                        requestData.setClassname(value);
                        break;
                    case 1:
                        ip_address = value;
                        break;
                    case 2:
                        port = Integer.decode(value);
                        break;

                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }
}
