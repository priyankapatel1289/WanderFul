package patel.priyanka.wanderful.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;
import patel.priyanka.wanderful.model.MainAdapter;
import patel.priyanka.wanderful.model.MainModel;

import static com.google.android.gms.auth.api.credentials.CredentialPickerConfig.Prompt.SIGN_IN;

public class MainActivity extends AppCompatActivity implements MainAdapter.MainAdapterOnClickHandler {

    @BindView(R.id.recyclerView_main_activity)
    RecyclerView recyclerView;
    @BindView(R.id.fab_create_group)
    FloatingActionButton fab;
    @BindView(R.id.adView)
    AdView adView;

    private DatabaseReference databaseReference;
    private String groupId;

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

        signingIn();
        clickFab();
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
                                "Error trying to get group for",
                                Toast.LENGTH_LONG).show();

                    }
                });
    }

//    public void setGroupList(ArrayList<MainModel> list) {
//        this.groupList = list;
//    }

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
    public void onClick(int groupId) {
        MainModel mainModel = new MainModel();
        Intent intent = new Intent(MainActivity.this, GroupDetailActivity.class);
        intent.putExtra("groupId", mainModel.getGroupId());
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in. Welcome back!",
                        Toast.LENGTH_LONG).show();
                displayMainActivity();
            }
        } else {
            Toast.makeText(this,
                    "We could not sign you in. Please try again later.",
                    Toast.LENGTH_LONG).show();

            finish();
        }
    }
}

