package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.clock.player2.R;

import bean.UserBean;
import service.HjyApp;

public class SexActivity extends Activity {
    private ImageView ivBack;
    private TextView tvSex;
    private LinearLayout llSex;
    private UserBean userBean;
    private LinearLayout full;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex);
        userBean = HjyApp.get().getUb();//获取Actvity之间的传值
        tvSex = findViewById(R.id.sex);
        ivBack = findViewById(R.id.back);
        llSex = findViewById(R.id.llSex);
        full=findViewById(R.id.full);
        tvSex.setText(userBean.getSex() ? "男" : "女");
        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SexActivity.this, MainActivity.class));
            }
        });
        llSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopwindow();
            }
        });

    }

    private void showPopwindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.act_pop, null);
        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#ffffff"));
        window.setBackgroundDrawable(dw);
        window.setAnimationStyle(R.style.popwindow_anim_style);
        window.showAtLocation(llSex, Gravity.BOTTOM, 0, 0);
        TextView male = view.findViewById(R.id.tvMale);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSex(true);
                window.dismiss();

            }
        });
        TextView female = view.findViewById(R.id.tvFemale);
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSex(false);
                window.dismiss();

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

    private void updateSex(boolean sex) {
        userBean.setSex(sex);
        tvSex.setText(userBean.getSex() ? "男" : "女");
        HjyApp.get().setUb(userBean);
    }
}
