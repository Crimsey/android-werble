package com.example.android_werble.network;

import android.os.Build;

import com.example.android_werble.TokenManager;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import com.example.android_werble.BuildConfig;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitBuilder {
    //private static final String BASE_URL = "http://192.168.10.10/api/";//"http://werble.test/api/";
    private static final String BASE_URL = "http://192.168.10.10/api/";//"http://werble.test/api/";



    private final static OkHttpClient client = buildClient();

    private static Retrofit retrofit = buildRetrofit(client);

    private static OkHttpClient buildClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //.connectTimeout(10, TimeUnit.SECONDS)
                //.readTimeout(15,TimeUnit.SECONDS)
                //.writeTimeout(15,TimeUnit.SECONDS)
                .callTimeout(5,TimeUnit.MINUTES)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();

                    Request.Builder builder = request.newBuilder()
                            .addHeader("Content-Type","application/json")
                            .addHeader("Accept","application/json")
                            .addHeader("Content-Type","multipart/form data");
                            //.addHeader("Accept","application/json")
                            //.header("Accept-Encoding", "identity")
                            //.addHeader("Content-Type","multipart/form data")
                            //.addHeader("Content-Type","application/json")
                            //.addHeader("Connection","close");


                    request = builder.build();

                    return  chain.proceed(request);
                }
            });
        if (BuildConfig.DEBUG){
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return  builder.build();
    }

    private static Retrofit buildRetrofit(OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service){
        return retrofit.create(service);
    }

    public static <T> T createServiceWithAuth(Class<T> service, final TokenManager tokenManager){

        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                if(tokenManager.getToken().getAccessToken() != null){
                    builder.addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
                }
                request = builder.build();
                return chain.proceed(request);
            }
        }).authenticator(CustomAuthenticator.getInstance(tokenManager)).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();//.callbackExecutor(Executors.newSingleThreadExecutor()).build();
        return newRetrofit.create(service);

    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

}
