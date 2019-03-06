package in.pokoman.www.pokoman.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.pokoman.www.pokoman.Model.QuizStatus;
import in.pokoman.www.pokoman.R;

public class Questions extends AppCompatActivity {
    QuestionView questionView;
    FragmentManager fragman;
    boolean fraginplace;
    private VideoView v1;
    private String liveStatus, showQuestion;
    private String currentQuesno = "1";
    private ValueEventListener mQuizStatusListner;
    DatabaseReference mQuizStatusReference;
    private Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/quizapp-36880.appspot.com/o/Phree%20-%20Make%20the%20world%20your%20paper.mp4?alt=media&token=e2943cc3-554a-4cd2-a23e-d38256aa28ed");
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        questionView=new QuestionView();
        fraginplace=false;
        mQuizStatusReference = FirebaseDatabase.getInstance().getReference().child("QuizStatus");
        attachQuizStatusListener();
        fragman= getSupportFragmentManager();
        v1=(VideoView)findViewById(R.id.videoview);
        Button endbtn = (Button) findViewById(R.id.endquiz);
        endbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(Questions.this,MainActivity.class));
                    onStop();
            }
        });

    }

    private void setfragement(Fragment frag) {
        fraginplace=true;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.mainframe, frag).addToBackStack(null).commit();
    }
    public void onBackPressed() {

        if(fraginplace)
        {
            if (fragman.getBackStackEntryCount() == 0) {
                fraginplace=false;
                this.finish();
            } else {
                fragman.popBackStack();
            }
        }
    }

    private void attachQuizStatusListener() {
        if (mQuizStatusListner == null) {
            mQuizStatusListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    QuizStatus status = dataSnapshot.getValue(QuizStatus.class);
                    liveStatus = status.getLive();
                    currentQuesno = status.getCurques();
                    showQuestion = status.getShowques();
                    if (status.getLive().equals("1")) {
                        v1.start();
                        if (showQuestion != null) {
                            if (status.getShowques().equals("1")) {
                                fraginplace = true;
                                setfragement(questionView);
                            } else {
                                if (fraginplace) {
                                    fragman.popBackStack();
                                    fraginplace = false;
                                }
                            }
                        }
                    } else {
                        v1.pause();
                        if (fraginplace)
                            fragman.popBackStack();
                        fraginplace = false;
                        startActivity(new Intent(Questions.this, ResultActivity.class));
                    }
                    if (status.getShowques() != null)
                    {
                        if (!status.getShowques().equals("1")) {
                            onBackPressed();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please try again",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mQuizStatusReference.addValueEventListener(mQuizStatusListner);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        v1.setVideoURI(uri);
        v1.requestFocus();
    }
    @Override
    public void onStop() {
        super.onStop();
        detachAllListener();
    }
    private void detachAllListener() {
//        if(mQuestionListner!=null)
//            mQuestionListner = null;
        if(mQuizStatusListner!=null)
            mQuizStatusListner = null;
    }
}
