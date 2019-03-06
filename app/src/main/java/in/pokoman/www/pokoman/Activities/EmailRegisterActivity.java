package in.pokoman.www.pokoman.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.pokoman.www.pokoman.Model.User;
import in.pokoman.www.pokoman.R;

public class EmailRegisterActivity extends AppCompatActivity {
    Button register,login;
    EditText name, email, password;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseUser muser;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);

        //Get Firebase auth instance
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        muser = auth.getCurrentUser();
        databaseReference = database.getReference().child("Users");

        name = findViewById(R.id.nameRegistration);
        email = findViewById(R.id.emailRegistration);
        password = findViewById(R.id.passwordRegistration);

        login = findViewById(R.id.want_to_login);
        login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailRegisterActivity.this, LoginActivity.class));
            }
        });


        register = findViewById(R.id.emailRegisterButton);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

               // String uid = user.getUid();
                String getName = name.getText().toString().trim();
                String getEmail = email.getText().toString().trim();
                String getPassword = password.getText().toString().trim();


                if (TextUtils.isEmpty(getEmail)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(getPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                }

                //create user
                final User user = new User(getName, getEmail,0,0);

                auth.createUserWithEmailAndPassword(getEmail, getPassword)
                        .addOnCompleteListener(EmailRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(EmailRegisterActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(EmailRegisterActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    FirebaseUser firebaseUser =  auth.getCurrentUser();
                                    String userId = firebaseUser.getUid();
                                databaseReference.child(userId)
                                   .setValue(user);
                           Toast.makeText(EmailRegisterActivity.this,"User created successfully",Toast.LENGTH_SHORT);

                                    startActivity(new Intent(EmailRegisterActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            };


        });
    }

}
