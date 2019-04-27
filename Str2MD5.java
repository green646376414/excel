package com.walmart.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vn0xelr on 8/22/2018.
 */
public class Str2MD5 {

     

    public static void main(String[] args) {
        //String key = "key";
        //EhcacheHelper.put("mytest", key, "hello");
        //System.out.println(EhcacheHelper.get("mytest", key));

        String url = "http://localhost:9999/app/aa";

        Map map =new HashMap();
        map.put("a","收到收到");
        map.put("b","1111");
        String jsonList= JSON.toJSONString(map);
        //当前时间
        long timestampRes=System.currentTimeMillis();
        try {
            Map m =new HashMap();
            m.put("b","1111");
            //WebUtils.doPost(url, "application/json", JSON.toJSONString(re).getBytes(), 5000, 60000);
            //doPost(String url, Map<String, String> params,String charset, int connectTimeout, int readTimeout)
            String rs=	WebUtils.doPost(url,map,"utf-8" ,5000,60000);
            System.out.println(rs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}