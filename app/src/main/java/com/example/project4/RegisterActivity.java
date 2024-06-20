package com.example.project4;

import android.content.Context;
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

public class RegisterActivity extends AppCompatActivity {
    EditText usernameInput;
    EditText pwdInput;
    EditText heightInput;
    EditText weightInput;
    EditText ageInput;

    EditText nameInput;


    Button register;

    TextView buttonToSignIn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_activity);
        usernameInput = findViewById(R.id.username_input);
        pwdInput = findViewById(R.id.password_input);
        nameInput = findViewById(R.id.name_input);
        heightInput = findViewById(R.id.height_input);
        weightInput = findViewById(R.id.weight_input);
        ageInput = findViewById(R.id.age_input);
        register = findViewById(R.id.register_button);
        buttonToSignIn = findViewById(R.id.hasAnAccount);

        // if the user already has an account, they should be taken to login activity
        buttonToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // When a user clicks the register button, a new account is created and user information
        // is saved to SharedPreferences
        // if the user is already registered, it should throw an error ("this username already exists")
        // if the user is not registered, save their data
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim();
                String password = pwdInput.getText().toString().trim();
                String height = heightInput.getText().toString().trim();
                String weight = weightInput.getText().toString().trim();
                String age = ageInput.getText().toString().trim();
                String name = nameInput.getText().toString().trim();

                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

                if (isUserRegistered(sharedPreferences, username)) {
                    Toast.makeText(RegisterActivity.this,
                            "This username already exists. Please choose another username",
                            Toast.LENGTH_SHORT).show();
                } else if ((!TextUtils.isEmpty(username)) &&
                        (!TextUtils.isEmpty(height)) && (!TextUtils.isEmpty(weight))
                        && (!TextUtils.isEmpty(age)) && (password.length() > 6)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("currentUsername", username); // Save current username with a consistent key
                    editor.putString(username + "_username", username);
                    editor.putString(password + "_password", password);
                    editor.putString("_name", name);
                    editor.putString(weight + "_weight", weight);
                    editor.putString(height + "_height", height);
                    editor.putString(age + "_age", age);
                    editor.apply();
                    saveUsername(RegisterActivity.this, username);

                    Toast.makeText(RegisterActivity.this, "Account created.",
                            Toast.LENGTH_SHORT).show();
                    Log.d("RegisterActivity", "Login successful. Navigating to Dashboard.");
                    Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Please make sure your password is more than 6 characters and " +
                                    "no fields are empty.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isUserRegistered(SharedPreferences sharedPreferences, String username) {
        return sharedPreferences.contains(username + "_username");
    }

    public void saveUsername(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentUsername", username);
        editor.apply();
    }

    public String getCurrentUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentUsername", "");
    }

    public EditText getNameInput() {
        return nameInput;
    }

    public EditText getHeightInput() {
        return heightInput;
    }
}
