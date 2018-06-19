package patel.priyanka.wanderful.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>{

    private final Context context;
    private final MainAdapterOnClickHandler clickHandler;
    private List<MainModel> groupList;
    private DatabaseReference databaseReference;
    private String groupName;

    public interface MainAdapterOnClickHandler {
        void onClick(MainModel mainModel);
    }


    public MainAdapter(Context context, MainAdapterOnClickHandler clickHandler, ArrayList<MainModel> list) {
        this.context = context;
        this.clickHandler = clickHandler;
        this.groupList = list;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.main_item_layout, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        MainModel mainModel = groupList.get(position);

        holder.imageGroupIcon.setImageResource(R.drawable.com_facebook_button_icon_blue);
        holder.textViewGroupName.setText(groupName);
        holder.textViewTravelPlace.setText(mainModel.getGroupPlace());
        holder.textViewTravelDate.setText(mainModel.getGroupDate());
    }

    @Override
    public int getItemCount() {
        if (groupList == null)
            return 0;
        return groupList.size();
    }

    public void setGroupList(List<MainModel> mGroupList) {
        groupList = mGroupList;
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_group_icon)
        ImageView imageGroupIcon;
        @BindView(R.id.textview_group_name)
        TextView textViewGroupName;
        @BindView(R.id.textview_travel_place)
        TextView textViewTravelPlace;
        @BindView(R.id.textview_travel_date)
        TextView textViewTravelDate;

        public MainViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            MainModel mainModel = groupList.get(getAdapterPosition());
            //handle click listener
            if (clickHandler != null) {
                clickHandler.onClick(mainModel);
            }
        }
    }
}
