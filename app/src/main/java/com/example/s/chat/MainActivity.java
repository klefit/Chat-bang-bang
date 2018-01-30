package com.example.s.chat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {

    static final String APP_ID = "65906";
    static final String AUTH_KEY = "CDcptrmG3r23C7b";
    static final String AUTH_SECRET = "KxZNRH6NTLV44sr";
    static final String ACCOUNT_KEY = "mazDGyCW_x8V3qsR2vzp";

    ImageButton btnLogin, btnRegister;
    EditText userText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFramework();

        if (isNetworkAvailable() != true)
        {
            startActivity(new Intent(MainActivity.this,NoInternetActivity.class));
        }
        else
        {
            btnLogin = (ImageButton)findViewById(R.id.loginbutton);
            btnRegister = (ImageButton)findViewById(R.id.registerbutton);
            userText = (EditText)findViewById(R.id.logintext);
            passwordText = (EditText)findViewById(R.id.passwordtext);

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,SingUpActivity.class));
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String user = userText.getText().toString();
                    final String password = passwordText.getText().toString();

                    //logowanie do quickblox
                    QBUser qbUser = new QBUser(user,password);
                    QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            Toast.makeText(getBaseContext(),"Succesful login",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, ChatDialogsActivity.class);
                            intent.putExtra("user",user);
                            intent.putExtra("password",password);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(getBaseContext(),"" + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });            }
            });
            }
    }

    public void close(){
        finish();
    }

    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void refresh()
    {
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
        //NoInternetActivity.destroy();
    }


}
