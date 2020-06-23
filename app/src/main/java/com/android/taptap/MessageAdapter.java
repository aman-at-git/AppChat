package com.android.taptap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    FirebaseAuth mAuth;
    String from_user;
    String current_user_id;


    public MessageAdapter(List<Messages> mMessagesList) {

        this.mMessageList = mMessagesList;
    }


    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        if(viewType == 0) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_sender_layout, parent, false);

            return new MessageViewHolder(v);
        }
        else if(viewType == 1) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_receiver_layout, parent, false);

            return new MessageViewHolder(v);
        }
        return null;

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView timeText;

        public MessageViewHolder(View view){
        super(view);

        messageText = view.findViewById(R.id.messageBox);
        timeText = view.findViewById(R.id.messageBoxTime);

        }

    }

    public void onBindViewHolder(MessageViewHolder viewHolder, int i){

        Messages c = mMessageList.get(i);
        viewHolder.messageText.setText(c.getMessage());
        String time = c.getTime().toString();
        viewHolder.timeText.setText(time);
        from_user = c.getFrom();
    }

    @Override
    public int getItemViewType(int position) {
        if(mMessageList.get(position).getFrom().equals(current_user_id)){
            return 0;

        }

        else
            return 1;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
