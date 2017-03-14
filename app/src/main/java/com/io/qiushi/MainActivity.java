package com.io.qiushi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.io.qiushi.data.source.remote.api.ApiService;
import com.io.qiushi.util.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Call<String> call = NetworkUtils.getInstance().create(ApiService.class)
                .getImageData(1);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String data = response.body();
                    Log.e("data>>", "" + data);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }
}
