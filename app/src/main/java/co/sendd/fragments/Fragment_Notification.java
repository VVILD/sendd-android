package co.sendd.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.sendd.R;
import co.sendd.activity.Activity_Main;
import co.sendd.databases.Db_Notifications;
import co.sendd.gettersandsetters.Notification;


public class Fragment_Notification extends Fragment {
    ListView NotificationList;
    Notification_adapter mAdapter;
    private ArrayList<Notification> Notification_List;
    private ArrayList<Notification> notif_data;

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity_Main.exit = false;
        ((Activity_Main) getActivity()).setActionBarTitle("Notifications");
        NotificationList = (ListView) view.findViewById(R.id.notification_list);
        notif_data = ShowDataList();
        mAdapter = new Notification_adapter(getActivity(), R.layout.list_item_notification, notif_data);
        NotificationList.setAdapter(mAdapter);
        NotificationList.invalidateViews();
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_notification, container, false);
    }

    public void onResume() {
        super.onResume();
        Activity_Main.exit = false;
        ((Activity_Main) getActivity()).setActionBarTitle("Notifications");
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }

    }


    public ArrayList<Notification> ShowDataList() {

        Notification_List = new ArrayList<>();
        List<Db_Notifications> list = Db_Notifications.getAllNotif();
        for (int i = 0; i < list.size(); i++) {
            Db_Notifications addDBNotif = list.get(i);
            Notification notif = new Notification();
            notif.setMessage(addDBNotif.message);
            notif.setTitle(addDBNotif.title);
            Notification_List.add(notif);
        }
        Collections.reverse(Notification_List);
        return Notification_List;
    }

    public static class NotifHolder {
        private TextView Title;
        private TextView Message;
    }

    public class Notification_adapter extends ArrayAdapter<Notification> {
        private Context c;
        private List<Notification> notificationList;

        public Notification_adapter(Context context, int resource, List<Notification> objects) {
            super(context, resource, objects);
            this.c = context;
            this.notificationList = objects;
        }


        @Override
        public void add(Notification object) {
            super.add(object);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return notificationList.size();
        }

        @Override
        public Notification getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getPosition(Notification item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            NotifHolder notifHolder;
            if (convertView == null) {
                notifHolder = new NotifHolder();
                LayoutInflater inflater = (LayoutInflater) c
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_notification, parent, false);
                notifHolder.Message = (TextView) convertView.findViewById(R.id.notif_message);
                notifHolder.Title = (TextView) convertView.findViewById(R.id.notif_title);
                convertView.setTag(notifHolder);
            } else {
                notifHolder = (NotifHolder) convertView.getTag();
            }
            notifHolder.Title.setText(notificationList.get(position).getTitle());
            notifHolder.Message.setText(notificationList.get(position).getMessage());
            return convertView;
        }
    }


}
