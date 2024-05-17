package ru.mirea.panov.mirea_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.panov.mirea_project.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "firebase";
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser.isEmailVerified()) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
        } else {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            binding.textViewStatus.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            binding.textViewDetail.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            binding.linearLayoutSignIn.setVisibility(View.GONE);
            binding.linearLayoutSignOut.setVisibility(View.VISIBLE);
            binding.buttonVerifyEmail.setEnabled(!user.isEmailVerified());
        } else {
            binding.textViewStatus.setText(R.string.signed_out);
            binding.textViewDetail.setText(null);
            binding.linearLayoutSignIn.setVisibility(View.VISIBLE);
            binding.linearLayoutSignOut.setVisibility(View.GONE);

            binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = binding.editTextEmail.getText().toString();
                    String password = binding.editTextPassword.getText().toString();
                    if (!email.isEmpty() && !password.isEmpty()) {
                        signIn(email, password);
                    } else {
                        Toast.makeText(AuthActivity.this, "Заполните все поля!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            binding.buttonSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });

            binding.buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = binding.editTextEmail.getText().toString();
                    String password = binding.editTextPassword.getText().toString();
                    if (!email.isEmpty() && !password.isEmpty()) {
                        createAccount(email, password);
                    } else {
                        Toast.makeText(AuthActivity.this, "Заполните все поля!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            binding.buttonVerifyEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmailVerification();
                }
            });
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "create account: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailAndPassword: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.d(TAG, "createUserWithEmailAndPassword: failure");
                            Toast.makeText(AuthActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn: " + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                            } else {
                                updateUI(user);
                            }
                        } else {
                            Log.d(TAG, "signInWithEmail: failure");
                            Toast.makeText(AuthActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            binding.textViewStatus.setText(R.string.auth_failed);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        binding.buttonVerifyEmail.setEnabled(false);
        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.buttonVerifyEmail.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(AuthActivity.this,
                                    "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(AuthActivity.this,
                                    "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
