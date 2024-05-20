package com.alan.clients.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Local {

    private static final String IP_API_URL = "https://api64.ipify.org?format=json";
    private static final String LOCATION_API_URL = "http://ip-api.com/json/";

    private static final HttpClient httpClient = HttpClients.createDefault();

    private static String fetchResponse(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        HttpEntity entity = httpClient.execute(request).getEntity();
        return EntityUtils.toString(entity);
    }

    private static String getPublicIPAddress() throws IOException {
        String response = fetchResponse(IP_API_URL);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("ip");
    }

    private static String getLocationByIP(String ipAddress) throws IOException {
        String apiUrl = LOCATION_API_URL + ipAddress;
        String response = fetchResponse(apiUrl);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("regionName");
    }

    private static final Map<String, String> LOCATION_MAP = initializeLocationMap();

    private static Map<String, String> initializeLocationMap() {
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("beijing", "[北京入]");
        locationMap.put("tianjin", "[天津入]");
        locationMap.put("shanghai", "[上海入]");
        locationMap.put("chongqing", "[重庆入]");

        // 省份
        locationMap.put("anhui", "[安徽省入]");
        locationMap.put("fujian", "[福建省入]");
        locationMap.put("gansu", "[甘肃省入]");
        locationMap.put("guangdong", "[广东省入]");
        locationMap.put("guizhou", "[贵州省入]");
        locationMap.put("hainan", "[海南省入]");
        locationMap.put("hebei", "[河北省入]");
        locationMap.put("heilongjiang", "[黑龙江省入]");
        locationMap.put("henan", "[河南井盖侠]");
        locationMap.put("hubei", "[湖北省入]");
        locationMap.put("hunan", "[湖南省入]");
        locationMap.put("jiangsu", "[江苏省入]");
        locationMap.put("jiangxi", "[江西省入]");
        locationMap.put("jilin", "[吉林省入]");
        locationMap.put("liaoning", "[辽宁省入]");
        locationMap.put("qinghai", "[青海省入]");
        locationMap.put("shaanxi", "[陕西省入]");
        locationMap.put("shandong", "[山东省入]");
        locationMap.put("shanxi", "[山西省入]");
        locationMap.put("sichuan", "[四川省入]");
        locationMap.put("yunnan", "[云南省入]");
        locationMap.put("zhejiang", "[浙江省入]");

        // 自治区
        locationMap.put("guangxi", "[广西老表]");
        locationMap.put("neimenggu", "[内蒙古入]");
        locationMap.put("ningxia", "[宁夏入]");
        locationMap.put("xizang", "[西藏入]");
        locationMap.put("xinjiang", "[新疆入]");

        // 特别行政区
        locationMap.put("hongkong", "[香港入]");
        locationMap.put("macau", "[澳门入]");
        locationMap.put("taiwan", "[中华民国入]");
        return locationMap;
    }

    public static String getLocation() {
        try {
            String location = getLocationByIP(getPublicIPAddress());
            String province = LOCATION_MAP.getOrDefault(location.toLowerCase(), "外国");
            return (province != null) ? province : "外国";
        } catch (IOException e) {
            e.printStackTrace();
            return "获取位置失败";
        }
    }
}
