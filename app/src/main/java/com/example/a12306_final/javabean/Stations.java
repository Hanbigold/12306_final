package com.example.a12306_final.javabean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Strayer on 2017/11/29.
 */

public class Stations extends BmobObject{
    private String station_name;

    public String getStation_name(){
        return station_name;
    }
    public void setStation_name(String station_name){
        this.station_name=station_name;
    }
}
