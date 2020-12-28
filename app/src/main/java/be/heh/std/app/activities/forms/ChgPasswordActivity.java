package be.heh.std.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

import be.heh.std.app.R;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.User;

public class ChgPasswordActivity extends FormActivity {

    private Intent intent;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText newPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        form = findViewById(R.id.chgpassword_form);
        submit = (Button) findViewById(R.id.accept_chg_password);
        cancel = (Button) findViewById(R.id.cancel_chg_password);
        oldPassword = (EditText) findViewById(R.id.chg_password_old);
        newPassword = (EditText) findViewById(R.id.chg_password_new);
        newPassword2 = (EditText) findViewById(R.id.chg_password_new2);

        notEmptyInputs.addAll(Arrays.asList(oldPassword, newPassword, newPassword2));

        form.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void checkForm() throws Exception {
        super.checkForm();

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        User u = db.userdao().getUserByEmail(intent.getStringExtra("user_email"));

        if(!isPotentialHash(oldPassword.getText().toString(), u.password))
            throw new Exception(getString(R.string.not_matching_password_err));

        verifyPassword(newPassword);
        areTextMatching(newPassword, newPassword2);

        int id = Integer.parseInt(intent.getStringExtra("user_id"));
        db.userdao().updateUserPassword(id, getHashedPassword(newPassword.getText().toString()));
        toastMessage(getString(R.string.chg_password_done));
        finish();
    }
}
