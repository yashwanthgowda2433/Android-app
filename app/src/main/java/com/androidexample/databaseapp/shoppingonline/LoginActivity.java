package com.androidexample.databaseapp.shoppingonline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private Button joinnow, login;
    private EditText name, password;
    private CheckBox chkBoxRememberMe;
    List<Hero> userList;
    ListView listView;
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        joinnow = (Button) findViewById(R.id.main_join_now_btn);
        joinnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        pref = getSharedPreferences("user_details",MODE_PRIVATE);

        name = (EditText) findViewById(R.id.user_number);
        password = (EditText) findViewById(R.id.user_password);

        login = (Button) findViewById(R.id.main_login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });
    }

    private void verify() {
        String u_name = name.getText().toString().trim();
        String u_password = password.getText().toString().trim();

        if(TextUtils.isEmpty(u_name))
        {
            name.setError("Please Enter Username/Password");
            name.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(u_password))
        {
            password.setError("Please Enter Password");
            password.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("name", u_name);
        params.put("password", u_password);

        LoginActivity.PerformNetworkRequest request = new LoginActivity.PerformNetworkRequest(Api.URL_Login_ACCOUNT, params, CODE_POST_REQUEST);
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
            //Toast.makeText(LoginActivity.this, "1", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //progressBar.setVisibility(GONE);
            try {
                //Toast.makeText(LoginActivity.this, "2", Toast.LENGTH_SHORT).show();
                JSONObject object = new JSONObject(s);
                String avail = "Username not available";
                String noavail = "Username available";
                String noemail = "Email Already";

                if(object.getString("message").equals(avail)) {
                    Toast.makeText(getApplicationContext(), "Username/Email Wrong", Toast.LENGTH_SHORT).show();
                    name.setError("Wrong Username/Email");
                    name.requestFocus();
                    password.setError("Wrong Password");
                    password.requestFocus();
                }
                else {
                    //if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "exmap"+object.getJSONArray("users"), Toast.LENGTH_SHORT).show();


                    //Toast.makeText(LoginActivity.this, "3", Toast.LENGTH_SHORT).show();
                        refreshHeroList(object.getJSONArray("users"));


                    //}

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

    private void refreshHeroList(JSONArray users) throws JSONException {
        //Toast.makeText(LoginActivity.this, "4", Toast.LENGTH_SHORT).show();
       for (int i = 0; i <= 1; i++) {
            JSONObject obj = users.getJSONObject(i);

           SharedPreferences.Editor editor = pref.edit();
           editor.putString("userid",obj.getString("User_id"));
           editor.putString("username",obj.getString("User_name"));
           editor.putString("useremail",obj.getString("User_email"));
           editor.putString("password",obj.getString("Password"));
           editor.putString("avatar",obj.getString("Avatar"));

           editor.commit();


           //Toast.makeText(LoginActivity.this, "id"+obj.getInt("User_id"), Toast.LENGTH_SHORT).show();


           Intent intent = new Intent(LoginActivity.this, MainActivity.class);
           startActivity(intent);

        }


    }

}
