package com.buyuphk.soketcommunication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.buyuphk.soketcommunication.NettyConstant;
import com.buyuphk.soketcommunication.R;
import com.buyuphk.soketcommunication.bean.MessageEntity;

import java.util.List;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-07-20 09:34
 * motto: 勇于向未知领域探索
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<MessageEntity> messageList;

    public MessageAdapter(Context context, List<MessageEntity> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    public void setNewData(List<MessageEntity> data) {
        messageList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        MessageEntity message = messageList.get(position);
        if (message.getSpeaker().equals(NettyConstant.CLIENT_ID)) {
            //right
            holder.getFrameLayoutLeft().setVisibility(View.GONE);
            holder.getFrameLayoutRight().setVisibility(View.VISIBLE);
            if (message.getMessageType() == 0) {
                String userId = " :" + message.getSpeaker();
                holder.getTextViewUserIdZero1().setText(userId);
                String messageCompose = message.getMessage() + "(" + message.getCreateDateTime() + ")";
                holder.getTextViewMessage1().setText(messageCompose);
                holder.getLinearLayoutZero1().setVisibility(View.VISIBLE);
                holder.getLinearLayoutOne1().setVisibility(View.GONE);
            } else {
                String userId = " :" + message.getSpeaker();
                holder.getTextViewUserIdOne1().setText(userId);
                Glide.with(context).load(message.getMessage()).centerCrop().override(200, 150).into(holder.getImageViewPicture1());
                holder.getLinearLayoutZero1().setVisibility(View.GONE);
                holder.getLinearLayoutOne1().setVisibility(View.VISIBLE);
            }
        } else {
            //left
            holder.getFrameLayoutRight().setVisibility(View.GONE);
            holder.getFrameLayoutLeft().setVisibility(View.VISIBLE);
            if (message.getMessageType() == 0) {
                String userId = message.getSpeaker() + ": ";
                holder.getTextViewUserIdZero().setText(userId);
                String messageCompose = message.getMessage() + "(" + message.getCreateDateTime() + ")";
                holder.getTextViewMessage().setText(messageCompose);
                holder.getLinearLayoutZero().setVisibility(View.VISIBLE);
                holder.getLinearLayoutOne().setVisibility(View.GONE);
            } else {
                String userId = message.getSpeaker() + ": ";
                holder.getTextViewUserIdOne().setText(userId);
                Glide.with(context).load(message.getMessage()).centerCrop().override(200, 150).into(holder.getImageViewPicture());
                holder.getLinearLayoutZero().setVisibility(View.GONE);
                holder.getLinearLayoutOne().setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayoutZero;
        private LinearLayout linearLayoutOne;
        private TextView textViewUserIdZero;
        private TextView textViewUserIdOne;
        private TextView textViewMessage;
        private ImageView imageViewPicture;

        private FrameLayout frameLayoutLeft;
        private FrameLayout frameLayoutRight;

        private RelativeLayout linearLayoutZero1;
        private RelativeLayout linearLayoutOne1;
        private TextView textViewUserIdZero1;
        private TextView textViewUserIdOne1;
        private TextView textViewMessage1;
        private ImageView imageViewPicture1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutZero = itemView.findViewById(R.id.item_msg_linear_layout_zero);
            linearLayoutOne = itemView.findViewById(R.id.item_msg_linear_layout_one);
            textViewUserIdZero = itemView.findViewById(R.id.item_msg_text_view_user_id_zero);
            textViewUserIdOne = itemView.findViewById(R.id.item_msg_text_view_user_id_one);
            textViewMessage = itemView.findViewById(R.id.tv_msg);
            imageViewPicture = itemView.findViewById(R.id.item_msg_image_view);

            frameLayoutLeft = itemView.findViewById(R.id.left);
            frameLayoutRight = itemView.findViewById(R.id.right);

            linearLayoutZero1 = itemView.findViewById(R.id.item_msg_linear_layout_zero1);
            linearLayoutOne1 = itemView.findViewById(R.id.item_msg_linear_layout_one1);
            textViewUserIdZero1 = itemView.findViewById(R.id.item_msg_text_view_user_id_zero1);
            textViewUserIdOne1 = itemView.findViewById(R.id.item_msg_text_view_user_id_one1);
            textViewMessage1 = itemView.findViewById(R.id.tv_msg1);
            imageViewPicture1 = itemView.findViewById(R.id.item_msg_image_view1);
        }

        public LinearLayout getLinearLayoutZero() {
            return linearLayoutZero;
        }

        public void setLinearLayoutZero(LinearLayout linearLayoutZero) {
            this.linearLayoutZero = linearLayoutZero;
        }

        public LinearLayout getLinearLayoutOne() {
            return linearLayoutOne;
        }

        public void setLinearLayoutOne(LinearLayout linearLayoutOne) {
            this.linearLayoutOne = linearLayoutOne;
        }

        public TextView getTextViewUserIdZero() {
            return textViewUserIdZero;
        }

        public void setTextViewUserIdZero(TextView textViewUserIdZero) {
            this.textViewUserIdZero = textViewUserIdZero;
        }

        public TextView getTextViewUserIdOne() {
            return textViewUserIdOne;
        }

        public void setTextViewUserIdOne(TextView textViewUserIdOne) {
            this.textViewUserIdOne = textViewUserIdOne;
        }

        public TextView getTextViewMessage() {
            return textViewMessage;
        }

        public void setTextViewMessage(TextView textViewMessage) {
            this.textViewMessage = textViewMessage;
        }

        public ImageView getImageViewPicture() {
            return imageViewPicture;
        }

        public void setImageViewPicture(ImageView imageViewPicture) {
            this.imageViewPicture = imageViewPicture;
        }

        public FrameLayout getFrameLayoutLeft() {
            return frameLayoutLeft;
        }

        public void setFrameLayoutLeft(FrameLayout frameLayoutLeft) {
            this.frameLayoutLeft = frameLayoutLeft;
        }

        public FrameLayout getFrameLayoutRight() {
            return frameLayoutRight;
        }

        public void setFrameLayoutRight(FrameLayout frameLayoutRight) {
            this.frameLayoutRight = frameLayoutRight;
        }

        public RelativeLayout getLinearLayoutZero1() {
            return linearLayoutZero1;
        }

        public void setLinearLayoutZero1(RelativeLayout linearLayoutZero1) {
            this.linearLayoutZero1 = linearLayoutZero1;
        }

        public RelativeLayout getLinearLayoutOne1() {
            return linearLayoutOne1;
        }

        public void setLinearLayoutOne1(RelativeLayout linearLayoutOne1) {
            this.linearLayoutOne1 = linearLayoutOne1;
        }

        public TextView getTextViewUserIdZero1() {
            return textViewUserIdZero1;
        }

        public void setTextViewUserIdZero1(TextView textViewUserIdZero1) {
            this.textViewUserIdZero1 = textViewUserIdZero1;
        }

        public TextView getTextViewUserIdOne1() {
            return textViewUserIdOne1;
        }

        public void setTextViewUserIdOne1(TextView textViewUserIdOne1) {
            this.textViewUserIdOne1 = textViewUserIdOne1;
        }

        public TextView getTextViewMessage1() {
            return textViewMessage1;
        }

        public void setTextViewMessage1(TextView textViewMessage1) {
            this.textViewMessage1 = textViewMessage1;
        }

        public ImageView getImageViewPicture1() {
            return imageViewPicture1;
        }

        public void setImageViewPicture1(ImageView imageViewPicture1) {
            this.imageViewPicture1 = imageViewPicture1;
        }
    }
}
