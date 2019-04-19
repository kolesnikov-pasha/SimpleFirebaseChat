package com.firebase.app;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class Message {

    String text;
    long time;
    String from;

    public Message() { }

    public Message(String text, long time, String from) {
        this.text = text;
        this.time = time;
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference referenceMes;
    DatabaseReference referenceCnt;

    ArrayList<Message> messages = new ArrayList<>();
    int count = 0;

    LinearLayout layout;
    AppCompatButton btnAdd;
    EditText edtMessage, edtUsername;
    LayoutInflater inflater;
    String username = null;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = getLayoutInflater();
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        referenceMes = database.getReference().child("messages");
        referenceCnt = database.getReference().child("cnt");
        layout = findViewById(R.id.main_layout);
        btnAdd = findViewById(R.id.send);
        scrollView = findViewById(R.id.scroll);
        edtMessage = findViewById(R.id.message_text);
        edtUsername = findViewById(R.id.username);
        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                username = s.toString();
            }
        });
        referenceCnt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) count = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        referenceMes.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                messages.add(dataSnapshot.getValue(Message.class));
                LinearLayout l = (LinearLayout) inflater.inflate(R.layout.message_layout, layout, false);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) l.getLayoutParams();
                Message current = dataSnapshot.getValue(Message.class);
                TextView txtUsername = l.findViewById(R.id.sender);
                txtUsername.setText(current.getFrom());
                TextView txtText = l.findViewById(R.id.message_text);
                txtText.setText(current.getText());
                GradientDrawable drawable = new GradientDrawable();
                drawable.setStroke(2, Color.GRAY);
                drawable.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
                if (current.getFrom().equals(username)) {
                    params.gravity = Gravity.RIGHT;
                    l.setLayoutParams(params);
                    txtUsername.setText("You");
                    txtUsername.setTextColor(Color.WHITE);
                    drawable.setColor(Color.parseColor("#7777FF"));
                    l.setBackgroundDrawable(drawable);
                }
                else {
                    params.gravity = Gravity.LEFT;
                    l.setLayoutParams(params);
                    drawable.setColor(Color.parseColor("#F0F0F0"));
                    l.setBackgroundDrawable(drawable);
                }
                layout.addView(l);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtMessage.getText().toString();
                Message message = new Message(text, System.currentTimeMillis(), username);
                referenceMes.child(count + "").setValue(message);
                referenceCnt.setValue(count + 1);
                count++;
            }
        });
    }
}
