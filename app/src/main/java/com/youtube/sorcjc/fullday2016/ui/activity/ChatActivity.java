package com.youtube.sorcjc.fullday2016.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.model.Question;
import com.youtube.sorcjc.fullday2016.ui.PanelActivity;
import com.youtube.sorcjc.fullday2016.ui.adapter.QuestionAdapter;
import com.youtube.sorcjc.fullday2016.ui.adapter.SpeakerAdapter;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements ValueEventListener {

    private static final String TAG = "ChatActivity";

    private QuestionAdapter questionAdapter;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getInstantQuestions();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        questionAdapter = new QuestionAdapter();
        recyclerView.setAdapter(questionAdapter);
    }

    private void getInstantQuestions() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("questions");
        myRef.orderByChild("likes").addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // long countQuestions = dataSnapshot.getChildrenCount();
        // Toast.makeText(getApplicationContext(), "Vote por su pregunta preferida. Son " + countQuestions + ".", Toast.LENGTH_SHORT).show();

        ArrayList<Question> questions = new ArrayList<>();
        for (DataSnapshot questionSnap: dataSnapshot.getChildren()) {
            Question question = questionSnap.getValue(Question.class);
            final String questionKey = questionSnap.getKey();
            question.setKey(questionKey);
            DatabaseReference myRef = database.getReference("likes/"+1+"/"+questionKey);
            myRef.addListenerForSingleValueEvent(new MyGivenLikes(question));
            questions.add(question);
        }

        questionAdapter.setDataSet(questions);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Log.w(TAG, "Failed to read value.", error.toException());
    }

    class MyGivenLikes implements ValueEventListener {
        private Question question;

        MyGivenLikes(Question question) {
            this.question = question;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            /*if (dataSnapshot.exists()) {
                question.setLiked(true);
            } else {
                question.setLiked(false);
            }*/
            question.setLiked(dataSnapshot.exists());
            questionAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "Failed to read value.", databaseError.toException());
        }
    }
}
