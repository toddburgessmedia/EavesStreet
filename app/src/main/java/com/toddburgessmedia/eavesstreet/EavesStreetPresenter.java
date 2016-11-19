package com.toddburgessmedia.eavesstreet;

import com.toddburgessmedia.eavesstreet.retrofit.EAProfile;
import com.toddburgessmedia.eavesstreet.retrofit.EAProfileAPI;
import com.toddburgessmedia.eavesstreet.retrofit.EAProfileData;

import java.util.StringTokenizer;

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
    EavesStreetIntentService service;

    String errorMsg = "";

    public EavesStreetPresenter(String client, String access_token, EavesStreetView view) {

        this.client = client;
        this.access_token = access_token;
        this.view = view;
    }

    public EavesStreetPresenter(String client, String access_token, EavesStreetIntentService service
                                ) {

        this.client = client;
        this.access_token = access_token;
        this.service = service;
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

                if (!errorMsg.equals("")) {
                    if (view != null) {
                        view.onError(errorMsg);
                    } else {
                        service.onError(errorMsg);
                    }
                    return;
                }

                if (view != null) {
                    view.update();
                } else {
                    service.update(profile);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (view != null) {
                    view.onError("Authentication Error");
                } else {
                    service.onError("network");
                }

            }

            @Override
            public void onNext(Response<EAProfileData> eaProfileDataResponse) {

                if (eaProfileDataResponse.code() == 200) {
                    EAProfileData data;
                    data = eaProfileDataResponse.body();
                    profile = data.getProfile();

                } else {
                    errorMsg = getErrorMessage(eaProfileDataResponse.raw().toString());
                    onError(new Throwable(errorMsg));
                }
            }
        });
    }

    private void createRetrofit() {

//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl("https://api.empire.kred")
                .build();
    }

    private String getErrorMessage (String body)  {

        StringTokenizer st = new StringTokenizer(body, ",");
        String token;
        String error = "";

        while (st.hasMoreElements()) {
            token = st.nextToken();
            if (token.startsWith(" message=")) {
                error = token.substring(token.indexOf('=')+1);
                break;
            }
        }

        return error;
    }


    public interface EavesStreetView {

        public void update();

        public void onError(String message);
    }
}
