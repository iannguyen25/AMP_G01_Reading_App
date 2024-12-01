package com.example.amp_g01_reading_app.connect;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //private static final String BASE_URL = " http://localhost:8001/api/";
    private static final String BASE_URL = "https://21c0-42-113-119-187.ngrok-free.app/api/"; // Địa chỉ của web service
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Sử dụng Gson để parse JSON
                    .build();
        }
        return retrofit;
    }
}
