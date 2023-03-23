package com.bk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * @Description:
 * @Author: Dark Wang
 * @Create: 2023/3/23 10:08
 **/
public class JsonToSQL {
    public static final String preSQL = "insert into t_comm_city(city_id,city_name,parent_city_id,city_level) values(";
    public static final String suffSQL = ");";
    public static final String PROVINCE_LEVEL = "1";
    public static final String CITY_LEVEL = "2";
    public static final String AREA_LEVEL = "3";

    public static void main(String[] args) {
        String filePath = "/Users/zirawell/Git/China_Province_City/2020年8月中华人民共和国县以上行政区划代码.json";
        String outputPath = "/Users/zirawell/Git/China_Province_City/src/main/java/com/bk/sql/2020年8月中华人民共和国县以上行政区划代码.sql";
        String jsonString = readJsonFile(filePath);
        Set<Map> pList = new HashSet();
        Set<Map> cList = new HashSet();
        Set<Map> aList = new HashSet();
        dealWithData(pList,cList,aList,jsonString);
        writeSqlFile(pList, cList, aList, outputPath);
    }
    private static void dealWithData(Set<Map> pList, Set<Map> cList, Set<Map> aList, String jsonString) {
        JSONArray pJsonArray = JSON.parseArray(jsonString);
        if (null != pJsonArray) {
            for (Object pJsonObject : pJsonArray) {
                JSONObject pJson = (JSONObject) pJsonObject;
                Map<String, String> pMap = new HashMap<String, String>();
                pMap.put("id", (String) pJson.get("code"));
                pMap.put("name", (String) pJson.get("name"));
                pMap.put("levelType", PROVINCE_LEVEL);
                pList.add(pMap);
                JSONArray cJsonArray = pJson.getJSONArray("cityList");
                if (null != cJsonArray) {
                    for (Object cJsonObject : cJsonArray) {
                        JSONObject cJson = (JSONObject) cJsonObject;
                        Map<String, String> cMap = new HashMap<String, String>();
                        cMap.put("id", (String) cJson.get("code"));
                        cMap.put("name", (String) cJson.get("name"));
                        cMap.put("parentId", (String) pJson.get("code"));
                        cMap.put("levelType", CITY_LEVEL);
                        cList.add(cMap);
                        JSONArray aJsonArray = cJson.getJSONArray("areaList");
                        if (null != aJsonArray) {
                            for (Object aJsonObject : aJsonArray) {
                                JSONObject aJson = (JSONObject) aJsonObject;
                                Map<String, String> dMap = new HashMap<String, String>();
                                dMap.put("id", (String) aJson.get("code"));
                                dMap.put("name", (String) aJson.get("name"));
                                dMap.put("parentId", (String) cJson.get("code"));
                                dMap.put("levelType", AREA_LEVEL);
                                aList.add(dMap);
                            }
                        }
                    }
                }
            }
        }
    }
    private static void writeSqlFile(Set<Map> pList, Set<Map> cList, Set<Map> aList, String outputPath) {
        StringBuffer sb = new StringBuffer();
        sb.append(createSql(pList));
        sb.append(createSql(cList));
        sb.append(createSql(aList));
        writeFile(outputPath, sb);
    }
    private static String createSql(Set<Map> list) {
        StringBuffer sb = new StringBuffer();
        for (Map map : list) {
            sb.append(preSQL)
                    .append(map.get("id")).append(",")
                    .append("'").append(map.get("name")).append("'").append(",")
                    .append(map.get("parentId")).append(",")
                    .append(map.get("levelType"))
                    .append(suffSQL)
                    .append("\n");
        }
        return sb.toString();
    }
    public static String readJsonFile(String fileName) {
        String jsonStr;
        File jsonFile = new File(fileName);
        try (Reader reader = new InputStreamReader(Files.newInputStream(jsonFile.toPath()), StandardCharsets.UTF_8)) {
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void writeFile(String outputPath, StringBuffer sb) {
        FileOutputStream out = null;
        try {
            File file = new File(outputPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileOutputStream(file, false);
            out.write(sb.toString().getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}



