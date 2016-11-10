package com.toddburgessmedia.eavesstreet;

import com.toddburgessmedia.eavesstreet.retrofit.EAProfile;
import com.toddburgessmedia.eavesstreet.retrofit.EAProfileAPI;
import com.toddburgessmedia.eavesstreet.retrofit.EAProfileData;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/11/16.
 */

public class EavesStreetPresenter {

    String client;
    String access_token;

    Retrofit retrofit;
    private EAProfile profile;

    EavesStreetView view;

    public EavesStreetPresenter(String client, String access_token, EavesStreetView view) {

        this.client = client;
        this.access_token = access_token;
        this.view = view;
    }




    public EAProfile getProfile() {
        return profile;
    }

    public void setProfile(EAProfile profile) {
        this.profile = profile;
    }

    public void fetchEAProfile() {

        createRetrofit();

        EAProfileAPI profileAPI = retrofit.create(EAProfileAPI.class);
        Observable<Response<EAProfileData>> call = profileAPI.getProfileInfo(client, access_token);

        call.observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Response<EAProfileData>>() {
            @Override
            public void onCompleted() {
                view.updateView();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Response<EAProfileData> eaProfileDataResponse) {
                EAProfileData data;
                data = eaProfileDataResponse.body();
                profile = data.getProfile();


            }
        });
    }

    private void createRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl("https://api.empire.kred")
                .client(client)
                .build();
    }



    public interface EavesStreetView {

        public void updateView();
    }
}
