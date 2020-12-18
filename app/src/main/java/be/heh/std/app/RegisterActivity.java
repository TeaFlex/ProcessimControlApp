package be.heh.std.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends FormActivity {

    private boolean firstTime;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private EditText password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstTime = getIntent().getBooleanExtra("first_time", false);
        form = findViewById(R.id.register_form);
        firstname = (EditText) findViewById(R.id.register_firstname);
        lastname = (EditText) findViewById(R.id.register_lastname);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        password2 = (EditText) findViewById(R.id.register_password2);
        submit = (Button) findViewById(R.id.register_connect);

        form.setVisibility(View.VISIBLE);
        if(firstTime) info_msg.setText(getString(R.string.need_register));
    }

    @Override
    public void checkForm() {
        try {

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}