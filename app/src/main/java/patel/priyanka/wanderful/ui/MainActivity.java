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

    private ArrayList<MainModel> groupList = new ArrayList<>();

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MainAdapter mainAdapter = new MainAdapter(this, this, groupList);
        recyclerView.setAdapter(mainAdapter);
        mainAdapter.setGroupList(groupList);

        databaseReference = FirebaseDatabase.getInstance().getReference();

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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("groupName").child("group").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MainModel mainModel = dataSnapshot.getValue(MainModel.class);
                groupList.add(mainModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setGroupList(ArrayList<MainModel> list) {
        this.groupList = list;
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

