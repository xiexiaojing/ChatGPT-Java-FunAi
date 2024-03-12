package com.gzhu.funai;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.*;

public class TestDB {
    @Test
    public void test() {
        // JDBC驱动程序名称及URL
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3307/funai?useSSL=false";

        // 数据库登录信息
        String username = "root";
        String password = "root";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 加载JDBC驱动程序
            Class.forName(driver);

            // 建立数据库连接
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(url, username, password);

            // 创建Statement对象
            System.out.println("Creating statement object...");
            stmt = conn.createStatement();

            // 执行查询语句
            String sql = "SELECT * FROM user_session";
            System.out.println("Executing query...");
            rs = stmt.executeQuery(sql);

            // 处理结果集
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                // 输出每条记录的内容
                System.out.print("ID: " + id);
                System.out.println(", Name: " + name);
            }

            // 关闭ResultSet、Statement和Connection对象
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 确保所有资源都被正确地关闭
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
        }//end finally block
    }

    @Test
    public void getToken() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("grant_type", "client_credentials");
        paramsMap.put("client_id", "fsLWpiMEV7BCRfi8LHXZPIGs");
        paramsMap.put("client_secret", "16cOCfBOX2VK2gZQkpjJ2UUyHZodZHU3");
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = paramsMap.keySet();
        for(String key:keySet) {
            String value = paramsMap.get(key);
            formBodyBuilder.add(key,value);
        }
        FormBody formBody = formBodyBuilder.build();
        Request request = new Request
                .Builder()
                .post(formBody)
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .build();
        try (Response response = mOkHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
