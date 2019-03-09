package in.pokoman.www.pokoman.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.pokoman.www.pokoman.Model.Question;
import in.pokoman.www.pokoman.Model.QuizStatus;
import in.pokoman.www.pokoman.R;
public class QuestionView extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ProgressBar progressBar;
    FragmentManager fragman;
    CountDownTimer waitTimer;
    boolean fraginplace,doneloading;
    int counter,initial;
    boolean timeup=false,selected=false,selectedcorrect=false;
    ArrayList<Question> questions;
    TextView questiionView, op1, op2, op3, op4,time,qno;
    boolean clicked;
    private String liveStatus, showQuestion;
    private String currentQuesno = "1";
    private ValueEventListener mQuizStatusListner;
    private ValueEventListener mQuestionListner;
    DatabaseReference mQuizStatusReference;
    private DatabaseReference mUserPtsReference;
    private DatabaseReference mQuestionsReference;
    private DatabaseReference mVoucherReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mUserLostReference;
    public int pts=0, voucher = 0;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_question_view, container, false);

        counter =  0;
        questions = new ArrayList<>();
        clicked =fraginplace=doneloading=selected=selectedcorrect=false;
        fragman= getFragmentManager();
        progressBar=(ProgressBar)v.findViewById(R.id.questionloadprog);
        questiionView = (TextView) v.findViewById(R.id.questionView);

        //Initialise the views
        time= (TextView) v.findViewById(R.id.timeview);
        op1 = (TextView) v.findViewById(R.id.option1);
        op2 = (TextView) v.findViewById(R.id.option2);
        op3 = (TextView) v.findViewById(R.id.option3);
        op4 = (TextView) v.findViewById(R.id.option4);
        qno=  (TextView) v.findViewById(R.id.qno);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mQuestionsReference=FirebaseDatabase.getInstance().getReference().child("Question");
        mQuizStatusReference = FirebaseDatabase.getInstance().getReference().child("QuizStatus");
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        mUserPtsReference=mUserReference.child("totalpt");
        mUserLostReference=mUserReference.child("Lost");
        mVoucherReference = mUserReference.child("voucher");
        attachQuestionListener();
        attachQuizStatusListener();

        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markcorrectanswer(op1);
            }
        });
        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markcorrectanswer(op2);
            }
        });
        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markcorrectanswer(op3);
            }
        });
        op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markcorrectanswer(op4);
            }
        });
        return v;
    }

    private void markcorrectanswer(TextView option) {
        selected=true;

        //Checks if the answer is correct and colours the textview accordingly
        //To stop the timer
        TextView correctans = null;
        if(doneloading) {
                    System.out.println("Doneloading");
                    System.out.println(questions);
                if (op1.getText().toString().equals(questions.get(Integer.parseInt(currentQuesno)-1).getAns())) {
                    System.out.println("1 is correct");
                    op1.setBackgroundResource(R.drawable.correctquestion);
                    correctans = op1;
                } else if (op2.getText().toString().equals(questions.get(Integer.parseInt(currentQuesno)-1).getAns())) {
                    op2.setBackgroundResource(R.drawable.correctquestion);
                    System.out.println("2 is correct");
                    correctans = op2;
                } else if (op3.getText().toString().equals(questions.get(Integer.parseInt(currentQuesno)-1).getAns())) {
                    op3.setBackgroundResource(R.drawable.correctquestion);
                    System.out.println("3 is correct");
                    correctans = op3;
                } else if (op4.getText().toString().equals(questions.get(Integer.parseInt(currentQuesno)-1).getAns())) {
                    op4.setBackgroundResource(R.drawable.correctquestion);
                    System.out.println("4 is correct");
                    correctans = op4;
                }


            if (!timeup) {
                if (correctans != null) {
                    System.out.println("!timeup");
                    if (!clicked) {
                        if (correctans != option) {
                            option.setBackgroundResource(R.drawable.wrongquestion);
                            mUserLostReference.setValue(1);
                        } else {
                            selectedcorrect=true;
                            pts++;
                            option.setBackgroundResource(R.drawable.correctquestion);
                            mUserPtsReference.setValue(pts);
                            if(pts >=1 && pts<6){
                                mVoucherReference.setValue(100);
                            }
                            if(pts >=6 && pts<11){
                                mVoucherReference.setValue(250);
                            }
                            if(pts >=11 && pts<15){
                                mVoucherReference.setValue(500);
                            }
                            if(pts == 15){
                                mVoucherReference.setValue(1000);
                            }

                        }
                    }
                } else
                    System.out.println("Error occurred");

                clicked = true;
            }
            else
            {
                correctans.setBackgroundResource(R.drawable.correctquestion);
                clicked = true;
            }
        }
        selected=false;
    }




    private void displayNextQuestion(int count) {
        selected=false;
        op1.setBackgroundResource(R.drawable.questionsholder);
        op2.setBackgroundResource(R.drawable.questionsholder);
        op3.setBackgroundResource(R.drawable.questionsholder);
        op4.setBackgroundResource(R.drawable.questionsholder);
        clicked=false;
        if(doneloading) {
            if(count>=5)
                count=1;
             progressBar.setVisibility(View.GONE);
            System.out.println("Questions here is " + questions);
            questiionView.setText(questions.get(count-1).getQues());
            op1.setText(questions.get(count-1).getOp1());
            op2.setText(questions.get(count-1).getOp2());
            op3.setText(questions.get(count-1).getOp3());
            op4.setText(questions.get(count-1).getOp4());
            qno.setVisibility(View.VISIBLE);
            qno.setText((count)+"/5");
        }
        else
            System.out.println("not done loading");
    }

private void attachQuestionListener() {
    if (mQuestionListner == null) {
        mQuestionListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                initial++;
                for (DataSnapshot questionsnap : dataSnapshot.getChildren()) {
                    Question q = questionsnap.getValue(Question.class);
                    questions.add(q);
                }
                System.out.println("question listener fired");
                doneloading = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mQuestionsReference.addValueEventListener(mQuestionListner);
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
                    System.out.println("Live :" + liveStatus);
                    System.out.println("show question :" + showQuestion);
                    System.out.println("Current question :" + currentQuesno);
                    if(liveStatus.equals("1")) {

                        if (showQuestion.equals("1")) {
                            //selected=false;
                            initial++;
                            if (waitTimer != null) {
                                waitTimer.cancel();
                                waitTimer = null;
                            }
                            timeup = false;
                            if (initial > 1) {
                                progressBar.setVisibility(View.VISIBLE);
                                time.setTextSize(14);
                                time.setGravity(Gravity.CENTER_HORIZONTAL);
                                displayNextQuestion(Integer.parseInt(currentQuesno));
                                reverseTimer(20, time);

                            }
                            else
                            {
                                progressBar.setVisibility(View.GONE);
                                op1.setBackgroundResource(R.color.colorWhite);
                                op2.setBackgroundResource(R.color.colorWhite);
                                op3.setBackgroundResource(R.color.colorWhite);
                                op4.setBackgroundResource(R.color.colorWhite);
                                time.setTextSize(30);
                                time.setGravity(Gravity.CENTER);
                                time.setText("Quiz is loading \n please be patient ");
                            }
                        }
                        else
                        {
                            initial++;
                            if(waitTimer != null) {
                                waitTimer.cancel();
                                waitTimer = null;
                            }
                        }
                    }
                    else
                    {
                        if(waitTimer != null) {
                            waitTimer.cancel();
                            waitTimer = null;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mQuizStatusReference.addValueEventListener(mQuizStatusListner);
        }
    }

    public void reverseTimer(int Seconds, final TextView tv) {

        waitTimer = new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                seconds = seconds % 60;
                tv.setText(String.format("%02d", seconds) + " seconds to choose the answer");
            }

            public void onFinish() {
                //To prevent the timer from causing a npe from lack of a context
               // int orientation = getActivity().getResources().getConfiguration().orientation;
               // if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    tv.setText("Time's up!");
                    timeup = true;
                    if(!clicked)
                        mUserLostReference.setValue(1);
                    //.markcorrectanswer(op1);
                }

        }.start();
    }
     public void onStart() {
        super.onStart();
            progressBar.setVisibility(View.VISIBLE);
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserLostReference.setValue(0);
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

    }
    @Override
     public void onStop() {
        super.onStop();
        mUserLostReference.setValue(0);
        detachAllListener();
    }

    private void detachAllListener() {
        if(mQuestionListner!=null)
            mQuestionListner = null;
        if(mQuizStatusListner!=null)
            mQuizStatusListner = null;
    }
}
