package activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clock.player2.R;

import java.util.List;

import bean.UserBean;
import service.HjyApp;

public class LoginActivity extends Activity {
    ListDataSaveTool dataSave;
    private EditText accountE;
    private EditText passwordE;
    private CheckBox remember;
    private TextView register;
    private Button login;
    private List<UserBean> list;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);//安卓手机SD卡获取权限
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        accountE = findViewById(R.id.account);
        passwordE = findViewById(R.id.password);
        remember = findViewById(R.id.remember);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        dataSave = new ListDataSaveTool(this);//?
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            accountE.setText(account);
            passwordE.setText(password);
            remember.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = dataSave.getDataList();
                String account = accountE.getText().toString();
                String password = passwordE.getText().toString();

                UserBean ub = dataSave.getUb(account, password);
                if (ub == null) {
                    Toast.makeText(LoginActivity.this, "账号不存在或者密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    if (remember.isChecked()) {
                        editor = pref.edit();
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                        editor.commit();
                    }
                    HjyApp.get().setUb(ub);
                    Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();
                }
                /*boolean hasUser = false;
                for (int i = 0; i < list.size(); i++) {
                    UserBean bean = list.get(i);
                    if (bean.account.equals(account) && bean.password.equals(password)) {
                        hasUser = true;
                        editor = pref.edit();
                        if (remember.isChecked()) {
                            editor.putBoolean("remember_password", true);
                            editor.putString("account", account);
                            editor.putString("password", password);
                        } else {
                            editor.clear();
                        }
                        editor.apply();
                        break;
                    }
                }
                if (hasUser) {
                    HjyApp.get().setUb();
                    Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                    startActivityForResult(intent, 2);
//                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "账号不存在或者密码错误", Toast.LENGTH_SHORT).show();
                }
*/
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

