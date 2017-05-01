package fyp.cnc.cnc_fyp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import fyp.cnc.cnc_fyp.R;
import fyp.cnc.cnc_fyp.helper.SQLiteHandler;
import fyp.cnc.cnc_fyp.helper.SessionManager;

public class PressNewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String URL_NEWS = "http://www.ouhk.edu.hk/wcsprd/Satellite?pagename=OUHK/tcPortalPage2014&CCNAME=CCNEWS&YEAR=2017&dis=11&pri=0&LANG=eng";
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_class_schedule_button) {
            Intent intent = new Intent(this, Class_scheduleActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.drawer_class_news_button) {
            Intent intent = new Intent(this, PressNewsActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.drawer_class_event_button) {
            Intent intent = new Intent(this, Event_ViewerActivity.class);
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

    // Description AsyncTask
    private class News extends AsyncTask<Void, Void, Void> {
        String title[];
        String link[];

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(URL_NEWS).get();
                // Get the link of the news
                Elements div = document.select("a[class=content_title2_link]");
                title = new String[div.size()];
                link = new String[div.size()];
                for (int i = 0; i < div.size(); i++) {
                    title[i] = div.select("a").get(i).text();
                    link[i] = div.get(i).absUrl("href");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Display news list
            LinearLayout layout = (LinearLayout) findViewById(R.id.news_Layout);
            for (int i = 0; i < title.length; i++) {
                //Add news title
                final TextView rowTitle = new TextView(getApplicationContext());
                rowTitle.setText(title[i]);
                rowTitle.setPadding(5, 5, 5, 5);
                rowTitle.setTypeface(null, Typeface.BOLD);
                layout.addView(rowTitle);

                //Add news links
                final TextView rowLink = new TextView(getApplicationContext());
                SpannableString content = new SpannableString(link[i]);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                rowLink.setText(content);
                rowLink.setPadding(5, 5, 5, 30);
                rowLink.setTypeface(null, Typeface.ITALIC);
                rowLink.setTextColor(Color.BLUE);
                rowLink.setOnClickListener(view -> {
                    //Clean the layout
                    layout.removeAllViews();
                    //Get the detail of clicked link
                    TextView textView = (TextView) view;
                    CharSequence linkText = textView.getText();
                    String link1 = linkText.toString();
                    //Load and display the web
                    final WebView newsContainer = new WebView(getApplicationContext());
                    newsContainer.loadUrl(link1);
                    newsContainer.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });
                    newsContainer.scrollTo(650, 0);
                    layout.addView(newsContainer);
                });
                layout.addView(rowLink);
            }
        }
    }
}
