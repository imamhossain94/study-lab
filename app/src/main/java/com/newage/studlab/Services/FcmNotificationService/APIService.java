package com.newage.studlab.Services.FcmNotificationService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAjV2cByw:APA91bGCd0_RV42uuWKKj8XmvWmyr3wxnULoeyM65Lylq1DH5Chnwivos0uoUrSaD-IFZnlPm8tVjGmkqActfRbGVPtLJNy9Iet0A9g9X07wCMlWfEsSOQUsjzpM3MuOJ-FZhXMEP_NQ" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

