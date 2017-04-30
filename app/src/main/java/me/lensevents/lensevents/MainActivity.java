package me.lensevents.lensevents;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import me.lensevents.model.Category;
import me.lensevents.model.Group;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, CategoryFragment.OnListFragmentInteractionListener, GroupFragment.OnListFragmentInteractionListener {

    private static final int RC_SIGN_IN = 123;
    private HomeFragment homeFragment;
    private CategoryFragment categoryFragment;
    private GroupFragment groupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        homeFragment = new HomeFragment();
        categoryFragment = new CategoryFragment();
        transaction.replace(R.id.content_frament_to_replace, homeFragment);
        transaction.commit();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(), new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.navigation_home:
                transaction.replace(R.id.content_frament_to_replace, homeFragment);
                transaction.commit();
                return true;
            case R.id.navigation_events:
                transaction.replace(R.id.content_frament_to_replace, new Fragment());
                transaction.commit();
                return true;
            case R.id.navigation_groups:
                transaction.replace(R.id.content_frament_to_replace, categoryFragment);
                transaction.commit();
                return true;
            case R.id.navigation_calendar:
                return true;
        }
        return false;
    }


    @Override
    public void onListFragmentInteraction(Category category) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        groupFragment = GroupFragment.newInstance(category);
        transaction.replace(R.id.content_frament_to_replace, groupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(Group item) {
        Toast.makeText(getApplicationContext(), item.getName(), Toast.LENGTH_LONG).show();
    }
}
