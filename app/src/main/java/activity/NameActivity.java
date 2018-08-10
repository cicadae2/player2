package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.clock.player2.R;

import java.util.List;

import bean.UserBean;
import service.HjyApp;

public class NameActivity extends Activity {
    private ImageView back;
    private EditText etName;
    private UserBean userBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        userBean = HjyApp.get().getUb();//获取Actvity之间的传值
        back = findViewById(R.id.back);
        etName = findViewById(R.id.name);
        etName.setText(userBean.getName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateName(editable.toString());
            }
        });

    }

    private void updateName(String newname) {
        Log.e("hjy", newname);
        userBean.setName(newname);
        HjyApp.get().setUb(userBean);
    }
}
