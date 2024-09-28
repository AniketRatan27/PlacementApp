package com.example.placementapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatMessage> chatMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewChat);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        chatMessages = new ArrayList<>();
        adapter = new ChatAdapter(chatMessages);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("chats");

        fetchChatMessages();
    }

    private void fetchChatMessages(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    if (chatMessage != null && "admin".equals(chatMessage.getSender())){
                        chatMessages.add(chatMessage);
                    }
                }

                adapter.notifyDataSetChanged();
                recyclerView.post(() -> {
                    if (!chatMessages.isEmpty()){
                        recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}



















