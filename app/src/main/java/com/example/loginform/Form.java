package com.example.loginform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginform.PersonModel.PersonModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Pattern;

import javax.xml.transform.Result;

public class Form extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText password;
    private Button btn;
    private TextView gotologin;
    private EditText confirm;
    private Button image;
    private RadioGroup gender;
     RadioButton genderBtn;
    ProgressDialog progressDialog;


    Uri fileData = null;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        image=findViewById(R.id.str_image);
        name=findViewById(R.id.str_name);
        email=findViewById(R.id.str_email);
        phone=findViewById(R.id.str_phone);
        password=findViewById(R.id.str_pass);
        confirm=findViewById(R.id.str_confirm);
        gender=findViewById(R.id.gender);
        btn=findViewById(R.id.str_btn);
        gotologin=findViewById(R.id.str_goto);
        progressDialog= new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Form.this,MainActivity.class));
                        finish();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllValidation();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Choose_Image();
            }
        });

    }

    private void Choose_Image() {

        Intent ImagePick=new Intent();
        ImagePick.setType("image/*");
        ImagePick.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(ImagePick,01);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 01 && requestCode  == RESULT_OK);
        fileData = data.getData();
    }

    private void checkAllValidation() {
        String username=name.getText().toString().trim();
        String userEmail=email.getText().toString().trim();
        String userPhone=phone.getText().toString().trim();
        String userPass=password.getText().toString().trim();
        String userCon=confirm.getText().toString().trim();

        if (TextUtils.isEmpty(username)){
            name.setError("Please Enter Name Here");
        }
        else if (TextUtils.isEmpty(userEmail)){
            name.setError("Please Enter email Here");
        }
        else if (!(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())){
            email.setError("Please Enter Valid email");
        }
        else if (TextUtils.isEmpty(userPass)){
            password.setError("Please Enter Password Here");
        }
        else if (TextUtils.isEmpty(userCon)){
            confirm.setError("Please Enter Confirm Password Here");
        }
        else if (!(userPass.equals(userCon))){
            Toast.makeText(this,"Password dont Match",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhone)){
            phone.setError("Please Enter PhoneNo Here");
        }
        else if (fileData == null){
            Toast.makeText(this,"Please Select Image",Toast.LENGTH_SHORT).show();
        }
        else {
             int selectedId = gender.getCheckedRadioButtonId();
             genderBtn.findViewById(selectedId);
             String userGender=genderBtn.getText().toString();
             InsertInDatabase(username,userEmail,userPhone,userPass,userCon,userGender,fileData);

        }
    }

    private void InsertInDatabase(final String userName, final String userEmail, final String userPhone, final String userPass, final String userCon, final String userGender, final Uri fileData) {
        progressDialog.setMessage("please wait.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(userEmail,userPass)
                 .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                             SendImageinStorage(userName,userEmail,userPhone,userPass,userCon,userGender,fileData);

                         }
                         else {

                             progressDialog.dismiss();
                             Toast.makeText(Form.this,"Authentication not Complete",Toast.LENGTH_SHORT).show();
                         }

                     }
                 });

    }

    private void SendImageinStorage(final String userName, final String userEmail, final String userPhone, final String userPass, final String userCon, final String userGender, final Uri fileData) {
        final StorageReference ref = FirebaseStorage.getInstance().getReference("PersonImages/" + auth.getCurrentUser().getUid());
        ref.putFile(fileData).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()) {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(!task.isSuccessful()) {
                    Uri DownloadURL = task.getResult();
                    InsertInRealTimeDatabase(userName,userEmail,userPhone,userPass,userCon,userGender,DownloadURL);

                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(Form.this,"Url Not Generate",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void InsertInRealTimeDatabase(String userName, String userEmail, String userPhone, String userPass, String userCon, String userGender, Uri downloadedURL) {
        PersonModel values= new PersonModel(userName, userEmail, userPass, userPhone, userGender, downloadedURL.toString());
        FirebaseDatabase.getInstance().getReference("PersonTable").child(auth.getCurrentUser().getUid()).setValue(values)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(Form.this,"User  Created",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Form.this,MainActivity.class));
                                   finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(Form.this,"User not Created",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}