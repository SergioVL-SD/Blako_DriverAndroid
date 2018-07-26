/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blako.mensajero.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.blako.mensajero.App;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.Utils.AppPreferences;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.Views.BkoTicketActivity;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    private AppPreferences preferences;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences= App.getInstance().getPreferences();
        try {
            String refreshedToken = preferences.getFirebaseToken();
            BkoUser user = BkoUserDao.Consultar(RegistrationIntentService.this);

            if (user != null) {
                Map<String, String> mapVisible = new HashMap<String, String>();
                mapVisible.put("workerId", user.getWorkerId());
                mapVisible.put("token", refreshedToken);
                String updateFirebaseToken = HttpRequest.get(Constants.GET_UPDATE_TOKEN(RegistrationIntentService.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();

                if (updateFirebaseToken != null) {

                }
            }


            BkoDataMaganer.setSendedFirebaseToken(this, true);
        } catch (Exception e) {

        }

    }

    /**
     * Persist registration to third-party servers.
     * <p>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
