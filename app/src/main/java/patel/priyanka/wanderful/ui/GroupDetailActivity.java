package patel.priyanka.wanderful.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;
import patel.priyanka.wanderful.model.GroupMessagesAdapter;
import patel.priyanka.wanderful.model.GroupMessagesModel;

public class GroupDetailActivity extends AppCompatActivity {

    @BindView(R.id.fab_group_chat)
    FloatingActionButton fab;
    @BindView(R.id.edit_text_group_chat)
    EditText editText_group_chat;
    @BindView(R.id.recyclerView_group_chat)
    RecyclerView recyclerView_group_chat;

    public static String WIDGET_MESSAGES_SHAREDPREF = "widget_messages_list";
    private static final String SCROLL_POSITION = "SCROLL_POSITION";
    private DatabaseReference dbRef;
    private int nextMessageId;
    private FirebaseUser user;
    Parcelable listState;

    private String groupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra("groupId")) {
            groupId = intent.getStringExtra("groupId");
        }

        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView_group_chat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView_group_chat.setLayoutManager(layoutManager);
        recyclerView_group_chat.getLayoutManager().onRestoreInstanceState(listState);

        displayGroupChat();
        fabListener();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listState = savedInstanceState.getParcelable(SCROLL_POSITION);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = recyclerView_group_chat.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SCROLL_POSITION, listState);
    }

    private void fabListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });
    }

    private void displayGroupChat() {
                dbRef.child("messages").child("1").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<GroupMessagesModel> groupMessagesList = new ArrayList<>();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            groupMessagesList.add(ds.getValue(GroupMessagesModel.class));
                        }
                        GroupMessagesAdapter groupMessagesAdapter = new
                                GroupMessagesAdapter(getApplicationContext(), groupMessagesList);
                        groupMessagesAdapter.setGroupMessagesList(groupMessagesList);
                        recyclerView_group_chat.setAdapter(groupMessagesAdapter);

                        Gson gson = new Gson();
                        String jsonMessages = gson.toJson(groupMessagesList);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(WIDGET_MESSAGES_SHAREDPREF, jsonMessages);
                        editor.apply();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void addGroup() {
        GroupMessagesModel groupMessagesModel = createGroupObj();
        addMessagesToDB(groupMessagesModel);

    }

    private GroupMessagesModel createGroupObj() {
        final GroupMessagesModel groupMessagesModel = new GroupMessagesModel();

        String message = editText_group_chat.getText().toString();

        //Check if a new message has been entered otherwise show Toast without adding to database.
        if (!message.equals("")) {
            groupMessagesModel.setMessageText(message);
            String message_time = new SimpleDateFormat("MM-dd-yy HH:mm a").format((Calendar.getInstance().getTime()));
            groupMessagesModel.setMessageTime(message_time);
            if (user.getDisplayName() != null) {
                groupMessagesModel.setMessageUser(user.getDisplayName());
            } else {
                groupMessagesModel.setMessageUser(user.getEmail());
            }
            return groupMessagesModel;
        } else {
            Toast.makeText(getApplicationContext(), R.string.enter_message, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void addMessagesToDB(final GroupMessagesModel groupMessagesModel) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("MessagesIDs").child("id");

        databaseReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                if (mutableData.getValue(int.class) == null) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() == null) {
                                databaseReference.setValue(1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    return Transaction.abort();
                }
                nextMessageId = mutableData.getValue(int.class);
                mutableData.setValue(nextMessageId + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean state,
                                   @Nullable DataSnapshot dataSnapshot) {
                if (state) {
                    addNewMessages(groupMessagesModel, ""+nextMessageId);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addNewMessages(GroupMessagesModel groupMessagesModel, String messageId) {
        if (groupMessagesModel != null) {
            groupMessagesModel.setMessageId(messageId);
            dbRef.child("messages").child("1").child(messageId)
                    .setValue(groupMessagesModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                restUi();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.error_adding_message,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private void restUi() {
        editText_group_chat.setText("");
    }

}
