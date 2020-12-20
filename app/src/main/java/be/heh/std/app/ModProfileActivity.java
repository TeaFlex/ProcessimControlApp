package be.heh.std.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Objects;

import be.heh.std.database.AppDatabase;
import be.heh.std.database.User;

public class ModProfileActivity extends FormActivity {

    private Intent intent;
    private EditText email;
    private EditText firstname;
    private EditText lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        form = findViewById(R.id.modprofile_form);
        submit = (Button) findViewById(R.id.accept_mod_profile);
        cancel = (Button) findViewById(R.id.cancel_mod_profile);
        email = (EditText) findViewById(R.id.mod_profile_email);
        firstname = (EditText) findViewById(R.id.mod_profile_firstname);
        lastname = (EditText) findViewById(R.id.mod_profile_lastname);
        intent = getIntent();

        notEmptyInputs.addAll(Arrays.asList(email, firstname, lastname));

        form.setVisibility(View.VISIBLE);
        email.setText(intent.getStringExtra("user_email"));
        firstname.setText(intent.getStringExtra("user_firstname"));
        lastname.setText(intent.getStringExtra("user_lastname"));
    }

    @Override
    public void onFormClickManager(View v) {
        super.onFormClickManager(v);
        if(v.getId() == R.id.cancel_mod_profile) {
            intent.setClass(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void checkForm() throws Exception {
        super.checkForm();


        verifyEmail(email);
        verifyLength(firstname, 4, 20);
        verifyLength(lastname, 4, 20);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        if((db.userdao().getUserByEmail(email.getText().toString()) != null)
        && (!Objects.equals(intent.getStringExtra("user_email"), email.getText().toString())))
            throw new Exception(getString(R.string.taken_email_err));

        int id = Integer.parseInt(intent.getStringExtra("user_id"));
        db.userdao().updateUserBasicInfos(id,
                firstname.getText().toString(),
                lastname.getText().toString(),
                email.getText().toString());

        User u = db.userdao().getUserByEmail(email.getText().toString().toLowerCase());

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("user_id", String.valueOf(u.id));
        intent.putExtra("user_firstname", u.firstname);
        intent.putExtra("user_lastname", u.lastname);
        intent.putExtra("user_email", u.email);
        intent.putExtra("user_role", u.role);
        toastMessage(getString(R.string.mod_profile_done));
        startActivity(intent);
        finish();
    }
}
