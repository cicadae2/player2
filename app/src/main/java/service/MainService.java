package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import activity.MainActivity;
import bean.SongBean;

public class MainService extends Service {
    public static final String pause = "pause";
    public static final String play = "play";
    public static final String click = "click";
    public static final String back = "back";
    public static final String forward = "forward";
    public static final int playMode_single = 1;//单曲循环
    public static final int playMode_loop = 2;//顺序循环
    public static final int playMode_shuffle = 3;//随机循环
    public static final String mode = "mode";//播放循环选择
    public static final String onStopTrackingTouch = "onStopTrackingTouch";
    public static final String delete = "delete";

    private int playmode = playMode_loop;
    private MyBroadcastReceiver receiver;
    MediaPlayer mMediaPlayer;
    boolean playing = false;
    public Timer timer;
    public TimerTask timertask;
    int progress = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter());
        IntentFilter filter = new IntentFilter();
        filter.addAction(pause);
        filter.addAction(play);
        filter.addAction(click);
        filter.addAction(mode);
        filter.addAction(back);
        filter.addAction(forward);
        filter.addAction(delete);
        filter.addAction(onStopTrackingTouch);
        registerReceiver(receiver, filter);
        sendBroadcast(new Intent(MainActivity.serviceOnCreated));
        timer = new Timer();
        timertask = new TimerTask() {
            @Override
            public void run() {
                if (playing) {
                    progress++;
                    sendBroadcast(new Intent(MainActivity.progress).putExtra("progress", progress));

                }
            }
        };
        timer.schedule(timertask, 0, 1000);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case pause:
                    pause();
                    break;
                case play:
                    play();
                    break;
                case click:
                    SongBean songBean = (SongBean) intent.getSerializableExtra("MP3");
                    click(songBean);
                    progress = 0;
                    break;
                case mode:
                    playmode = intent.getIntExtra("mode", 0);
                    break;
                case back:
                    int pos = intent.getIntExtra("back", 0);
                    back(pos);
                    break;
                case forward:
                    int pos2 = intent.getIntExtra("forward", 0);
                    forward(pos2);
                    break;
                case onStopTrackingTouch:
                    progress = intent.getIntExtra("stop", 0);
                    if (mMediaPlayer != null) mMediaPlayer.seekTo(progress * 1000);
                    break;
                case delete:
                    int delete = intent.getIntExtra("deletePosition", 0);
                    forward(delete);
            }


        }
    }

    private void forward(int pos2) {
        sendBroadcast(new Intent(MainActivity.forward2).putExtra("forward2", pos2));
    }

    private void back(int pos) {
        sendBroadcast(new Intent(MainActivity.back2).putExtra("back2", pos));
    }

    private void click(SongBean songBean) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }


        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(songBean.mp3Path);
            mMediaPlayer.prepare();//播放前准备一下
        } catch (IOException e) {
            e.printStackTrace();
        }
        play();
        //监听：准备完成的监听
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                sendBroadcast(new Intent(MainActivity.end));

            }
        });


        sendBroadcast((new Intent(MainActivity.getDuration).putExtra("max", mMediaPlayer.getDuration() / 1000)));
//        if (playing) {
//            play();
//            Toast.makeText(this, "正在播放" + songBean.name, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "选择" + songBean.name, Toast.LENGTH_SHORT).show();
//        }
    }

    private void play() {
        playing = true;
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            sendBroadcast(new Intent(MainActivity.playCheck).putExtra("play", true));
        }
    }

    private void pause() {
        playing = false;
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            sendBroadcast(new Intent(MainActivity.playCheck).putExtra("play", false));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timertask != null) {
            timertask.cancel();
            timertask = null;
        }
        unregisterReceiver(receiver);
    }
}
