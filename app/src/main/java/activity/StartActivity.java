package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.clock.player2.R;

import java.util.ArrayList;
import java.util.List;

import bean.SongBean;
import service.HjyApp;
import util.FileUtil;
import util.NetUtil;
import util.UrlUtil;

public class StartActivity extends BaseActivity {
    private int downIndex = 0;
    private ProgressBar bar;
    private TextView progress_number;
    private boolean cancelDwon = false;
    private AlertDialog dialog;
    private boolean downMusic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        showPro();
        new Thread() {
            @Override
            public void run() {
                if (FileUtil.existMusic()) {
                    HjyApp.get().setSbs(FileUtil.getSBsFormCache());
                } else {
                     List<SongBean> sdSBs = FileUtil.getSBsFormSd();
                    if (sdSBs.isEmpty()) {
                        downMusic = true;
                    } else {
                        HjyApp.get().setSbs(sdSBs);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissPro();
                        downMusic(downMusic);
                    }
                });
            }
        }.start();
    }

    private void toMain() {
        if (!HjyApp.get().getSbs().isEmpty()) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        }
        finish();
    }

    private void downMusic(boolean down) {
        if (down) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(StartActivity.this);
            dialog.setTitle("下载");
            dialog.setMessage("是否下载示例音乐？");
            dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    toMain();
                }
            });
            dialog.setNegativeButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showDownDialog();
                    download();
                }
            });
            dialog.show();
        } else {
            toMain();
        }
    }

    private void showDownDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View progressView = inflater.inflate(R.layout.activity_dialog, null);
        progress_number = progressView.findViewById(R.id.progress_number);
        bar = progressView.findViewById(R.id.progress_bar);
        bar.setMax(UrlUtil.getUrls().size());
        progress_number.setText(0 + "/" + bar.getMax());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("下载进程");
        builder.setView(progressView);
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelDwon = true;
                toMain();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void showDownAgainDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(StartActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("未下载到音乐,继续下载？");
        dialog.setPositiveButton("退出APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("继续下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDownDialog();
                download();
            }
        });
        dialog.show();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int index = msg.arg1;
                    bar.setProgress(index);
                    progress_number.setText(index + "/" + bar.getMax());
                    break;
                case 2: //说明下载完成 去跳转吧！！！！
                    dialog.dismiss();
                    showPro();
                    break;
                case 3:
                    dissPro();
                    if (HjyApp.get().getSbs().isEmpty()) {
                        //提示弹窗 继续下载
                        showDownAgainDialog();
                    } else {
                        //下载到音乐的情况
                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                        finish();
                    }
                    break;
            }
        }
    };

    public void download() {
        new Thread() {
            @Override
            public void run() {
                downIndex = 0;
                List<String> urls = UrlUtil.getUrls();
                for (String url : urls) {
                    if (cancelDwon) {
                        break;
                    }
                    NetUtil.downloadFile(url);
                    downIndex++;
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = downIndex;
                    handler.sendMessage(msg);
                }
                handler.sendEmptyMessage(2);
                HjyApp.get().setSbs(FileUtil.getSBsFormCache());
                handler.sendEmptyMessage(3);
            }
        }.start();
    }
//            dialog.setNegativeButton("下载", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    LayoutInflater inflater = LayoutInflater.from(StartActivity.this);
//                    final View progressView = inflater.inflate(R.layout.activity_dialog, null);
//                    bar = progressView.findViewById(R.id.progress_bar);
//                    progress_number = progressView.findViewById(R.id.progress_number);
//                    bar.setMax(UrlUtil.getUrls().size());
//                    AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
//                    builder.setTitle("下载进程");
//                    builder.setView(progressView);
//                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            startActivity(new Intent(StartActivity.this, MainActivity.class));
//                        }
//                    });
//                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            cancelDwon = true;
//                            startActivity(new Intent(StartActivity.this, MainActivity.class));
//
//                        }
//                    });
//
//                    builder.create().show();
//                    download();
//                }
//            });
//            dialog.show();
//        } else {
//            startActivity(new Intent(this, MainActivity.class));
//        }
//
//    }
//
//    public void download() {
//        new Thread() {
//            @Override
//            public void run() {
//                List<String> urls = UrlUtil.getUrls();
//                for (String url : urls) {
//                    if (cancelDwon) {
//                        break;
//                    }
//                    NetUtil.downloadFile(url);
//                    downIndex++;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            updateProgressView();
//                        }
//                    });
//                }
//            }
//        }.start();
//
//    }
//       private void updateProgressView(){
//        bar.setProgress(downIndex);
//        int max=bar.getMax();
//        progress_number.setText(downIndex+"/"+max);
//
//
//    }


}