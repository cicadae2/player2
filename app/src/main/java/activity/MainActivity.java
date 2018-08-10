package activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.clock.player2.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import Adapter.MainAdapter;
import bean.SongBean;
import bean.UserBean;
import service.HjyApp;
import service.MainService;
import util.FastBlurUtil;
import util.FileUtil;
import util.GetScreen;

public class MainActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static int CAMERA_REQUEST_COOE = 1;
    private static int GALLERY_REQUEST_COOE = 2;
    private TextView timeCurrent;
    private TextView timeAll;
    private SeekBar bar;
    private ImageView back;
    private ImageView forward;
    private ImageView mode;
    private ImageView pause;
    private ImageView menu;
    private TextView name;
    private TextView singer;
    private LinearLayout main_layout;
    private MainAdapter adapter;
    private List<SongBean> songBeanList;
    private ImageView dandan;
    private TextView sync;
    private ImageView information;
    private ImageView head;
    private Intent serviceIt;
    private UserBean userBean;
    public static final String serviceOnCreated = "serviceOnCreated";
    public int playposition = 0;
    public int clickPos = -1;
    public ImageView second_bg;
    boolean playing = false;
    public static final String back2 = "back2";
    public static final String forward2 = "forward2";
    public static final String end = "end";
    public static final String getDuration = "getDuration";
    public static final String progress = "progress";
    public static final String playCheck = "playCheck";
    private int playMode = MainService.playMode_loop; //播放模式
    private boolean sex;
    int time;
    long starttime = 0;
    long pausetime = 0;
    private TextView player;
    PopupWindow popupWindow;
    Animation animation;
    private DrawerLayout drawerLayout;
    //侧滑菜单栏
    private NavigationView navigationView;
    //沉浸式状态栏
    private SystemBarTintManager tintManager;
    private LinearLayout llHead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        userBean = HjyApp.get().getUb();//获取Actvity之间的传值
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav);
        View headerView = navigationView.getHeaderView(0);//View 第0个头布局
        llHead = headerView.findViewById(R.id.llHead);
        head = headerView.findViewById(R.id.person);
//        开启手势滑动打开侧滑菜单栏，如果要关闭手势滑动，将后面的UNLOCKED替换成LOCKED_CLOSED 即可
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_account:
                        startActivity(new Intent(MainActivity.this, NameActivity.class));
                        //此处不能加Finsh杀死界面否则点击返回会退出程序而不是回到主界面
                        break;
                    case R.id.menu_name:
                        startActivity(new Intent(MainActivity.this, SexActivity.class));
                        break;
                    case R.id.menu_back:
                        finish();
                        break;

                }

                drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });
        serviceIt = new Intent(this, MainService.class);
        startService(serviceIt);//开启一个服务器
        IntentFilter filter = new IntentFilter();
        filter.addAction(end);
        filter.addAction(getDuration);
        filter.addAction(back2);
        filter.addAction(forward2);
        filter.addAction(serviceOnCreated);
        filter.addAction(progress);
        filter.addAction(playCheck);
        registerReceiver(receiver, filter);
        dandan = findViewById(R.id.imageview);
        animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
//        MediaScannerConnection.scanFile(this, new String[] { Environment
//                .getExternalStorageDirectory().getAbsolutePath() }, null, null);

        initViews();
        setUserInfo();
        createPopWindow();
        setBackground();

    }

    private void setUserInfo() {
        if (TextUtils.isEmpty(userBean.gethead())) return;
        Bitmap bmp = BitmapFactory.decodeFile(userBean.gethead());
        if (bmp == null) return;
        head.setImageBitmap(bmp);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initViews() {
        timeCurrent = findViewById(R.id.time);
        timeAll = findViewById(R.id.timeAll);
        bar = findViewById(R.id.bar);
        back = findViewById(R.id.back);
        forward = findViewById(R.id.forward);
        mode = findViewById(R.id.mode);
        pause = findViewById(R.id.play);
        menu = findViewById(R.id.menu);
        main_layout = findViewById(R.id.main_layout);
        songBeanList = HjyApp.get().getSbs();
        name = findViewById(R.id.name);
        singer = findViewById(R.id.singer);
        second_bg = findViewById(R.id.second_bg);
        sync = findViewById(R.id.sync);
        information = findViewById(R.id.information);
        player = findViewById(R.id.player);
        SongBean songBean = songBeanList.get(playposition);
        sendBroadcast(new Intent(MainService.click).putExtra("MP3", songBean));
        menu.setOnClickListener(this);
        back.setOnClickListener(this);
        forward.setOnClickListener(this);
        pause.setOnClickListener(this);
        mode.setOnClickListener(this);
        bar.setOnSeekBarChangeListener(this);
        sync.setOnClickListener(this);
        information.setOnClickListener(this);
        mode.setImageResource(R.drawable.loop);
        pause.setImageResource(R.drawable.play);
        information.setOnClickListener(this);
        head.setOnClickListener(this);
    }

    private void setBackground() {

        Resources res = getResources();
        Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.drawable.dandan_background);

        //        scaledBitmap为目标图像，10是缩放的倍数（越大模糊效果越高）
        Bitmap blurBitmap = FastBlurUtil.toBlur(scaledBitmap, 2);
        second_bg.setImageBitmap(blurBitmap);

    }

    private void createPopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_popup, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManger = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManger);
        adapter = new MainAdapter(songBeanList);
        recyclerView.setAdapter(adapter);
        int height = GetScreen.getHeight(this);
        popupWindow = new PopupWindow(main_layout, ViewGroup.LayoutParams.MATCH_PARENT, (height / 3) * 2);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());//点旁边popup消失
        adapter.setOnClickListener(new MainAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                popupWindow.dismiss();
                playposition = position;
                clickSong();
            }
        });
        adapter.setOnItemLongClickListener(new MainAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(final int position) {
                AlertDialog.Builder delete = new AlertDialog.Builder(MainActivity.this);
                delete.setTitle("删除");
                delete.setMessage("确定要删除吗？");
                delete.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                delete.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.removeItem(position);
                        sendBroadcast(new Intent(MainService.delete).putExtra("deletePosition", position));
                    }
                });
                delete.show();

            }
        });
    }

    private void showPopupWindow() {
        if (popupWindow == null) {
            createPopWindow();
        }
        popupWindow.showAtLocation(MainActivity.this.findViewById(R.id.timeAll), Gravity.BOTTOM, 0, 0);
    }


    private void clickSong() {
        if (songBeanList.isEmpty()) return;
        if (playposition == songBeanList.size()) playposition = 0;
        SongBean songBean = songBeanList.get(playposition);
        sendBroadcast(new Intent(MainService.click).putExtra("MP3", songBean));
        adapter.setClickPos(playposition);
        name.setText(songBeanList.get(playposition).name);
        singer.setText(songBeanList.get(playposition).singer);
        resetProgress();


    }

    private void resetProgress() {
        timeCurrent.setText("00:00");

    }

    File tempFile;

    /**
     * 从相机获取图片
     */
    private void getPicFromCamera() {
//       /* //用于保存调用相机拍照后所生成的文件
        tempFile = new File(FileUtil.createFolder(FileUtil.imgPath), System.currentTimeMillis() + ".jpg");
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.example.clock.player2", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, CAMERA_REQUEST_COOE);

//        Intent intent = new Intent();
//        // 指定开启系统相机的Action
//        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        // 根据文件地址创建文件
//        if (!tempFile.exists()) {
//            try {
//                tempFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        // 把文件地址转换成Uri格式
//        Uri uri = Uri.fromFile(tempFile);
//        // 设置系统相机拍摄照片完成后图片文件的存放地址
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, CAMERA_REQUEST_COOE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_COOE) {
            //判断用户点击了拍照还是取消，如果是取消的话就不用进行数据的处理
//            if (data == null) {
//                return;
//            } else {
            if (data != null) {
                Bitmap bm = data.getExtras().getParcelable("data");
                tempFile = FileUtil.saveBitmapFile(bm, FileUtil.imgPath + System.currentTimeMillis() + "jpg");
            }
//            Bundle extras = data.getExtras();
//            if (extras != null) {
//                //创建Bitmap用于保存用户拍照的数据
//                Bitmap bm = extras.getParcelable("data");
//                if (bm == null) return;
//                if (data == null) {
//                    tempFile = FileUtil.saveBitmapFile(bm, FileUtil.imgPath + System.currentTimeMillis() + "jpg");
//                }
            String tem = tempFile.getAbsolutePath();
            userBean.sethead(tem);
            HjyApp.get().setUb(userBean);
            Log.e("hjy", tem);
            setUserInfo();
//            }
//                Bitmap bmp = BitmapFactory.decodeFile(cpath);
//                head.setImageBitmap(bmp);
//                }
//                Bundle extras = data.getExtras();
//                if (extras != null) {
//                    //创建Bitmap用于保存用户拍照的数据
//                    Bitmap bm = extras.getParcelable("data");
//                    ImageView imageView = findViewById(R.id.person);
//                    imageView.setImageBitmap((userBean.bm);
//                    userBean.sethead(bm);
//                }
//            }
        } else if (requestCode == GALLERY_REQUEST_COOE) {
            if (data == null) {
                return;
            }

            //当使用这种方法打开图库并选择一张照片后返回后onActivityResult方法中的data参数将会包含一个
            //uri（就是选择图片所对应的uri统一资源标识符）
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap == null) return;
            File alumfile = FileUtil.saveBitmapFile(bitmap, FileUtil.imgPath + System.currentTimeMillis() + "jpg");
            String alum = alumfile.getAbsolutePath();
            head.setImageBitmap(bitmap);
            userBean.sethead(alum);
            HjyApp.get().setUb(userBean);
            setUserInfo();
        }
    }


//    private Uri saveBitmap(Bitmap bm) {
//        //获取sdcard中的一个路径
//        File tmpDir = new File(FileUtil.imgPath + "/avater");
//        if (tmpDir.exists()) {
//            tmpDir.mkdir();
//        }
//        File img = new File(tmpDir.getAbsolutePath() + "avater.png");
//        try {
//            FileOutputStream fos = new FileOutputStream(img);
//
//            //将图像的数据写入该输出流中，第一个参数是要压缩的格式，第二个参数：图片的质量
//            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
//
//            fos.flush();
//            fos.close();
//            return Uri.fromFile(img);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if (playing) {
                    sendBroadcast(new Intent(MainService.pause));
                } else {
                    sendBroadcast(new Intent(MainService.play));
                }
                break;
            case R.id.back:
                dandan.setRotation(0);
                if (playMode == MainService.playMode_shuffle) {
                    switchSong();
                } else {
                    if (playposition > 0) {
                        playposition--;
                        sendBroadcast(new Intent(MainService.back).putExtra("back", playposition));
                    } else {
                        playposition = songBeanList.size() - 1;
                        sendBroadcast(new Intent(MainService.back).putExtra("back", playposition));

                    }

                }
                break;
            case R.id.forward:
                dandan.setRotation(0);
                if (playMode == MainService.playMode_shuffle) {
                    switchSong();
                } else {
                    if (playposition < songBeanList.size() - 1) {
                        playposition++;
                        sendBroadcast(new Intent(MainService.forward).putExtra("forward", playposition));
                    } else {
                        playposition = 0;
                        sendBroadcast((new Intent(MainService.forward).putExtra("forward", playposition)));
                    }
                }
                break;
            case R.id.mode:
                switch (playMode) {
                    case MainService.playMode_single:
                        playMode = MainService.playMode_loop;
                        mode.setImageResource(R.drawable.loop);
                        break;
                    case MainService.playMode_loop:
                        playMode = MainService.playMode_shuffle;
                        mode.setImageResource(R.drawable.shuffle);
                        break;
                    case MainService.playMode_shuffle:
                        playMode = MainService.playMode_single;
                        mode.setImageResource(R.drawable.single);
                        break;
                }

                sendBroadcast(new Intent(MainService.mode).putExtra("mode", playMode));
                break;
            case R.id.menu:
                showPopupWindow();
                break;
            case R.id.sync:
                AlertDialog.Builder sync = new AlertDialog.Builder(MainActivity.this);
                sync.setTitle("同步");
                sync.setMessage("是否同步手机中的音乐");
                sync.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                sync.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showPro();
                        new Thread() {
                            @Override
                            public void run() {
                                FileUtil.getSBsFormSd();
                                HjyApp.get().setSbs(FileUtil.getSBsFormCache());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dissPro();
                                    }
                                });
                            }
                        }.start();
                    }
                });
                sync.show();
                break;
            case R.id.information:
                drawerLayout.openDrawer(navigationView);
                break;
            case R.id.person:
                showCameraPopupWindow();

        }


    }

    private void showCameraPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.act_camera, null);
        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#ffffff"));
        window.setBackgroundDrawable(dw);
        window.setAnimationStyle(R.style.popwindow_anim_style);
        window.showAtLocation(singer, Gravity.BOTTOM, 0, 0);
        TextView camera = view.findViewById(R.id.tvCamera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicFromCamera();
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, CAMERA_REQUEST_COOE);
                window.dismiss();

            }
        });
        TextView album = view.findViewById(R.id.tvAlbum);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_COOE);

            }
        });
        TextView cancel = view.findViewById(R.id.tvCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case getDuration:
                    time = intent.getIntExtra("max", 0);
                    bar.setMax(time);
                    timeAll.setText(getCheckTime(time));
                    break;
                case end:
                    switchSong();
                    break;
                case back2:
                    playposition = intent.getIntExtra("back2", 0);
                    clickSong();
                    break;
                case forward2:
                    playposition = intent.getIntExtra("forward2", 0);
                    clickSong();
                    break;
                case serviceOnCreated:
                    clickSong();
                    break;
                case progress:
                    int progress = intent.getIntExtra("progress", 0);
                    bar.setProgress(progress);
                    timeCurrent.setText(getCheckTime(progress));
                    break;
                case playCheck:
                    playing = intent.getBooleanExtra("play", false);
                    switchPlayState();
                    break;
            }

        }
    };

    private float mAngle = 0;

    private float anglecalculation(long time) {

        float angle = (360 * ((time % 10000f) / 10000));
        return angle;
    }

    private void switchPlayState() {
        if (playing) {
            starttime = System.currentTimeMillis();
            Log.e("hjy", "---------开始时间" + starttime);
            dandan.startAnimation(animation);
            pause.setImageResource(R.drawable.pause);
        } else {
            pause.setImageResource(R.drawable.play);
            pausetime = System.currentTimeMillis();
            Log.e("hjy", "---------停止时间" + pausetime);
            dandan.clearAnimation();
            long checktime = Math.abs(starttime - pausetime);
            Log.e("hjy", "---------时间长度" + checktime);
            float angle = anglecalculation(checktime);
            Log.e("hjy", "---------angle:" + angle);
            angle = mAngle + angle;
            dandan.setRotation((angle) % 360);
            this.mAngle = angle;
        }
    }

    private void switchSong() {
        switch (playMode) {
            case MainService.playMode_single:
                break;
            case MainService.playMode_loop:
                playposition++;
                break;
            case MainService.playMode_shuffle:
                playposition = new Random().nextInt(songBeanList.size());
                break;
        }
        clickSong();

    }

    private String getCheckTime(int time) {
        int s = time % 60;
        int m = time / 60;
        String ss = s + "";
        String mm = m + "";
        if (s < 10) {
            ss = "0" + s;
        }
        if (m < 10) {
            mm = "0" + m;

        }
        return mm + ":" + ss;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(serviceIt);//关掉应用的时候不留后台
        unregisterReceiver(receiver);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        timeCurrent.setText(getCheckTime(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        sendBroadcast(new Intent(MainService.onStopTrackingTouch).putExtra("stop", bar.getProgress()));

    }

    public interface OnClickListener {
        void onClick(int position);
    }

    private class SystemBarTintManager {
    }
}

