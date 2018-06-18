package patel.priyanka.wanderful.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

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

    private MainActivity mainActivity;
    private static int INTERNAL_CONTENT_URI; //TODO: might need to add value to it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);

        mainActivity = new MainActivity();

        setButton_cancel_group();
        setButton_create_group();
        getImage();
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

    private void setButton_create_group() {
        button_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MainModel> groupList = new ArrayList<>();
                MainModel mainModel = new MainModel();
                mainModel.setGroupName(String.valueOf(editText_group_name.getText()));
                mainModel.setGroupPlace(String.valueOf(editText_travel_place.getText()));
                mainModel.setGroupDate(String.valueOf(editText_travel_date.getText()));
                groupList.add(mainModel);

                mainActivity.setGroupList(groupList);

                Intent intent = new Intent(CreateGroupActivity.this, GroupDetailActivity.class);
                startActivity(intent);
            }
        });
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
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                        imageView_group_icon.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this.getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}
