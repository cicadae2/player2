package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog progress;


    protected void showPro() {
        progress = ProgressDialog.show(this, "", "正在获取手机的音乐...");
        progress.setCancelable(false);
    }

    protected void dissPro() {
        if (progress != null) progress.dismiss();
    }


}
