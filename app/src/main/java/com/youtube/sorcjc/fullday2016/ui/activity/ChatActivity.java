package com.youtube.sorcjc.fullday2016.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.model.Question;
import com.youtube.sorcjc.fullday2016.ui.LoginActivity;
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        etDescription = (EditText) findViewById(R.id.etDescription);
        ImageButton btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        final int user_id = Global.getIntFromSharedPreferences(this, "user_id");
        if (user_id == 0)
            redirectToLogin();

        questionAdapter = new QuestionAdapter(user_id);
        recyclerView.setAdapter(questionAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInstantQuestions() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("questions");
        myRef.orderByChild("likes").addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // long countQuestions = dataSnapshot.getChildrenCount();

        ArrayList<Question> questions = new ArrayList<>();
        for (DataSnapshot questionSnap: dataSnapshot.getChildren()) {
            Question question = questionSnap.getValue(Question.class);

            final String questionKey = questionSnap.getKey();
            question.setKey(questionKey);

            final int user_id = Global.getIntFromSharedPreferences(this, "user_id");
            if (user_id == 0)
                redirectToLogin();

            DatabaseReference myRef = database.getReference("likes/"+user_id+"/"+questionKey);
            myRef.addListenerForSingleValueEvent(new MyGivenLikes(question));
            questions.add(question);
        }

        questionAdapter.setDataSet(questions);
    }

    private void redirectToLogin() {
        Toast.makeText(this, R.string.session_expired, Toast.LENGTH_SHORT).show();

        // Close all activities and open the login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Log.w(TAG, "Failed to read value", error.toException());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                final String description = etDescription.getText().toString().trim();
                if (description.isEmpty())
                    return;

                final String name = Global.getFromSharedPreferences(this, "name");
                if (name.isEmpty())
                    return;

                // Show a dialog with a warning
                final String questionKey = database.getReference("questions").push().getKey();
                Question newQuestion = new Question();
                newQuestion.setDescription(description);
                newQuestion.setUser(name);
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
            question.setLiked(dataSnapshot.exists());
            questionAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "Failed to read value.", databaseError.toException());
        }
    }
}
