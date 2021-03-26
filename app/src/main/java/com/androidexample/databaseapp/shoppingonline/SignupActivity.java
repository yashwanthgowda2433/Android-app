package com.androidexample.databaseapp.shoppingonline;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static android.view.View.GONE;

public class SignupActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    private Button login, signup;
    ProgressBar progressBar;
    private EditText user_name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        user_name = (EditText) findViewById(R.id.user_name);
        String editxt = user_name.getText().toString().trim();

//        Toast.makeText(this, "word:"+editxt, Toast.LENGTH_SHORT).show();
        final Pattern keyPattern = Pattern.compile("[0-9a-zA-Z,._]+");

        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Toast.makeText(SignupActivity.this, "User Name:" + s, Toast.LENGTH_SHORT).show();
                String username = s.toString().trim();
                HashMap<String, String> params = new HashMap<>();
                params.put("name", username);

                PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CECK_USERNAME, params, CODE_POST_REQUEST);
                request.execute();

                if (!keyPattern.matcher(user_name.getText()).matches()) {
                    Toast.makeText(SignupActivity.this, "Your Message:", Toast.LENGTH_SHORT).show();
                }

            }
        });

        email = (EditText) findViewById(R.id.user_email);
        password = (EditText) findViewById(R.id.user_password);

        signup = (Button) findViewById(R.id.main_signup_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        login = (Button) findViewById(R.id.main_login_now_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void create() {

        String u_name = user_name.getText().toString().trim();
        String u_email = email.getText().toString().trim();
        String u_password = password.getText().toString().trim();

        if (TextUtils.isEmpty(u_name)) {
            user_name.setError("Please enter User name");
            user_name.requestFocus();
            return;
        }
        if (!u_name.matches("[a-zA-Z0-9.? ]*")) {

            user_name.setError("Allowed only Alphabets and Numbers");
            user_name.requestFocus();
            return;
        }
        if (u_name.length() < 5) {

            user_name.setError("Minimum 5 characters");
            user_name.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(u_email)) {
            email.setError("Please enter Email");
            email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(u_password)) {
            password.setError("Please enter Password");
            password.requestFocus();
            return;
        }
        if (u_password.length() < 7) {

            password.setError("Minimum 8 characters");
            password.requestFocus();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("name", u_name);
        params.put("email", u_email);
        params.put("password", u_password);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_ACCOUNT, params, CODE_POST_REQUEST);
        request.execute();

    }
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //progressBar.setVisibility(GONE);
            try {
                JSONObject object = new JSONObject(s);
                String avail = "Username not available";
                String noavail = "Username available";
                String noemail = "Email Already";

                if(object.getString("message").equals(avail))
                {
                    Toast.makeText(getApplicationContext(), "User name available", Toast.LENGTH_SHORT).show();


                }
                else if(object.getString("message").equals(noavail)) {
                    Toast.makeText(getApplicationContext(), "User name Not Available", Toast.LENGTH_SHORT).show();
                    user_name.setError("User name Not Available");
                    user_name.requestFocus();
                }
                else if(object.getString("message").equals(noemail)) {
                    Toast.makeText(getApplicationContext(), "User name Not Available", Toast.LENGTH_SHORT).show();
                    email.setError("Email Already Registered");
                    email.requestFocus();
                }
                else {
                    if (!object.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                         startActivity(intent);
                    }

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }


}