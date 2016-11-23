package com.youtube.sorcjc.fullday2016.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.model.Question;
import com.youtube.sorcjc.fullday2016.ui.adapter.QuestionAdapter;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements ValueEventListener, View.OnClickListener {

    private static final String TAG = "ChatActivity";

    private QuestionAdapter questionAdapter;
    private FirebaseDatabase database;

    private EditText etDescription;

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

        etDescription = (EditText) findViewById(R.id.etDescription);
        ImageButton btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                final String description = etDescription.getText().toString().trim();
                if (description.isEmpty())
                    return;

                // TODO: Show a dialog with a warning
                final String questionKey = database.getReference("questions").push().getKey();
                Question newQuestion = new Question();
                newQuestion.setDescription(description);
                newQuestion.setUser("Juan");
                newQuestion.setLikes(0);
                database.getReference("questions/"+questionKey).setValue(newQuestion);
                etDescription.setText("");
                Global.hideKeyBoard(this);
                break;
        }
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
