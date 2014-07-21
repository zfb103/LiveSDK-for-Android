//------------------------------------------------------------------------------
// Copyright 2014 Microsoft Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//------------------------------------------------------------------------------

package com.microsoft.live.sample;

import java.util.Arrays;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveStatus;

public class SignInActivity extends Activity {
    private LiveSdkSampleApplication mApp;

    private LiveAuthClient mAuthClient;

    private ProgressDialog mInitializeDialog;

    private Button mSignInButton;

    private TextView mBeginTextView;

    private Button mNeedIdButton;

    private TextView mBeginTextViewNeedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        mApp = (LiveSdkSampleApplication)getApplication();
        mAuthClient = new LiveAuthClient(mApp, Config.CLIENT_ID);
        mApp.setAuthClient(mAuthClient);
        mInitializeDialog = ProgressDialog.show(this, "", "Initializing. Please wait...", true);
        
        mBeginTextView = (TextView)findViewById(R.id.beginTextView);
        mSignInButton = (Button)findViewById(R.id.signInButton);
        
        mBeginTextViewNeedId = (TextView)findViewById(R.id.beginTextViewNeedId);
        mNeedIdButton = (Button)findViewById(R.id.needIdButton);

        // Check to see if the CLIENT_ID has been changed. 
        if (Config.CLIENT_ID.equals("0000000048122D4E")) {
        	 mNeedIdButton.setVisibility(View.VISIBLE);
             mBeginTextViewNeedId.setVisibility(View.VISIBLE);
             mNeedIdButton.setOnClickListener(new View.OnClickListener() {

            	   @Override
            	   public void onClick(View view) {
            		   final Intent intent = new Intent(Intent.ACTION_VIEW);
            		   intent.setData(Uri.parse(getBaseContext().getString(R.string.AndroidSignInHelpLink)));
            		   startActivity(intent);
            	   }
            	});
        }

      mSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuthClient.login(SignInActivity.this, Arrays.asList(Config.SCOPES), new LiveAuthListener() {
                        @Override
                        public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
                            if (status == LiveStatus.CONNECTED) {
                                launchMainActivity(session);
                            } else {
                                showToast("Login did not connect. Status is " + status + ".");
                            }
                        }

                        @Override
                        public void onAuthError(LiveAuthException exception, Object userState) {
                            showToast(exception.getMessage());
                        }
                    });
                }
            });
        
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuthClient.initialize(Arrays.asList(Config.SCOPES), new LiveAuthListener() {
            @Override
            public void onAuthError(LiveAuthException exception, Object userState) {
                mInitializeDialog.dismiss();
                showSignIn();
                showToast(exception.getMessage());
            }

            @Override
            public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
                mInitializeDialog.dismiss();

                if (status == LiveStatus.CONNECTED) {
                    launchMainActivity(session);
                } else {
                    showSignIn();
                }
            }
        });
    }

    private void launchMainActivity(LiveConnectSession session) {
        assert session != null;
        mApp.setSession(session);
        mApp.setConnectClient(new LiveConnectClient(session));
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showSignIn() {
        mSignInButton.setVisibility(View.VISIBLE);
        mBeginTextView.setVisibility(View.VISIBLE);
    }
}
