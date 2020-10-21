package com.scuavailable.available.school;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.scuavailable.available.R;

public class RequestModifyActivity extends AppCompatActivity {
    EditText studentIDEt,courseIDEt,sectionIDEt;
    Button mSubmitBtn;
    ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_modify);
        initViews();

    }

    private void initViews() {
        studentIDEt = findViewById(R.id.et_request_modify_student_id);
        courseIDEt = findViewById(R.id.et_request_modify_course_id);
        sectionIDEt = findViewById(R.id.et_request_modify_section);
        mSubmitBtn = findViewById(R.id.btn_request_modify_submit);
        backBtn = findViewById(R.id.ib_request_modify_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送给请求
                String studentID = studentIDEt.getText().toString();
                String courseID = courseIDEt.getText().toString();
                String sectionID = sectionIDEt.getText().toString();


            }
        });
    }


}
