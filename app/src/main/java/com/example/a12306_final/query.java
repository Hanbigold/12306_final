package com.example.a12306_final;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12306_final.Adapter.Tickets_Adapter;
import com.example.a12306_final.javabean.Tickets;
import com.example.a12306_final.javabean._User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class query extends AppCompatActivity {
    private TextView textView_title,Qury_date;
    private RecyclerView  recyclerView_Tickets;
    private Tickets_Adapter adapter;
    private List<Tickets> mList;
    private String FirstPlace,LastPlace,Begin_Day,Begin_Time,title,Begin,End;
    private ProgressDialog pd;
    private TextView menu2_beginTime,menu2_duration,menu2_ArriveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.station_query);
        Bmob.initialize(this,"22488e6103913f621999fda39c18cdec");
        //判断用户是否已登录
        _User userinfo = BmobUser.getCurrentUser(_User.class);
        if(userinfo == null){
            final AlertDialog.Builder Tixin=new AlertDialog.Builder(query.this)
                    .setTitle("温馨提示")
                    .setMessage("请先登录再进行查询")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(query.this,Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            Tixin.show();
        }
        Load();
        MenuLoad();
    }
    private void MenuLoad(){

        menu2_beginTime=(TextView)findViewById(R.id.menu2_beginTime);
        menu2_ArriveTime=(TextView)findViewById(R.id.menu2_ArriveTime);
        menu2_duration=(TextView)findViewById(R.id.menu2_duration);
        menu2_beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TimeUtil.isFastDoubleClick(R.id.menu2_beginTime)){
                    if(!menu2_beginTime.isSelected()){
                        pd=ProgressDialog.show(query.this,"Loading...","请稍等",true,false);
                        menu2_beginTime.setSelected(true);
                        menu2_ArriveTime.setSelected(false);
                        menu2_duration.setSelected(false);
                    }
                    listRload();
                }
            }
        });
        menu2_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TimeUtil.isFastDoubleClick(R.id.menu2_duration)){
                    if(!menu2_duration.isSelected()){
                        pd=ProgressDialog.show(query.this,"Loading...","请稍等",true,false);
                        menu2_beginTime.setSelected(false);
                        menu2_ArriveTime.setSelected(false);
                        menu2_duration.setSelected(true);
                    }
                    listRload();
                }

            }
        });
        menu2_ArriveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TimeUtil.isFastDoubleClick(R.id.menu2_ArriveTime)){
                        if(!menu2_ArriveTime.isSelected()){
                            pd=ProgressDialog.show(query.this,"Loading...","请稍等",true,false);
                            menu2_beginTime.setSelected(false);
                            menu2_ArriveTime.setSelected(true);
                            menu2_duration.setSelected(false);
                        }
                        listRload();
                    }
                }

        });
    }
    private void Load(){
        pd=ProgressDialog.show(query.this,"Loading...","请稍等",true,false);
        new Thread(){
            @Override
            public void run() {
                Intent getData=getIntent();
                FirstPlace=getData.getStringExtra("FirstPlace");
                LastPlace=getData.getStringExtra("LastPlace");
                Begin_Day=getData.getStringExtra("Begin_Day");
                Begin_Time=getData.getStringExtra("Begin_Time");
                textView_title=(TextView)findViewById(R.id.title_text);
                Qury_date=(TextView)findViewById(R.id.Query_Date);
                Qury_date.setText(Begin_Day);
                title=FirstPlace+"<-->"+LastPlace;
                textView_title.setText(title);
                Begin=Begin_Time.substring(0,5);
                End=Begin_Time.substring(7,12);

                //Toast.makeText(getBaseContext(),Begin+" "+End,Toast.LENGTH_LONG).show();

                recyclerView_Tickets=(RecyclerView)findViewById(R.id.recycler_Query);
                recyclerView_Tickets.setLayoutManager(new LinearLayoutManager(query.this));
                mList=new ArrayList<>();
                List<BmobQuery<Tickets>> AndQuery=new ArrayList<BmobQuery<Tickets>>();
                BmobQuery<Tickets> query_list = new BmobQuery<>();
                BmobQuery<Tickets> q1 = new BmobQuery<>();
                BmobQuery<Tickets> q2 = new BmobQuery<>();
                BmobQuery<Tickets> q3 = new BmobQuery<>();
                BmobQuery<Tickets> q4 = new BmobQuery<>();
                BmobQuery<Tickets> q5 = new BmobQuery<>();
                q1.addWhereEqualTo("FirstPlace",FirstPlace);
                q2.addWhereEqualTo("LastPlace",LastPlace);
                q3.addWhereGreaterThanOrEqualTo("FirstPlace_Time",Begin);
                q4.addWhereLessThanOrEqualTo("LastPlace_Time",End);
                q5.addWhereEqualTo("Date",Begin_Day);
                AndQuery.add(q1);
                AndQuery.add(q2);
                AndQuery.add(q3);
                AndQuery.add(q4);
                AndQuery.add(q5);
                query_list.and(AndQuery);
                query_list.order("StationID");
                query_list.findObjects(new FindListener<Tickets>() {
                    @Override
                    public void done(List<Tickets> list, BmobException e) {
                        if(e == null){
                            // mList = new ArrayList<>();
                            //Toast.makeText(getBaseContext(),"运行到这里",Toast.LENGTH_SHORT).show();
                            for(Tickets tickets : list){
                                Tickets tickets1=new Tickets(
                                        tickets.getStationID(),
                                        tickets.getUseTime(),
                                        tickets.getFirstPlace(),
                                        tickets.getLastPlace(),
                                        tickets.getFirstPlace_Time(),
                                        tickets.getLastPlace_Time(),
                                        tickets.getFirstPlace_Type(),
                                        tickets.getLastPlace_Type(),
                                        tickets.getShangwu_price(),
                                        tickets.getYideng_price(),
                                        tickets.getErdeng_price(),
                                        tickets.getShangwu_num(),
                                        tickets.getYideng_num(),
                                        tickets.getErdeng_num(),
                                        tickets.getWuzuo_num()
                                );
                                //String t=tickets1.getObjectId();
                                //Toast.makeText(getBaseContext(),t, Toast.LENGTH_SHORT).show();
                                mList.add(tickets1);
                            }
                            showAllTickets(mList);
                            handler.sendEmptyMessage(0);
                        }
                        else{
                            Toast.makeText(getBaseContext(),"无符合条件的列车",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //handler.sendEmptyMessage(0);
            }
        }.start();
    }
    private void showAllTickets(final List<Tickets> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter = new Tickets_Adapter(query.this,list);
                recyclerView_Tickets.setAdapter(adapter);
                adapter.setOnItemClickListener(new Tickets_Adapter.OnItemClickListener(){
                    @Override
                    public void onItemClick(View view, int position) {
                        //Toast.makeText(getBaseContext(), list.get(position).getStationID()+list.get(position).getFirstPlace(), Toast.LENGTH_SHORT).show();
                        String stationID=list.get(position).getStationID();
                        Intent toOrder_confirm=new Intent(query.this, confirm_Order.class);
                        toOrder_confirm.putExtra("stationID",stationID);
                        startActivity(toOrder_confirm);
                    }
                });
            }
        });

    }
    private void listRload(){
        mList=new ArrayList<>();
        List<BmobQuery<Tickets>> AndQuery=new ArrayList<BmobQuery<Tickets>>();
        BmobQuery<Tickets> query_list = new BmobQuery<>();
        BmobQuery<Tickets> q1 = new BmobQuery<>();
        BmobQuery<Tickets> q2 = new BmobQuery<>();
        BmobQuery<Tickets> q3 = new BmobQuery<>();
        BmobQuery<Tickets> q4 = new BmobQuery<>();
        BmobQuery<Tickets> q5 = new BmobQuery<>();
        q1.addWhereEqualTo("FirstPlace",FirstPlace);
        q2.addWhereEqualTo("LastPlace",LastPlace);
        q3.addWhereGreaterThanOrEqualTo("FirstPlace_Time",Begin);
        q4.addWhereLessThanOrEqualTo("LastPlace_Time",End);
        q5.addWhereEqualTo("Date",Begin_Day);
        AndQuery.add(q1);
        AndQuery.add(q2);
        AndQuery.add(q3);
        AndQuery.add(q4);
        AndQuery.add(q5);
        query_list.and(AndQuery);
        if(menu2_beginTime.isSelected()){
            query_list.order("FirstPlace_Time");
        }else if (menu2_ArriveTime.isSelected()){
            query_list.order("LastPlace_Time");
        }else if (menu2_duration.isSelected()){
            query_list.order("useTime");
        }
        query_list.findObjects(new FindListener<Tickets>() {
            @Override
            public void done(List<Tickets> list, BmobException e) {
                if (e == null) {
                    // mList = new ArrayList<>();
                    //Toast.makeText(getBaseContext(),"运行到这里",Toast.LENGTH_SHORT).show();
                    for (Tickets tickets : list) {
                        Tickets tickets1 = new Tickets(
                                tickets.getStationID(),
                                tickets.getUseTime(),
                                tickets.getFirstPlace(),
                                tickets.getLastPlace(),
                                tickets.getFirstPlace_Time(),
                                tickets.getLastPlace_Time(),
                                tickets.getFirstPlace_Type(),
                                tickets.getLastPlace_Type(),
                                tickets.getShangwu_price(),
                                tickets.getYideng_price(),
                                tickets.getErdeng_price(),
                                tickets.getShangwu_num(),
                                tickets.getYideng_num(),
                                tickets.getErdeng_num(),
                                tickets.getWuzuo_num()
                        );
                        //String t=tickets1.getObjectId();
                        //Toast.makeText(getBaseContext(),t, Toast.LENGTH_SHORT).show();
                        mList.add(tickets1);
                    }
                    showAllTickets(mList);
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
        }
    };
    @Override
    public void onBackPressed() {
        finish();
    }
}
