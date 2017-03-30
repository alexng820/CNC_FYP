package fyp.cnc.cnc_fyp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import fyp.cnc.cnc_fyp.R;
import fyp.cnc.cnc_fyp.helper.SQLiteHandler;
import fyp.cnc.cnc_fyp.helper.SessionManager;

public class NavBarTemplate extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String URL_NEWS = "http://www.ouhk.edu.hk/wcsprd/Satellite?pagename=OUHK/tcPortalPage2014&CCNAME=CCNEWS&YEAR=2017&dis=11&pri=0&LANG=eng";
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbartemplate);

        new News().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //Session manager
        session = new SessionManager(getApplicationContext());

        //Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            logoutUser();
        }
    }

    // Description AsyncTask
    private class News extends AsyncTask<Void, Void, Void> {
        String link;

        @Override
        protected void onPreExecute() {}

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(URL_NEWS).get();
                // Get the link of the news
                link = document.select("a[class=content_title2_link]").first().absUrl("href");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Load and display the web
            WebView content = (WebView) findViewById(R.id.news_container);
            content.loadUrl(link);
            content.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_class_schedule_button) {
            Intent intent = new Intent(this, Class_scheduleActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.drawer_class_news_button) {
            Intent intent = new Intent(this, NavBarTemplate.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.drawer_class_logout_button) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
