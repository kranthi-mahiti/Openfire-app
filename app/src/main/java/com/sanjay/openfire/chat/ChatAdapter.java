package com.sanjay.openfire.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.sanjay.openfire.R;
import com.sanjay.openfire.chat.models.MessageChatModel;
import com.sanjay.openfire.chat.utilies.DateandTimeUtils;
import com.sanjay.openfire.chat.utilies.MySharedPref;
import com.sanjay.openfire.chat.utilies.StringUtils;

import java.util.List;

import static com.sanjay.openfire.chat.utilies.ColorUtils.getRandomColor;


/**
 * Created by RAJ ARYAN on 25/08/19.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatView> {
    MySharedPref mySharedPref;
    private List<MessageChatModel> modelList;
    private Context context;

    public ChatAdapter(List<MessageChatModel> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
        mySharedPref = new MySharedPref(context);
    }

    @NonNull
    @Override
    public ChatView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat, viewGroup, false);
        return new ChatView(view);
    }

    public void setList(List<MessageChatModel> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatView chatView, int i) {
        MessageChatModel model = modelList.get(i);
        int color = Color.parseColor(getRandomColor());
        if (mySharedPref.readInt(model.getFrom(), 0) == 0)
            mySharedPref.writeInt(model.getFrom(), color);
        setViewToLayout(model, chatView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setViewToLayout(MessageChatModel model, ChatView chatView) {
        if (model.getMessage_type().equalsIgnoreCase("RECEIVED")) {
            chatView.llLeftView.setVisibility(View.VISIBLE);
            chatView.llRightView.setVisibility(View.GONE);
            chatView.llCenterView.setVisibility(View.GONE);
//            chatView.llRightViewVideo.setVisibility(View.GONE);
            chatView.tvDate.setText(DateandTimeUtils.SimpleDatetoLongDate(model.getMessage_time()));
            chatView.tvName.setText(model.getFrom());
            chatView.tvName.setTextColor(mySharedPref.readInt(model.getFrom(), 0));
            chatView.tvMessage.setText(model.getMessage());
            chatView.tvNameInitials.setText(StringUtils.getFirstLetter(model.getFrom()));
            chatView.tvNameInitials.setTextColor(context.getResources().getColor(R.color.white));
            chatView.textViewBack.setBackgroundColor(mySharedPref.readInt(model.getFrom(), 0));
        } else if (model.getMessage_type().equalsIgnoreCase("LABEL")) {
            chatView.llLeftView.setVisibility(View.GONE);
            chatView.llRightView.setVisibility(View.GONE);
            chatView.llCenterView.setVisibility(View.VISIBLE);
            chatView.CardViewNewMember.setVisibility(View.VISIBLE);
            chatView.tvNewMemberadded.setText(model.getMessage());
        } else if (model.getMessage_type().equalsIgnoreCase("SENT")) {
            chatView.llLeftView.setVisibility(View.GONE);
            chatView.llRightView.setVisibility(View.VISIBLE);
            chatView.llCenterView.setVisibility(View.GONE);
//            chatView.llRightViewVideo.setVisibility(View.GONE);
            chatView.tvNameR.setText(model.getFrom());
            chatView.tvNameR.setTextColor(mySharedPref.readInt(model.getFrom(), 0));
            chatView.tvDateR.setText(DateandTimeUtils.SimpleDatetoLongDate(model.getMessage_time()));
            chatView.tvMessageR.setText(model.getMessage());
            chatView.tvNameInitialsR.setText(StringUtils.getFirstLetter(model.getFrom()));
            chatView.textViewBackR.setBackgroundColor(mySharedPref.readInt(model.getFrom(), 0));
        }
    }


    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ChatView extends RecyclerView.ViewHolder {

        LinearLayout llLeftView;
        LinearLayout llRightView;
        //        LinearLayout llRightViewVideo;
        LinearLayout llCenterView;

        TextView tvNameInitials;
        TextView tvMessage;
        TextView tvDate;
        TextView tvName;

        TextView tvNameInitialsR;
        TextView tvMessageR;
        TextView tvDateR;
        TextView tvNameR;

//        TextView tvNameInitialsRVideo;
//        TextView tvMessageRVideo;
//        TextView tvDateRVideo;

        RoundedImageView textViewBack;
        RoundedImageView textViewBackR;
        //        RoundedImageView textViewBackRVideo;
        CardView CardViewNewMember;
        TextView tvNewMemberadded;

        public ChatView(@NonNull View itemView) {
            super(itemView);

            CardViewNewMember = itemView.findViewById(R.id.cardViewNewMember);
            tvNewMemberadded = itemView.findViewById(R.id.tvmembersadded);


            llLeftView = itemView.findViewById(R.id.llLeftView);
            llRightView = itemView.findViewById(R.id.llLeftViewR);
            llCenterView = itemView.findViewById(R.id.centerllayout);
//            llRightViewVideo = itemView.findViewById(R.id.llRightViewVideo);

            tvNameInitials = itemView.findViewById(R.id.tvNameInitials);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvName = itemView.findViewById(R.id.tvName);
            tvNameR = itemView.findViewById(R.id.tvNameR);
            tvNameInitialsR = itemView.findViewById(R.id.tvNameInitialsR);
            tvMessageR = itemView.findViewById(R.id.tvMessageR);
            tvDateR = itemView.findViewById(R.id.tvDateR);

//            tvNameInitialsRVideo = itemView.findViewById(R.id.tvNameInitialsRVideo);
//            tvMessageRVideo = itemView.findViewById(R.id.tvMessageRVideo);
//            tvDateRVideo = itemView.findViewById(R.id.tvDateRVideo);

            textViewBack = itemView.findViewById(R.id.textViewBack);
            textViewBackR = itemView.findViewById(R.id.textViewBackR);
//            textViewBackRVideo = itemView.findViewById(R.id.textViewBackRVideo);

        }
    }
}
