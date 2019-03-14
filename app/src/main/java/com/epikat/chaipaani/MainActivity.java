package com.epikat.chaipaani;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    public Connection DbConn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                ConnectToDatabase();
//                Intent i = new Intent(MainActivity.this, Login.class);
//                startActivity(i);
//                finish();
            }
        }, 2000);

    }

    public void ConnectToDatabase(){
        try {

            // SET CONNECTIONSTRING
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            String username = "chaipaani";
            String password = "chaipaani";
            DbConn = DriverManager.getConnection("chaipaani.chs6waedsdkl.us-east-1.rds.amazonaws.com;user=" + username + ";password=" + password);

            Statement stmt = DbConn.createStatement();
            ResultSet reset = stmt.executeQuery(" select * from users ");

            Log.w("Got result set", "here:" + reset.toString());
            TextView num = (TextView) findViewById(R.id.main_appname);
            num.setText(reset.getString(1));

            DbConn.close();

        } catch (Exception e)
        {
            Log.w("Error connection","" + e.getMessage());
        }
    }
}
