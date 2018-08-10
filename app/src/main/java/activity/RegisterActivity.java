package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clock.player2.R;

import java.util.ArrayList;
import java.util.List;

import bean.SongBean;
import bean.UserBean;

public class RegisterActivity extends Activity {
    ListDataSaveTool dataSave;
    private EditText newaccount;
    private EditText newpassword;
    private EditText repeatpassword;
    private Button register;
    private String user;
    private TextView hasaccount;
    int number = 0;
    public List<UserBean> list;
    public UserBean bean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        newaccount = findViewById(R.id.newAccount);
        newpassword = findViewById(R.id.newPassword);
        repeatpassword = findViewById(R.id.password);
        register = findViewById(R.id.register);
        hasaccount = findViewById(R.id.hasAccount);
        dataSave = new ListDataSaveTool(this);
        list = dataSave.getDataList();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckAccount();
            }
        });
        hasaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void CheckAccount() {
        String account = newaccount.getText().toString();
        if (list.size() == 0) {
            save();
        } else {
            for (int i = 0; i < list.size(); i++) ;
            {
                UserBean bean = list.get(number);
                if (bean.account.equals(account)) {
                    Toast.makeText(RegisterActivity.this, "该账号已经存在", Toast.LENGTH_SHORT).show();
                } else {
                    save();
                }
            }
        }
    }

    public void save() {
        String account = newaccount.getText().toString();
        String password = newpassword.getText().toString();
        String repeat = repeatpassword.getText().toString();
        if (password.equals(repeat)) {
            UserBean user = new UserBean();
            user.setAccount(account);
            user.setPassword(password);
            list.add(user);
            dataSave.setDataList(list);
            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("account", account);
            intent.putExtra("password", password);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "两次密码输入不一样，请重新输入密码", Toast.LENGTH_SHORT).show();
        }
    }

}
