package me.lensevents.lensevents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.Category;
import me.lensevents.model.Event;
import me.lensevents.model.User;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, CategoryFragment.OnListFragmentInteractionListener, GroupFragment.OnListFragmentInteractionListener, GroupDetailsFragment.OnFragmentInteractionListener, UserFragment.OnListFragmentInteractionListener, CreateGroupFragment.OnFragmentInteractionListener, EventFragment.OnFragmentInteractionListener {

    private static final int RC_SIGN_IN = 123;
    private HomeFragment homeFragment;
    private CategoryFragment categoryFragment;
    private GroupFragment groupFragment;
    private EventFragment eventFragment;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                if (firebaseAuth.getCurrentUser() != null) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
                    databaseReference.orderByChild("uid").equalTo(firebaseAuth.getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChildren()) {
                                        saveUserData();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        }
        if (requestCode == CreateGroupFragment.REQUEST_CODE) {
            CreateGroupFragment groupFragment = (CreateGroupFragment) getSupportFragmentManager().findFragmentByTag("groupFragment");
            groupFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveUserData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            User user = new User();
            if (firebaseAuth.getCurrentUser().getDisplayName() != null) {
                user.setDisplayName(firebaseAuth.getCurrentUser().getDisplayName());
            }
            if (firebaseAuth.getCurrentUser().getEmail() != null) {
                user.setEmail(firebaseAuth.getCurrentUser().getEmail());
            }
            if (firebaseAuth.getCurrentUser().getPhotoUrl() != null) {
                user.setPhotoUrl(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
            }
            if (firebaseAuth.getCurrentUser().getUid() != null) {
                user.setUid(firebaseAuth.getCurrentUser().getUid());
            }

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
            databaseReference.push().setValue(user);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        homeFragment = new HomeFragment();
        categoryFragment = new CategoryFragment();
        eventFragment = new EventFragment();
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
                transaction.replace(R.id.content_frament_to_replace, eventFragment);
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
    public void onListFragmentInteraction(GroupDto item, String key) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        GroupDetailsFragment groupDetailsFragment = GroupDetailsFragment.newInstance(item, key);
        transaction.replace(R.id.content_frament_to_replace, groupDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onFragmentInteraction(Event event) {

    }

    @Override
    public void onListFragmentInteraction(User user) {
    }
}
