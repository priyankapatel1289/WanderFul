package patel.priyanka.wanderful.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;
import patel.priyanka.wanderful.model.MainModel;

public class CreateGroupActivity extends AppCompatActivity {


    @BindView(R.id.image_view_group_icon)
    ImageView imageView_group_icon;
    @BindView(R.id.button_add_image)
    Button button_add_image;
    @BindView(R.id.edit_text_group_name)
    EditText editText_group_name;
    @BindView(R.id.edit_text_travel_place)
    EditText editText_travel_place;
    @BindView(R.id.edit_text_travel_date)
    EditText editText_travel_date;
    @BindView(R.id.button_create_group)
    Button button_create_group;
    @BindView(R.id.button_cancel_group_creation)
    Button button_cancel_group;

    private Bitmap groupIcon;
    private int nextGroupId;


    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);

        dbRef = FirebaseDatabase.getInstance().getReference();

        button_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });

        setButton_cancel_group();
        getImage();
    }

    private void addGroup() {
        MainModel mainModel = createGroupObj();
        addGroupToDB(mainModel);
    }

    private MainModel createGroupObj() {
        final MainModel mainModel = new MainModel();

        String groupName = editText_group_name.getText().toString();

        //Check if ar least the group name has been entered otherwise show Toast without adding to database.
        if (!groupName.equals("")){
            //compress bitmap only if the group image is added.
            if (groupIcon != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                groupIcon.compress(Bitmap.CompressFormat.JPEG,0, stream);
                byte[] bytes = stream.toByteArray();
                String groupIconString = Base64.encodeToString(bytes, Base64.DEFAULT);
                mainModel.setGroupIcon(groupIconString);
            }

            mainModel.setGroupName(editText_group_name.getText().toString());
            mainModel.setGroupPlace(editText_travel_place.getText().toString());
            mainModel.setGroupDate(editText_travel_date.getText().toString());
            return mainModel;
        } else {
            Toast.makeText(getApplicationContext(), R.string.enter_group_name, Toast.LENGTH_LONG).show();
            return null;
        }

    }

    private void setButton_cancel_group() {
        button_cancel_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateGroupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addGroupToDB(final MainModel mainModel) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("GroupIDs").child("id");

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
                nextGroupId = mutableData.getValue(int.class);
                mutableData.setValue(nextGroupId + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean state,
                                   @Nullable DataSnapshot dataSnapshot) {
                if (state) {
                    addNewGroup(mainModel, ""+nextGroupId);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_creating_group, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addNewGroup(MainModel mainModel, String groupId) {
        if (mainModel != null) {
            mainModel.setGroupId(groupId);
            dbRef.child("group").child(groupId)
                    .setValue(mainModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                restUi();
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), GroupDetailActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), R.string.new_group_created,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.error_adding_group,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void restUi() {
        editText_group_name.setText("");
        editText_travel_place.setText("");
        editText_travel_date.setText("");

    }


    private void getImage() {
        button_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 10;
                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        groupIcon = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        imageView_group_icon.setImageBitmap(groupIcon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this.getApplicationContext(), R.string.cancelled, Toast.LENGTH_LONG).show();
            }
        }
    }
}
