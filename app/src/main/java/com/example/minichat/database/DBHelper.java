package com.example.minichat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.minichat.MyApplication;
import com.example.minichat.entity.LoginInfo;
import com.example.minichat.entity.Msg;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SummCoder
 * @date 2024/1/13 15:58
 */
public class DBHelper extends SQLiteOpenHelper {
    //    数据库名称以及版本
    private static final String DB_NAME = "miniChat.db";
    private static final int DB_VERSION = 1;
    private static DBHelper mHelper = null;

    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    //    构造方法
    private DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance(Context context){
        if(mHelper == null){
            mHelper = new DBHelper(context);
        }
        return mHelper;
    }

    // 打开数据库读连接
    public SQLiteDatabase openReadLink(){
        if(mRDB == null || !mRDB.isOpen()){
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    // 打开数据库写连接
    public SQLiteDatabase openWriteLink(){
        if(mWDB == null || !mWDB.isOpen()){
            mWDB = mHelper.getWritableDatabase();
        }
        return mWDB;
    }

    // 关闭数据库连接
    public void closeLink(){
        if(mRDB != null && mRDB.isOpen()){
            mRDB.close();
            mRDB = null;
        }
        if(mWDB != null && mWDB.isOpen()){
            mWDB.close();
            mWDB = null;
        }
    }

    // 创建数据库
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql_login = "CREATE TABLE IF NOT EXISTS " + "login_info" + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " username VARCHAR NOT NULL," +
                " password VARCHAR NOT NULL," +
                " remember INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(sql_login);

        String chat_sql = "CREATE TABLE IF NOT EXISTS " + "chat_info" + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " robot_id INTEGER NOT NULL," +
                " user_id INTEGER NOT NULL," +
                " role INTEGER NOT NULL," +
                " chat_content TEXT NOT NULL);";
        sqLiteDatabase.execSQL(chat_sql);
    }

    // 更新数据库版本
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertLoginInfo(LoginInfo loginInfo){
        ContentValues values = new ContentValues();
        values.put("username", loginInfo.username);
        values.put("password", loginInfo.password);
        values.put("remember", loginInfo.remember);
        return mWDB.insert("login_info", null, values);
    }

    public long insertChat(int robot_id, int role, String chat_content){
        ContentValues values = new ContentValues();
        values.put("robot_id", robot_id);
        values.put("user_id", MyApplication.getInstance().infoMap.get("userid"));
        values.put("role", role);
        values.put("chat_content", chat_content);
        return mWDB.insert("chat_info", null, values);
    }

    // 保存或更新原来保存的密码
    public void saveLoginInfo(LoginInfo info){
        try {
            mWDB.beginTransaction();
            deleteLoginInfo(info);
            insertLoginInfo(info);
            mWDB.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mWDB.endTransaction();
        }
    }

    public long deleteLoginInfo(LoginInfo info){
        return mWDB.delete("login_info", "username=?", new String[]{info.username});
    }

    public long deleteMsg(int robotId){
        String selection = "robot_id = ? AND user_id = ?";
        String[] selectionArgs = { String.valueOf(robotId), MyApplication.getInstance().infoMap.get("userid")};
        return mWDB.delete("chat_info", selection, selectionArgs);
    }

    // 读取最后一条添加用户名及密码
    public LoginInfo queryTop(){
        LoginInfo info = null;
        String sql = "select * from login_info where remember = 1 ORDER BY _id DESC limit 1";
        Cursor cursor = mRDB.rawQuery(sql, null);
        if(cursor.moveToNext()){
            info = new LoginInfo();
            info.id = cursor.getInt(0);
            info.username = cursor.getString(1);
            info.password = cursor.getString(2);
            info.remember = cursor.getInt(3) != 0;
        }
        return info;
    }

    public LoginInfo queryByUsername(String username){
        LoginInfo info = null;
        Cursor cursor = mRDB.query("login_info", null, "username=? and remember=1", new String[]{username}, null, null, null);
        if(cursor.moveToNext()){
            info = new LoginInfo();
            info.id = cursor.getInt(0);
            info.username = cursor.getString(1);
            info.password = cursor.getString(2);
            info.remember = cursor.getInt(3) != 0;
        }
        return info;
    }

    public List<Msg> queryByUserAndRobot(int robotId){
        List<Msg> msgList = new ArrayList<>();
        String selection = "robot_id = ? AND user_id = ?";
        String[] selectionArgs = { String.valueOf(robotId), MyApplication.getInstance().infoMap.get("userid")};
        Cursor cursor = mRDB.query("chat_info", null, selection, selectionArgs, null, null, null);
        while(cursor.moveToNext()){
            Msg msg = new Msg(cursor.getString(4), cursor.getInt(3));
            msgList.add(msg);
        }
        cursor.close();
        return msgList;
    }
}
