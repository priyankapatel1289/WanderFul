package patel.priyanka.wanderful.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;
import patel.priyanka.wanderful.model.GroupMessagesAdapter;
import patel.priyanka.wanderful.model.GroupMessagesModel;
import patel.priyanka.wanderful.model.MainModel;

public class GroupDetailActivity extends AppCompatActivity {

    @BindView(R.id.fab_group_chat)
    FloatingActionButton fab;
    @BindView(R.id.edit_text_group_chat)
    EditText editText_group_chat;
    @BindView(R.id.recyclerView_group_chat)
    RecyclerView recyclerView_group_chat;

    private DatabaseReference dbRef;
    private ArrayList<GroupMessagesModel> groupMessageList = new ArrayList<>();
    private int nextMessageId;
    private FirebaseAuth firebaseAuth;

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
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView_group_chat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView_group_chat.setLayoutManager(layoutManager);

        displayGroupChat();
        fabListener();
    }

    private void fabListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });
    }

//    private String getGroupId() {
//        dbRef.child("")
//    }

    private void displayGroupChat() {
        MainModel mainModel = new MainModel();
//        Query messageById =
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                messageById.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        ArrayList<GroupMessagesModel> groupMessagesList = new ArrayList<>();
//                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                            groupMessagesList.add(ds.getValue(GroupMessagesModel.class));
//                        }
////                        GroupMessagesModel groupMessagesModel = dataSnapshot.getValue(GroupMessagesModel.class);
////                        groupMessageList.add(groupMessagesModel);
//                        GroupMessagesAdapter groupMessagesAdapter = new
//                                GroupMessagesAdapter(getApplicationContext(), groupMessagesList);
//                        groupMessagesAdapter.setGroupMessagesList(groupMessagesList);
//                        recyclerView_group_chat.setAdapter(groupMessagesAdapter);
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

    }

    private void addGroup() {
        GroupMessagesModel groupMessagesModel = createGroupObj();
        addMessagesToDB(groupMessagesModel);

    }

    private GroupMessagesModel createGroupObj() {
        final GroupMessagesModel groupMessagesModel = new GroupMessagesModel();

        groupMessagesModel.setMessageText(editText_group_chat.getText().toString());
        groupMessagesModel.setMessageUser(firebaseAuth.getUid());

        return groupMessagesModel;
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
                    Toast.makeText(getApplicationContext(), "There was a problem creating group, " +
                            "please create group again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addNewMessages(GroupMessagesModel groupMessagesModel, String messageId) {
        groupMessagesModel.setMessageId(messageId);
        dbRef.child("messages").child("1").child(messageId)
                .setValue(groupMessagesModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            restUi();
//                            Toast.makeText(getApplicationContext(), "New group has been created",
//                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Message could not be added",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void restUi() {
        editText_group_chat.setText("");
    }

}
