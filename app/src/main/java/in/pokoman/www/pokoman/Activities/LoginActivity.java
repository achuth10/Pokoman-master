package in.pokoman.www.pokoman.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.pokoman.www.pokoman.Model.User;
import in.pokoman.www.pokoman.R;

public class LoginActivity extends AppCompatActivity {
    Toolbar mToolbar;
    TextView mToolTitle;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    //Firebase Realtime Database
    private DatabaseReference mUserDatabaseReference;
    private ValueEventListener mValueEventListner;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private final static int RC_SIGN_IN = 2;
    private final static String TAG = "LOGIN ACTIVITY";
    private TextView email_register, forgot_pwd;
    Button email_signin;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        email_register = (TextView) findViewById(R.id.registertext);
        email_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, EmailRegisterActivity.class));

            }
        });

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        email_signin = (Button) findViewById(R.id.login_btn);

        forgot_pwd = (TextView)findViewById(R.id.forgot_password);
        forgot_pwd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));

            }
        });



        email_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString();
                final String passwordString = password.getText().toString();

                if (TextUtils.isEmpty(emailString)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordString)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //authenticate user
                mAuth.signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        password.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

    }

    private void updateUI() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

   private void saveUserData(FirebaseUser user) {
        String uid = user.getUid();
        String name = user.getDisplayName();
        String email = user.getEmail();
        User userdata = new User(uid, name, email, 0, 0);
        mUserDatabaseReference.child(uid).setValue(userdata);
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            updateUI();

        }
    }

    private void detachedDatabaseReadListner() {
        if (mValueEventListner != null) {
            mUserDatabaseReference.removeEventListener(mValueEventListner);
            mValueEventListner = null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachedDatabaseReadListner();
    }
}
