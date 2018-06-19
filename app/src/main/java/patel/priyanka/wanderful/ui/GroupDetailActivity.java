package patel.priyanka.wanderful.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    private DatabaseReference databaseReference;
    private ArrayList<GroupMessagesModel> groupMessageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);

        recyclerView_group_chat.setHasFixedSize(true);
        recyclerView_group_chat.setLayoutManager(new LinearLayoutManager(this));
        GroupMessagesAdapter groupMessagesAdapter = new GroupMessagesAdapter(this);
        recyclerView_group_chat.setAdapter(groupMessagesAdapter);
        groupMessagesAdapter.setGroupMessagesList(groupMessageList);

        displayGroupChat();
        fabListener();
    }

    private void fabListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = FirebaseDatabase.getInstance().getReference();

                GroupMessagesModel groupMessagesModel = new GroupMessagesModel();

                groupMessagesModel.setMessageText(editText_group_chat.getText().toString());

                databaseReference.child("groupChat").child("chat").setValue(groupMessagesModel);
                editText_group_chat.setText("");
            }
        });
    }

    private void displayGroupChat() {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("groupChat").child("chat").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupMessagesModel groupMessagesModel = dataSnapshot.getValue(GroupMessagesModel.class);
                        groupMessageList.add(groupMessagesModel);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
