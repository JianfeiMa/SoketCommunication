package com.buyuphk.soketcommunication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buyuphk.soketcommunication.R;
import com.buyuphk.soketcommunication.bean.UserEntity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<UserEntity> userList;
    private MyItemClickListener myItemClickListener;
    private int position;

    public UserAdapter(Context context, List<UserEntity> userList) {
        this.context = context;
        this.userList = userList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<UserEntity> getUserList() {
        return userList;
    }

    public void setNewData(List<UserEntity> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public MyItemClickListener getMyItemClickListener() {
        return myItemClickListener;
    }

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view, myItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        UserEntity userEntity = userList.get(position);
        holder.getTextViewUserId().setText(userEntity.getUserId());
        int unreadCount = userEntity.getUnreadCount();
        TextView textViewUnreadCount = holder.getTextViewUnreadCount();
        textViewUnreadCount.setText(String.valueOf(unreadCount));
        if (unreadCount > 0) {
            textViewUnreadCount.setVisibility(View.VISIBLE);
        } else {
            textViewUnreadCount.setVisibility(View.GONE);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int a = holder.getLayoutPosition();
                Log.d("debug", "長按時的position->>>>>>" + a);
                setPosition(a);//
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        private TextView textViewUserId;
        private TextView textViewUnreadCount;
        private MyItemClickListener myItemClickListener;

        public ViewHolder(@NonNull View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);
            this.myItemClickListener = myItemClickListener;
            itemView.setOnClickListener(this);
            textViewUserId = itemView.findViewById(R.id.item_user_text_view_id);
            textViewUnreadCount = itemView.findViewById(R.id.item_user_text_view_unread_count);
            itemView.setOnCreateContextMenuListener(this);
        }

        public TextView getTextViewUserId() {
            return textViewUserId;
        }

        public void setTextViewUserId(TextView textViewUserId) {
            this.textViewUserId = textViewUserId;
        }

        public TextView getTextViewUnreadCount() {
            return textViewUnreadCount;
        }

        public void setTextViewUnreadCount(TextView textViewUnreadCount) {
            this.textViewUnreadCount = textViewUnreadCount;
        }

        @Override
        public void onClick(View v) {
            if (myItemClickListener != null) {
                myItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int groupID = 0;
            int order = 0;
            int[] itemID = {1, 2};
            for (int i = 0; i < itemID.length; i++) {
                switch (itemID[i]) {
                    case 1:
                        menu.add(groupID, itemID[i], order, "刪除");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
