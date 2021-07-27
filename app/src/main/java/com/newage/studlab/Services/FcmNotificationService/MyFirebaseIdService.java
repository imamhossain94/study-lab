package com.newage.studlab.Services.FcmNotificationService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.newage.studlab.Model.TokenModel.DeviceToken;

import static com.newage.studlab.Authentication.Verification.SignUpIdentification.user_id;

public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser!=null){
            updateToken(refreshToken);
        }
    }
    private void updateToken(String refreshToken){

        DeviceToken token = new DeviceToken(user_id,refreshToken);
        FirebaseDatabase.getInstance().getReference("/Tokens/"+user_id).setValue(token);
    }
}
