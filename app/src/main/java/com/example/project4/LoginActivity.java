package com.example.project4;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;



public class LoginActivity extends AppCompatActivity {

    TextView buttonToLogin;
    EditText usernameInput, pwdInput;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        // initialize variables
        usernameInput = findViewById(R.id.username_input);
        pwdInput = findViewById(R.id.password_input);
        login = findViewById(R.id.login_button);
        buttonToLogin = findViewById(R.id.noAccount);

        buttonToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim();
                String password = pwdInput.getText().toString().trim();

                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

                if (isUserRegistered(sharedPreferences, username, password)) {
                    String storedPwd = sharedPreferences.getString(password + "_password", password);
                    Log.i("LOGIN_ACTIVITY", "Saved password is " + storedPwd);
                    Log.i("LOGIN_ACTIVITY", "Saved username is " + username);
                    if (password.equals(storedPwd)) {
                        Toast.makeText(LoginActivity.this,
                                "Login successful", Toast.LENGTH_SHORT).show();
                        Log.d("SigninActivity", "Login successful. Navigating to Dashboard.");
                        Intent intent = new Intent(LoginActivity.this,
                                DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "User not found. Please check your username and password or register as a new user",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private boolean isUserRegistered(SharedPreferences sharedPreferences, String username, String password) {
        return sharedPreferences.contains(username + "_username")
                && sharedPreferences.contains(password + "_password");
    }
}
