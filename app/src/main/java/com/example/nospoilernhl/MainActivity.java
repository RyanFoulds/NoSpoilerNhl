package com.example.nospoilernhl;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.nospoilernhl.ui.teamselector.TeamSelectorFragment;
import com.example.nospoilernhl.ui.teamselector.TeamSelectorViewModel;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private CastContext castContext;
    private CastSession castSession;
    private SessionManager sessionManager;
    private final SessionManagerListener<CastSession> sessionManagerListener = new MySessionManagerListener();

    private MutableLiveData<CastSession> castSessionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_selector)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        castContext = CastContext.getSharedInstance(this);
        sessionManager = castContext.getSessionManager();

        castSessionReference = new ViewModelProvider(this).get(TeamSelectorViewModel.class)
                                                          .getCurrentCastSession();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sessionManager.removeSessionManagerListener(sessionManagerListener, CastSession.class);
        castSession = null;
        castSessionReference.postValue(null);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        castSession = sessionManager.getCurrentCastSession();
        sessionManager.addSessionManagerListener(sessionManagerListener, CastSession.class);
        castSessionReference.postValue(castSession);
    }

    private class MySessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionEnded(final CastSession session, final int error) {
            if (session == castSession) {
                castSession = null;
                castSessionReference.postValue(null);
            }
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(final CastSession session, final boolean wasSuspended) {
            castSession = session;
            castSessionReference.postValue(castSession);
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(final CastSession session, final String sessionId) {
            castSession = session;
            castSessionReference.postValue(castSession);
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarting(final CastSession session) {}

        @Override
        public void onSessionStartFailed(final CastSession session, final int error) {}

        @Override
        public void onSessionEnding(final CastSession session) {}

        @Override
        public void onSessionResuming(final CastSession session, final String sessionId) {}

        @Override
        public void onSessionResumeFailed(final CastSession session, final int error) {}

        @Override
        public void onSessionSuspended(final CastSession session, final int reason) {}
    }
}
