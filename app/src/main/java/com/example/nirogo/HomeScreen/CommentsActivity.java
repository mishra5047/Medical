package com.example.nirogo.HomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.nirogo.Adapters.comments.commentAdapter;
import com.example.nirogo.Adapters.comments.commentItem;
import com.example.nirogo.R;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private List<commentItem> list;
    private commentAdapter cAdaptor;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        list= new ArrayList<>();
        list.add(new commentItem("Shelly garg","This is awesome."));
        list.add(new commentItem("Abhi","I love this."));
        list.add(new commentItem("Yash","This is awesome."));
        list.add(new commentItem("Shelly","wowwwwwwwww."));
        list.add(new commentItem("John","shut the fuck up."));
        list.add(new commentItem("Aakash","you suck."));

        cAdaptor= new commentAdapter(list,getApplicationContext());
        recyclerView= findViewById(R.id.recyclercomments);
        recyclerView.setAdapter(cAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(list.isEmpty()){
            TextView emptyTextview = (TextView) findViewById(R.id.noComments);
            emptyTextview.setVisibility(View.VISIBLE);
        }

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}