package jp.ac.jec.cm0129.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private static final int EXTERNAL_STORAGE = 1;
    Button btnPlay,btnPause,btnStop;
    private final int SEARCH_REQCD = 123;

    String path;

    TextView txtFileName;

    private SeekBar sb;
    private int mTotalTime;
    private Thread thread;
    Runnable runnable;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayer = MediaPlayer.create(this,R.raw.tw054);
        btnPlay  = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new BtnEvent());

        btnPause  = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new BtnEvent());

        btnStop  = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new BtnEvent());

        txtFileName = findViewById(R.id.txtFileName);
        sb = findViewById(R.id.seekBar);

        Button btnSDLite = findViewById(R.id.btnSDList);
        btnSDLite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPlayer != null && mPlayer.isPlaying()){

                    mPlayer.stop();
                }
                Intent i = new Intent(MainActivity.this,SDListActivity.class);
                startActivityForResult(i,SEARCH_REQCD);
            }
        });
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE
                );
            }
        }

        btnPlay.setEnabled(false);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    Log.i("Main","TESGING_SEEKBAR"+i);
                    mPlayer.seekTo(i);
                    sb.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    private Handler threadHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.i("MainActivity", "msg.what = " + msg.what);sb.setProgress(msg.what);
          }};

    public void updateSeekbar() {

        int currPos = mPlayer.getCurrentPosition();
        Log.i("getCurrentPosition","CURRENT_POSITION"+currPos);
        sb.setProgress(currPos);
        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        };
        handler.postDelayed(runnable,1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            thread = null;
        }
    }


    class BtnEvent implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnPlay){
                mPlayer = new MediaPlayer();
                try {
                    //mPlayer = MediaPlayer.create(MainActivity.this,R.raw.tw054);

                    mPlayer.setDataSource(MainActivity.this, Uri.parse(path));
                    //System.out.println("MUSIC PLAYERRRRRR"+mPlayer);
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Toast.makeText(MainActivity.this,"再生終了",Toast.LENGTH_SHORT).show();
                            mPlayer.stop();
                            setDefaultButtons();
                        }
                    });
                    mPlayer.prepare();
                    mPlayer.seekTo(0);
                    mPlayer.start();
                    setPlayingStateButtons();
                    //sb.setMax(mPlayer.getDuration());
                    mTotalTime = mPlayer.getDuration();
                    sb.setMax(mTotalTime);
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (mPlayer != null) {
                                    int currentPosition = mPlayer.getCurrentPosition();
                                    Message msg = new Message();
                                    msg.what = currentPosition;
                                    threadHandler.sendMessage(msg);
                                    Thread.sleep(100);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                } catch (Exception e){
                    Log.e("Main",e.toString());
                }

            } else if (v.getId() == R.id.btnPause){
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                } else {
                    mPlayer.start();
                }

                setPlayingStateButtons();

            } else if (v.getId() == R.id.btnStop) {
                mPlayer.stop();

                setDefaultButtons();
            }

        }
    }
    private void setDefaultButtons() {
        btnPlay.setEnabled(true);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
    }
    private void setPlayingStateButtons() {
        btnPlay.setEnabled(false);
        btnPause.setEnabled(true);
        btnStop.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDefaultButtons();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length <= 0){
            return;
        }
        switch (requestCode){
            case EXTERNAL_STORAGE:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else {
                    Toast.makeText(this,"can't run the app",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_REQCD && resultCode == RESULT_OK) {
            path = data.getStringExtra("SELECT_FILE"); //for String
            //for object is get()
//            Object d = data.getExtras().get("SELECT_FILE");



            Log.i("TESTING_DATA","ALL_DATA::::  "+data.getStringExtra("SELECT_FILE"));
            txtFileName.setText(path);
            setDefaultButtons();
            sb.setProgress(0);
            sb.setEnabled(true);
        }
    }
}