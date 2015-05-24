package com.wesllei.ruufmt.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wesllei.ruufmt.R;

import java.util.ArrayList;

/**
 * Created by wesllei on 09/05/15.
 */
public class ChatFragment extends Fragment{

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ArrayList messages = new ArrayList();


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_main);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        chatAdapter = new ChatAdapter(context, messages);
        recyclerView.setAdapter(chatAdapter);

        return view;
    }
}
