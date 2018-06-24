package patel.priyanka.wanderful.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import patel.priyanka.wanderful.R;
import patel.priyanka.wanderful.model.GroupMessagesModel;
import patel.priyanka.wanderful.ui.GroupDetailActivity;

public class ListWidgetServices extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewsFactory(this.getApplicationContext(), intent);
    }

    class  ListViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private ArrayList<GroupMessagesModel> groupMessagesList = new ArrayList<>();
        private Context context;
        private Intent intent;

        public ListViewsFactory(Context applicationContext, Intent intent) {
            this.context = applicationContext;
            this.intent = intent;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String json = sharedPreferences.getString(GroupDetailActivity.WIDGET_MESSAGES_SHAREDPREF, "");
            if (!json.equals("")) {
                Gson gson = new Gson();
                groupMessagesList = gson.fromJson(json, new TypeToken<ArrayList<GroupMessagesModel>>() {
                }.getType());
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (groupMessagesList == null)
                return 0;
            return groupMessagesList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item_layout);
            views.setTextViewText(R.id.widget_text_view_user, groupMessagesList.get(position).getMessageUser());
            views.setTextViewText(R.id.widget_text_view_message, groupMessagesList.get(position).getMessageText());
            views.setTextViewText(R.id.widget_text_view_message_time, groupMessagesList.get(position).getMessageTime());

            Intent fillInIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("message", groupMessagesList.get(position).getMessageText());
            fillInIntent.putExtras(bundle);
            views.setOnClickFillInIntent(R.id.widget_text_view_message, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
