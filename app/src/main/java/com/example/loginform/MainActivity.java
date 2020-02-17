package com.example.loginform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextView text;
    private EditText email;
    private  EditText passw;
    private Button btn;

    ProgressDialog progressDialog ;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=findViewById(R.id.str_email);
        passw=findViewById(R.id.str_password);
     btn=findViewById(R.id.str_login);
     progressDialog = new ProgressDialog(this);
     auth = FirebaseAuth.getInstance();
        text=findViewById(R.id.str_text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Form.class);
                startActivity(intent);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllValidation();
            }
        });
    }

    private void checkAllValidation() {
        String userEmail = email.getText().toString().trim();
        String userPassword = passw.getText().toString().trim();
        if(TextUtils.isEmpty(userEmail)){
            email.setError("Please Enter Email");
        }
        else if (!(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())){
            email.setError("Please Enter Valid Email");
        }
        else if(TextUtils.isEmpty(userPassword)) {
            email.setError("Please Enter Password");
        }
        else {
            performAuthentication(userEmail,userPassword);
        }

    }

    private void performAuthentication(String userEmail, String userPassword) {
        progressDialog.setMessage("please wait.....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        auth.signInWithEmailAndPassword(userEmail,userPassword)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if(task.isSuccessful()){
                           progressDialog.dismiss();
                           startActivity(new Intent(MainActivity.this,Welcome.class));
                           finish();
                       }
                       else {
                           progressDialog.dismiss();
                           Toast.makeText(MainActivity.this,"Not Authentication",Toast.LENGTH_SHORT).show();
                       }

                   }
               });

    }
}
