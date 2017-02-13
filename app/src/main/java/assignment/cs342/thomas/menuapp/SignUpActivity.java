package assignment.cs342.thomas.menuapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by tlaminator on 2/11/17.
 */

public class SignUpActivity extends AppCompatActivity {
    public final static String FILE_NAME = "account_names";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    // Hides keyboard when touch outside of EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void registerUser(View view) {
        EditText usernameET = (EditText) findViewById(R.id.username_sign_up);
        EditText passwordET = (EditText) findViewById(R.id.password_sign_up);

        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        Log.d("SignUpActivity", "username: " + username);
        Log.d("SignUpActivity", "password: " + password);
        Intent intent = new Intent(this, MainActivity.class);

        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(FILE_NAME,
                    Context.MODE_APPEND));
            writer.write(username + "\t\t" + password + "\n");
            writer.close();
        } catch (IOException e) {
            Log.e("SignUpActivity", e.toString());
        }

        startActivity(intent);
    }
}
