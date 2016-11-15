package com.toddburgessmedia.eavesstreet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/11/16.
 */

public class EmpireAvenueAuthActivity extends AppCompatActivity {

    String TAG = EavesSteetMain.TAG;

    private final String AUTHORITY = "www.empire.kred";
    private final String TOKEN_URL = "https://api.empire.kred/oauth/token";

    private String CLIENT_ID;

    public static final String REDIRECT_URI = "eavesstreet://appdomain";
    private String CONSUMER_SECRET;

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CLIENT_ID = getString(R.string.clientid);
        CONSUMER_SECRET = getString(R.string.consumer_secret);

        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        webView = new WebView(this);
        webView.clearCache(true);
        webView.clearHistory();
        webView.setWebViewClient(new EAWebViewClient());
        setContentView(webView);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(AUTHORITY)
                .appendPath("profile")
                .appendPath("developer")
                .appendPath("authorize")
                .appendQueryParameter("client_id",CLIENT_ID)
                .appendQueryParameter("response_type","code")
                .appendQueryParameter("state","request_auth_code");

        String url = builder.build().toString();
        Log.d(TAG, "onCreate: URI " + url);

        webView.loadUrl(url);

    }

    private class EAWebViewClient extends WebViewClient {

        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d(TAG, "shouldOverrideUrlLoading: " + url);

            final Intent i = new Intent();

            Uri uri = Uri.parse(url);
            String code = uri.getQueryParameter("code");

            Log.d(TAG, "shouldOverrideUrlLoading: " + code);
            if (code != null) {
                if (!url.startsWith(REDIRECT_URI)) {
                    return false;
                }
                Subscription sub = getAuthTokenObservable(code)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                setResult(RESULT_OK,i);
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(String s) {
                                Log.d(TAG, "onNext: " + s);
                                i.putExtra("access_token", s);
                            }
                        });
                return true;
            }

            return false;
        }


        private String getAuthToken (String code) {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("client_id", CLIENT_ID)
                    .add("grant_type", "authorization_code")
                    .add("state", "test_state")
                    .add("code", code)
                    .add("client_secret", CONSUMER_SECRET)
                    .build();

            Request request = new Request.Builder()
                    .url(TOKEN_URL)
                    .post(body)
                    .build();

            String token = "";
            try {
                Response response = client.newCall(request).execute();
                ResponseBody mybody = response.body();
                JSONObject json = new JSONObject(mybody.string());
                token = json.getString("access_token");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return token;
        }

        Observable<String> getAuthTokenObservable(final String code) {
            return Observable.defer(new Func0<Observable<String>>() {
                @Override
                public Observable<String> call() {
                    return Observable.just(getAuthToken(code));
                }
            });
        }
    }

}
