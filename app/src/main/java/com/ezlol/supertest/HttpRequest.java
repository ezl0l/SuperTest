package com.ezlol.supertest;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.*;

class Requests {
    public static final MediaType MEDIA_TYPE = MediaType.parse("multipart/form-data; charset=utf-8");

    public static Response get(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            okhttp3.Response response = client.newCall(request).execute();
            return new Response(response.body().string(), response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response post(String url, Map<String, String> data){
        try {
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder body = new FormBody.Builder();
            for (Map.Entry<String, String> e : data.entrySet()) {
                body.add(e.getKey(), e.getValue());
            }
            Request request = new Request.Builder().url(url).post(body.build()).build();
            okhttp3.Response response = client.newCall(request).execute();
            return new Response(response.body().string(), response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class Response {
    private final String string;
    private final int statusCode;

    public Response(String string, int statusCode) {
        this.string = string;
        this.statusCode = statusCode;
    }

    public JSONObject json(){
        return JSON.decode(string);
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return string;
    }
}