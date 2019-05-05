package com.example.actividadrealm;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BaseAplicacionRealm extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuracion= new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuracion);
    }

}
