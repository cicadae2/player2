package activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import bean.UserBean;

/**
 * 这是一个工具类
 * <p>
 * 存在哪： SharedPreferences 这个是Android轻量级数据保存库 只能存基本类型
 * <p>
 * 作用： 保存用户列表 获取用户列表 更新某个用户信息
 */
public class ListDataSaveTool {
    private static final String key_user_list = "key_user_list";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public ListDataSaveTool(Context mContext) {
        //获取 SharedPreferences 实体类对象以供使用
        pref = mContext.getSharedPreferences("users", Context.MODE_PRIVATE);
        //获取 Editor 用来保存数据
        editor = pref.edit();
    }

    /**
     * 更新用户信息到列表 并保存到sp
     *
     * @param userBean
     */
    public void setUserBean(UserBean userBean) {
        List<UserBean> list = getDataList();
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            UserBean bean = list.get(i);
            if (bean.account.equals(userBean.account)) {
                index = i;
                break;
            }
        }
        if (index > -1) {
            list.remove(index);
            list.add(index, userBean);
        }
        setDataList(list);
    }

    public UserBean getUb(String account, String pwd) {
        List<UserBean> list = getDataList();
        for (int i = 0; i < list.size(); i++) {
            UserBean bean = list.get(i);
            if (bean.account.equals(account) && bean.password.equals(pwd)) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 保存用户列表到sharepreferencr
     *
     * @param
     */

    public void setDataList(List<UserBean> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.putString(key_user_list, strJson);
        editor.commit();
    }

    /**
     * 获取sharepreferencr的用户列表
     *
     * @return
     */
    public List<UserBean> getDataList() {
        List<UserBean> datalist = new ArrayList<>();
        String strJson = pref.getString(key_user_list, null);
        if (null == strJson) {
            return datalist;
        }

        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<UserBean>>() {
        }.getType());
        return datalist;
    }
}