package service;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import activity.ListDataSaveTool;
import bean.SongBean;
import bean.UserBean;

public class HjyApp extends Application {

    private static HjyApp instance;

    public static HjyApp get() {
        return instance;
    }

    private ListDataSaveTool dataSaveTool;
    private List<SongBean> sbs;

    private UserBean ub; //当前用户

    public void setUb(UserBean ub) {
        this.ub = ub;
        dataSaveTool.setUserBean(ub);
    }

    public UserBean getUb() {
        return ub;
    }

    public List<SongBean> getSbs() {
        if (sbs == null) sbs = new ArrayList<>();
        return sbs;
    }

    public void setSbs(List<SongBean> sbs) {
        if (sbs == null) return;
        if (this.sbs == null) this.sbs = new ArrayList<>();
        this.sbs.clear();
        this.sbs.addAll(sbs);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dataSaveTool = new ListDataSaveTool(this);
    }
}
