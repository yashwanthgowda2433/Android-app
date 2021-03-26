package com.androidexample.databaseapp.shoppingonline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ProgressBar progressBar;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //ListView listView;
    List<Hero> heroList;
    ViewPager2 videosViewPager;
    SharedPreferences prf;
    private  ImageView camera;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;

    boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videosViewPager = findViewById(R.id.viewPager);

        Paper.init(this);

        heroList = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //listView = (ListView) findViewById(R.id.listViewHeroes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        camera = (ImageView) findViewById(R.id.camera);

        camera.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PortrateActivity.class);
            startActivity(intent);

        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView userImageView = headerView.findViewById(R.id.user_profile_image);


        prf = getSharedPreferences("user_details",MODE_PRIVATE);

        if(prf.getString("username",null) != null) {
            userNameTextView.setText(prf.getString("username", null));
            Picasso.get().load("http://3dcopilot.com/"+prf.getString("avatar",null)).placeholder(R.drawable.profile).into(userImageView);
            userNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }else {

            userNameTextView.setText("login/signup");

            userNameTextView.setTextColor(getResources().getColor(R.color.blue));
            userNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);



        readHeroes();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }


    private void readHeroes() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_HEROES, null, CODE_GET_REQUEST);
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
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshHeroList(object.getJSONArray("heroes"));
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

    private void refreshHeroList(JSONArray heroes) throws JSONException {
        heroList.clear();
        List<VideoItem> videoItems = new ArrayList<>();


        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);

            heroList.add(new Hero(
                    obj.getInt("id"),
                    obj.getString("name"),
                    obj.getString("realname"),
                    obj.getString("rating")
            ));

            VideoItem videoItemCelebration = new VideoItem();
            videoItemCelebration.videoUrl = "http://3dcopilot.com/"+obj.getString("realname");
            videoItemCelebration.videoTitle = obj.getString("username");
            videoItemCelebration.videoDescription = obj.getString("name");
            videoItemCelebration.userProfile = obj.getString("rating");
            videoItems.add(videoItemCelebration);
        }

        videosViewPager.setAdapter(new VideosAdapter(videoItems));

//        HeroAdapter adapter = new HeroAdapter(heroList);
//        videosViewPager.setAdapter(adapter);

    }

    class HeroAdapter extends ArrayAdapter<Hero> {
        List<Hero> heroList;

        public HeroAdapter(List<Hero> heroList) {
            super(MainActivity.this, R.layout.product_items_layout, heroList);
            this.heroList = heroList;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.product_items_layout, null, true);
            ImageView imageView=listViewItem.findViewById(R.id.product_image);

            TextView textViewName = listViewItem.findViewById(R.id.product_name);

            ImageView imageUser = listViewItem.findViewById(R.id.product_user);
            TextView textViewDelete = listViewItem.findViewById(R.id.product_description);

            final Hero hero = heroList.get(position);

            textViewName.setText(hero.getName());
            Picasso.get().load("http://3dcopilot.com/"+hero.getRealname()).into(imageView);
            Picasso.get().load("http://3dcopilot.com/"+hero.getRating()).into(imageUser);



            return listViewItem;
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if(id == R.id.nav_view)
//        {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.nav_cart)
        {

        }
        else if(id == R.id.nav_orders)
        {

        }
        else if(id == R.id.nav_categories)
        {

        }
        else if(id == R.id.nav_settings)
        {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);

        }
        else if(id == R.id.nav_logout)
        {
            SharedPreferences.Editor editor = prf.edit();
            editor.clear();
            editor.commit();
            Paper.book().destroy();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // request camera permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "camera permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
