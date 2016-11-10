package com.toddburgessmedia.eavesstreet.retrofit;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/11/16.
 */

public interface EAProfileAPI {

    @GET("/profile/info")
    Observable<Response<EAProfileData>> getProfileInfo(@Query("client_id") String clientId,
                                                       @Query("access_token") String accessToken);

}
