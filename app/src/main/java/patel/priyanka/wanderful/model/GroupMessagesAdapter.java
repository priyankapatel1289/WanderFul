package patel.priyanka.wanderful.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import patel.priyanka.wanderful.R;

public class GroupMessagesAdapter extends RecyclerView.Adapter<GroupMessagesAdapter.GroupMessagesViewHolder>{

    private Context context;
    private List<GroupMessagesModel> groupMessagesList;

    public GroupMessagesAdapter(Context context, ArrayList<GroupMessagesModel> groupMessagesList) {
        this.context = context;
        this.groupMessagesList = groupMessagesList;
    }

    @NonNull
    @Override
    public GroupMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.detail_item_layout, parent, false);
        return new GroupMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessagesViewHolder holder, int position) {
        GroupMessagesModel groupMessagesModel = groupMessagesList.get(position);

        if (position %2 == 1) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.recyclerView1));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.recyclerView2));
        }

        holder.textView_user.setText(groupMessagesModel.getMessageUser());
        holder.textView_message.setText(groupMessagesModel.getMessageText());
        holder.textView_message_time.setText(DateFormat.format("MM-dd-yy (HH:mm)",
                groupMessagesModel.getMessageTime()));
    }

    @Override
    public int getItemCount() {
        if (groupMessagesList == null)
            return 0;
        return groupMessagesList.size();
    }

    public void setGroupMessagesList(List<GroupMessagesModel> mGroupList) {
        groupMessagesList = mGroupList;
        notifyDataSetChanged();
    }

    class GroupMessagesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_user)
        TextView textView_user;
        @BindView(R.id.text_view_message_time)
        TextView textView_message_time;
        @BindView(R.id.text_view_message)
        TextView textView_message;

        public GroupMessagesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
