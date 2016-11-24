package com.youtube.sorcjc.fullday2016.ui.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.youtube.sorcjc.fullday2016.Global;
import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.model.Paper;
import com.youtube.sorcjc.fullday2016.model.Question;
import com.youtube.sorcjc.fullday2016.model.Speaker;

import java.util.ArrayList;

import static android.R.attr.id;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private static final String TAG = "QuestionAdapter";
    private static long lastClickTime;
    private static int ignoredTimes = 0;

    private ArrayList<Question> dataSet;

    private static FirebaseDatabase database;
    // private static QuestionAdapter questionAdapter;
    private static int user_id;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // context
        Context context;
        // text views
        TextView tvName;
        TextView tvDescription;
        TextView tvLikes;
        // buttons
        Button btnLike, btnDislike;
        // data
        String key;
        // int position;

        ViewHolder(View v) {
            super(v);
            context = v.getContext();

            tvName = (TextView) v.findViewById(R.id.tvName);
            tvDescription = (TextView) v.findViewById(R.id.tvDescription);
            tvLikes = (TextView) v.findViewById(R.id.tvLikes);

            btnLike = (Button) v.findViewById(R.id.btnLike);
            btnDislike = (Button) v.findViewById(R.id.btnDislike);
        }

        void setOnClickListeners() {
            btnLike.setOnClickListener(this);
            btnDislike.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Preventing multiple clicks, using threshold of 1 second
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                switch (ignoredTimes) {
                    case 1:
                        Toast.makeText(context, R.string.stop_clicking_1, Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(context, R.string.stop_clicking_2, Toast.LENGTH_SHORT).show();
                        break;
                    case 10:
                        Toast.makeText(context, R.string.stop_clicking_3, Toast.LENGTH_SHORT).show();
                        break;
                    case 22:
                        Toast.makeText(context, R.string.stop_clicking_4, Toast.LENGTH_SHORT).show();
                        break;
                }

                if (ignoredTimes >= 23)
                    vibrateDevice();

                lastClickTime = SystemClock.elapsedRealtime();
                ++ignoredTimes;
                return;
            } else lastClickTime = SystemClock.elapsedRealtime();

            ignoredTimes = 0;
            switch (view.getId()) {
                case R.id.btnLike:
                    toggleLike();
                    btnLike.setEnabled(false);
                    break;
                case R.id.btnDislike:
                    toggleLike();
                    btnDislike.setEnabled(false);
                    break;
            }
        }

        private void vibrateDevice() {
            // Vibrate for 250 milliseconds
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(250);
        }

        private void toggleLike() {
            DatabaseReference myRef = database.getReference("likes/"+user_id+"/"+key);
            myRef.addListenerForSingleValueEvent(new StatusLikeForSingleValue(user_id, key/*, position*/));
        }

    }

    private static class StatusLikeForSingleValue implements ValueEventListener {

        private int user_id;
        private String key;
        // private int position;

        StatusLikeForSingleValue(int user_id, String key/*, int position*/) {
            this.user_id = user_id;
            this.key = key;
            // this.position = position;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                // Count -1
                final DatabaseReference questionLikeRef = database.getReference("questions/"+key+"/likes");
                questionLikeRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Integer likes = mutableData.getValue(Integer.class);

                        // For strange cases
                        if (likes == null) {
                            return Transaction.success(mutableData);
                        }

                        likes -= 1;

                        // Set value and report transaction success
                        mutableData.setValue(likes);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        /*questionAdapter.dataSet.get(position).setLiked(false);
                        questionAdapter.notifyItemChanged(position);*/
                    }
                });

                // Remove like
                database.getReference("likes/"+user_id+"/"+key).removeValue();
            } else {
                // Count +1
                DatabaseReference questionLikeRef = database.getReference("questions/"+key+"/likes");
                questionLikeRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Integer likes = mutableData.getValue(Integer.class);

                        // For strange cases
                        if (likes == null) {
                            return Transaction.success(mutableData);
                        }

                        likes += 1;

                        // Set value and report transaction success
                        mutableData.setValue(likes);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        /*questionAdapter.dataSet.get(position).setLiked(true);
                        questionAdapter.notifyItemChanged(position);*/
                    }
                });

                // Save who
                database.getReference("likes/"+user_id+"/"+key).setValue(true);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "Failed to read value.", databaseError.toException());
        }
    }

    public QuestionAdapter(int userId) {
        // questionAdapter = this;
        user_id = userId;

        this.dataSet = new ArrayList<>();

        if (database == null)
            database = FirebaseDatabase.getInstance();
    }

    public void setDataSet(ArrayList<Question> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }
    /*
    public void addQuestion(Question question) {
        this.dataSet.add(question);
        final int sizeDataSet = this.dataSet.size();
        notifyItemInserted(sizeDataSet);
    }
    public void updateQuestion(Question question) {
        for (int i=0; i<this.dataSet.size(); ++i) {
            if (this.dataSet.get(i).getKey().equals(question.getKey())) {
                // Keep the liked state
                question.setLiked(this.dataSet.get(i).isLiked());
                // and replace without problems
                this.dataSet.set(i, question);
                notifyItemChanged(i);
                break;
            }
        }
    }
    public void deleteQuestion(String key) {
        for (int i=0; i<this.dataSet.size(); ++i) {
            if (this.dataSet.get(i).getKey().equals(key)) {
                this.dataSet.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
    */

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question currentQuestion = dataSet.get(position);

        holder.tvName.setText(currentQuestion.getUser());
        holder.tvDescription.setText(currentQuestion.getDescription());
        holder.tvLikes.setText("A "+currentQuestion.getLikes()+" personas les gusta esta pregunta.");
        if (currentQuestion.isLiked()) {
            holder.btnLike.setVisibility(View.GONE);
            holder.btnDislike.setVisibility(View.VISIBLE);
        } else {
            holder.btnLike.setVisibility(View.VISIBLE);
            holder.btnDislike.setVisibility(View.GONE);
        }
        holder.btnLike.setEnabled(true);
        holder.btnDislike.setEnabled(true);

        // set events
        holder.setOnClickListeners();

        // params needed to show the details
        holder.key = currentQuestion.getKey();
        // holder.position = position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}