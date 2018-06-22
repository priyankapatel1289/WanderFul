package patel.priyanka.wanderful.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>{

    private final Context context;
    private final MainAdapterOnClickHandler clickHandler;
    private List<MainModel> groupList;

    public interface MainAdapterOnClickHandler {
        void onClick(int groupId);
    }


    public MainAdapter(Context context, ArrayList<MainModel> list, MainAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.groupList = list;
        this.clickHandler = clickHandler;
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

        if (position %2 == 1) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.recyclerView1));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.recyclerView2));
        }

        try {
            byte[] encodeByte = Base64.decode(mainModel.getGroupIcon(), Base64.DEFAULT);
            Bitmap groupIcon = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            holder.imageGroupIcon.setImageBitmap(groupIcon);
        } catch (Exception e) {
            e.getMessage();
        }

        holder.textViewGroupName.setText(mainModel.getGroupName());
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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            clickHandler.onClick(adapterPosition);

        }
    }
}
