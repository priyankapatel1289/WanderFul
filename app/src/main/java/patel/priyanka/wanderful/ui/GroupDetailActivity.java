package patel.priyanka.wanderful.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;
import patel.priyanka.wanderful.model.GroupMessagesModel;

public class GroupDetailActivity extends AppCompatActivity {

    @BindView(R.id.fab_group_chat)
    FloatingActionButton fab;
    @BindView(R.id.edit_text_group_chat)
    EditText editText_group_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
        fabListener();
    }

    private void fabListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new GroupMessagesModel(editText_group_chat.getText().toString(),
                                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getDisplayName()));
                editText_group_chat.setText("");
            }
        });
    }
}
