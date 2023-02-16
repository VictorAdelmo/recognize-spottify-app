package com.vgp.recognizeartists.modules.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.vgp.recognizeartists.global.callbacks.ApiCallBack;
import com.vgp.recognizeartists.global.enums.ImageFrom;
import com.vgp.recognizeartists.global.enums.ServerError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {

    private static final String URL_SERVER = "";
    private Context mContext;
    private ImageFrom mImageFrom;
    private String mImagePath;
    private int mImageId;

    public ApiService(Context context, ImageFrom imageFrom, String imgPath) {
        this.mContext = context;
        this.mImageFrom = imageFrom;
        this.mImagePath = imgPath;
    }

    public ApiService(Context context, ImageFrom imageFrom, int imgId) {
        this.mContext = context;
        this.mImageFrom = imageFrom;
        this.mImageId = imgId;
    }

    public void sendImgToApi(ApiCallBack callBack) {

        String base64 = convertToBase64();

        if (base64 == null) {
            callBack.error(ServerError.Base64Error);
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("img", base64)
                .build();

        Request request = new Request.Builder()
                .url(URL_SERVER)
                .post(formBody)
                .build();

        OkHttpClient eagerClient = client.newBuilder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();

        eagerClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("erro",e.getMessage());
                callBack.error(ServerError.Base64Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.code() == 200) {
                        try {
                            ArtistModel artistModel = new ArtistModel();
                            String myResponse = response.body().string();
                            JSONObject json = new JSONObject(myResponse);

                            if (json.has("artistName")) {
                                String name = json.getString("artistName");
                                artistModel.setName(name);
                            }else {
                                callBack.error(ServerError.ArtistNotRecognized);
                                return;
                            }

                            if (json.has("playListKey")){
                                String playListKey = json.getString("playListKey");
                                artistModel.setPlayListKey(playListKey);
                            }

                            if (json.has("artistBiography")) {
                                String biography = json.getString("artistBiography");
                                artistModel.setBiography(biography);
                            }

                            if (json.has("personalInfos")) {
                                JSONArray jsonArrayPersonalInfos = json.getJSONArray("personalInfos");
                                if (jsonArrayPersonalInfos.length() > 0) {
                                    JSONObject object = jsonArrayPersonalInfos.getJSONObject(0);

                                    if (object.has("net_worth")) {
                                        int worth = object.getInt("net_worth");
                                        artistModel.setWorth(worth);
                                    }

                                    if (object.has("age")) {
                                        int age = object.getInt("age");
                                        artistModel.setAge(age);
                                    }

                                    if (object.has("height")) {
                                        double height = object.getDouble("height");
                                        artistModel.setHeight(height);
                                    }

                                    if (object.has("birthday")) {
                                        String birth = object.getString("birthday");
                                        artistModel.setBirth(birth);

                                    }
                                }
                            }

                            callBack.success(artistModel);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("erro",e.getMessage());
                            callBack.error(ServerError.UnknowError);
                        }
                    } else {
                        Log.e("erro","artista desconhecido");
                        callBack.error(ServerError.ArtistNotRecognized);
                    }
                } else {
                    Log.e("errores",response.message());
                    callBack.error(ServerError.ArtistNotRecognized);
                }
            }
        });
    }

    private String convertToBase64() {
        try {
            Bitmap bitmap = null;
            if (mImageFrom == ImageFrom.Bitmap) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), mImageId);
            }else {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.fromFile(new File(mImagePath)));
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] bytesData = stream.toByteArray();
            String base64 = Base64.encodeToString(bytesData, Base64.DEFAULT);
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
