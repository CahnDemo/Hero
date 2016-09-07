package com.yujie.hero;

import android.app.Application;

import com.yujie.hero.bean.UserBean;

/**
 * Created by yujie on 16-9-7.
 */
public class HeroApplication extends Application {
    public static final String SERVER_ROOT = "http://115.28.2.61:8080/Hero/Server?";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public UserBean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserBean currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * current user who signed in
     */
    private UserBean currentUser;
    
    private static HeroApplication instance = null;
     private HeroApplication(){}
     public static HeroApplication getInstance() {
        synchronized (HeroApplication.class) {
            if (instance == null) {
                instance = new HeroApplication();
            }
        }
     
        return instance;}

}
