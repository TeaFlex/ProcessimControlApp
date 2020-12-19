package be.heh.std.app;

import android.app.Activity;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FormActivity extends Activity {

    protected View form;
    protected TextView title;
    protected TextView info_msg;
    protected TextView error_msg;
    protected Button submit;
    protected ArrayList<View> notEmptyInputs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        title = (TextView) findViewById(R.id.form_title);
        info_msg = (TextView) findViewById(R.id.info_msg);
        error_msg = (TextView) findViewById(R.id.error_msg);
        notEmptyInputs = new ArrayList<>();
    }

    public void onFormClickManager(View v) throws Exception {
        //Submit button automatically check the form.
        if(v.getId() == submit.getId()) checkForm();
    }

    public void checkForm() throws Exception {
        error_msg.setText("");
        for (View v: notEmptyInputs) {
            //Initially check if the text fields that must be filled are not empty.
            if((v instanceof EditText) && (isEditTextEmpty((EditText) v)))
                throw new Exception(String.format(getString(R.string.empty_err),
                        ((EditText)v).getHint()));
        }
    }

    public boolean isEditTextEmpty(EditText e) {
        return e.getText().toString().trim().length() == 0;
    }

    public void verifyLength(EditText in, int min, int max) throws Exception {
        if(in.getText().length() <= min || in.getText().length() >= max)
            throw new Exception(String.format(getString(R.string.between_err),
                    in.getHint(), min, max));
    }

    public void verifyEmail(EditText in) throws Exception {
        if(!(Patterns.EMAIL_ADDRESS.matcher(in.getText()).matches()))
            throw new Exception(String.format(getString(R.string.bad_email_err), in.getHint()));
    }

    public void verifyPassword(EditText in) throws Exception {
        if(!Pattern.matches("^(?=.*[0-9])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", in.getText()))
            throw new Exception(String.format(getString(R.string.bad_password_err),
                    in.getHint(), 8, 1, 1));
    }

    public void checkMatch(EditText one, EditText two) throws Exception {
        if(!one.getText().equals(two.getText()))
            throw new Exception(String.format(getString(R.string.dont_match_err),
                    one.getHint(), two.getHint()));
    }
}
