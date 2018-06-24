package patel.priyanka.wanderful.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;
import patel.priyanka.wanderful.model.MainAdapter;
import patel.priyanka.wanderful.model.MainModel;
import patel.priyanka.wanderful.widget.GroupsAppWidget;

import static com.google.android.gms.auth.api.credentials.CredentialPickerConfig.Prompt.SIGN_IN;

public class MainActivity extends AppCompatActivity implements MainAdapter.MainAdapterOnClickHandler {

    @BindView(R.id.recyclerView_main_activity)
    RecyclerView recyclerView;
    @BindView(R.id.fab_create_group)
    FloatingActionButton fab;
    @BindView(R.id.adView)
    AdView adView;

    private static final String SCROLL_POSITION = "SCROLL_POSITION";
    Parcelable listState;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Initialize the Mobile Ads SDK
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getLayoutManager().onRestoreInstanceState(listState);

        signingIn();
        clickFab();


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        displayMainActivity();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "getDynamicLink:onFailure", e);
                    }
                });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SCROLL_POSITION, listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listState = savedInstanceState.getParcelable(SCROLL_POSITION);
    }

    public void signingIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(),
                    SIGN_IN
            );
        } else {
            //User is already signed in. Display main activity.
            displayMainActivity();
        }
    }

    private void displayMainActivity() {
        databaseReference.child("group").orderByChild("groupId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<MainModel> groupList = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            groupList.add(ds.getValue(MainModel.class));
                        }
                        MainAdapter adapter = new
                                MainAdapter(getApplicationContext(), groupList, MainActivity.this);
                        adapter.setGroupList(groupList);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),
                                R.string.error_loading_group,
                                Toast.LENGTH_LONG).show();

                    }
                });
    }

    private void clickFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateGroupActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(MainModel mainModel) {
        sendBroadcast();
        Intent intent = new Intent(this, GroupDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("groupId", mainModel.getGroupId());
        intent.putExtra("data", bundle);
        startActivity(intent);
    }

    private void sendBroadcast() {
        Intent intent = new Intent(this, GroupsAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(this, GroupsAppWidget.class));
        widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.list_view_widget);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids);
        sendBroadcast(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.signed_in,
                        Toast.LENGTH_LONG).show();
                displayMainActivity();
            }
        } else {
            Toast.makeText(this,
                    R.string.error_signing_in,
                    Toast.LENGTH_LONG).show();

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, R.string.signed_out, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
        return true;
    }
}

