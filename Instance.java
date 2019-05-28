package cn.train.util;


import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * DB_URL 数据库url 注:应以'/'结尾 而且不应该带数据库名
 * DATABASE 数据库名
 * IS_CREATE_DATABASE 是否创建数据库 如果true且数据库存在会发生Exception异常
 * USER 账号
 * PASS 密码
 * FILE_PATH 要生成数据库表的Bean文件所在的文件夹
 * 
 * @author mc
 * 
 */
public class Instance {
 
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //private static final String DB_URL = "jdbc:mysql://localhost/";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE = "dream";
    private static final boolean IS_CREATE_DATABASE = true;
    private static final String USER = "root";
    private static final String PASS = "1111";
    //D:\1111\HuoCheDingPiaoXiTong\src\main\java\cn\train\model
    //private static final String FILE_PATH = "C:\\Users\\mc\\Desktop\\appweb\\src\\main\\java\\com\\pojo";
    private static final String FILE_PATH = "D:\\1111\\HuoCheDingPiaoXiTong\\src\\main\\java\\cn\\train\\model";
    private static StringBuffer sb = new StringBuffer();
    private static Map<String, List<String[]>> map = new HashMap();
    private static int index;
    /**
     * change方法用以后面扩展
     * @param s 接收的bean文件 属性类型
     * @return 数据库要创建的类型
     */
    public static String change(String s){
        switch (s){
            case "long":
            case "Long":
                s = "BIGINT";
                break;
            case "String":
            case "BigDecimal":
                s = "VARCHAR(255)";
                break;
        }
        return s;
    }
 
    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        if(IS_CREATE_DATABASE){
            String sql = "create database "+DATABASE+" character set utf8 collate utf8_general_ci";
            if(!executeUpdate(DB_URL, sql)){
                System.err.println("创建数据库失败.");
                throw new Exception();
            }
        }
 
        File file = new File(FILE_PATH);
        File[] files = file.listFiles();
        int ch;
        for (File f : files) {
            if(f.isFile() && f.getName().endsWith(".java")){
                FileReader fileReader = new FileReader(f);
                sb.delete(0,sb.length());
                index = 0;
                while ((ch=fileReader.read())!=-1){
                    sb.append((char)ch);
                }
                try {
                    jx();
                } catch (Exception e){
                    System.err.println("文件:"+f.getName()+"不是Bean");
                }
 
                fileReader.close();
            }
        }
 
        for (String s : map.keySet()) {
            String sql = createSQL(s);
            if(!executeUpdate(DB_URL+DATABASE,sql)){
                System.err.println("创建"+s+"表失败:"+sql);
            }
        }
 
 
    }
 
    /**
     * createSQL 方法为生成数据库的建表语句 可以根据需要修改
     * @param s 数据表名
     * @return 创建数据表的SQL语句
     */
    public static String createSQL(String s){
        String sql = "CREATE TABLE "+s+" (";
        List<String[]> list = map.get(s);
        String[] id = deleteId(list);
        sql += id[1]+" "+id[0]+" not NULL,";
        for (String[] strs : list) {
            sql +=strs[1]+" "+strs[0]+",";
        }
 
        sql += "PRIMARY KEY ( "+id[1]+" )";
        sql+= ")";
        return sql;
    }
 
    public static String[] deleteId(List<String[]> list){
        for (int i=0;i<list.size();i++) {
            if(list.get(i)[1].equalsIgnoreCase("id")){
                return list.remove(i);
            }
        }
        return list.remove(0);
    }
 
    public static void jx() throws Exception {
        String table = jxStr("public class ", " ", null);
        if(table!=null){
            ArrayList<String[]> list = new ArrayList<>();
            String str;
            while ((str=jxStr("private ", ";", "class "))!=null){
                str = removeRedundantSpaces(str);
                String[] split = str.split(" ");
                if(split.length==2){
                    list.add(new String[]{change(split[0]), split[1]});
                }
            }
            map.put(removeRedundantSpaces(table), list);
        }
    }
 
    public static String removeRedundantSpaces(String s){
        s = s.trim();
        return s.replaceAll("\\s+", " ");
    }
 
    public static String jxStr(String startStr, String endStr, String abnormal) throws Exception {
        String str = sb.toString();
        if(abnormal!=null && str.indexOf(abnormal, index)!=-1){
            throw new Exception();
        }
        int i = str.indexOf(startStr, index);
        if(i!=-1){
            int i1 = str.indexOf(endStr, i+startStr.length());
            if(i1!=-1){
                index = i1+endStr.length();
                return str.substring(i + startStr.length(), i1);
            }
        }
        return null;
    }
 
    public static boolean executeUpdate(String dbUrl,String sql) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(dbUrl, USER, PASS);
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            return false;
        }finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(stmt!=null){
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
 
}
