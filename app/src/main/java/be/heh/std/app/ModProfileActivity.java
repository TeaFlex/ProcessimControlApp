package be.heh.std.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Objects;

import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.User;

public class ModProfileActivity extends FormActivity {

    private Intent intent;
    private EditText email;
    private EditText firstname;
    private EditText lastname;
    private int id;
    private User user;
    private AppDatabase db;

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


        id = intent.getIntExtra("user_id", -1);


        db = AppDatabase.getInstance(getApplicationContext());
        user = db.userdao().getUserById(id);


        email.setText(user.email);
        firstname.setText(user.firstname);
        lastname.setText(user.lastname);
    }

    @Override
    public void checkForm() throws Exception {
        super.checkForm();

        verifyEmail(email);
        verifyLength(firstname, 4, 20);
        verifyLength(lastname, 4, 20);

        if((db.userdao().getUserByEmail(email.getText().toString()) != null)
        && (!Objects.equals(user.email, email.getText().toString())))
            throw new Exception(getString(R.string.taken_email_err));


        db.userdao().updateUserBasicInfos(id,
                firstname.getText().toString(),
                lastname.getText().toString(),
                email.getText().toString());


        toastMessage(getString(R.string.mod_profile_done));
        finish();
    }
}
