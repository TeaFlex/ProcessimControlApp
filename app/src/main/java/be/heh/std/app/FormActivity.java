package be.heh.std.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class FormActivity extends Activity {

    protected View form;
    protected TextView title;
    protected TextView info_msg;
    protected TextView error_msg;
    protected Button submit;
    protected HashMap<String, EditText> notEmptyInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        title = (TextView) findViewById(R.id.form_title);
        info_msg = (TextView) findViewById(R.id.info_msg);
        error_msg = (TextView) findViewById(R.id.error_msg);
        notEmptyInputs = new HashMap();
    }

    public void onFormClickManager(View v) throws Exception {
        if(v.getId() == submit.getId()) checkForm();
    }

    public void checkForm() throws Exception {
        for (Map.Entry<String, EditText> entry : notEmptyInputs.entrySet()) {
            if(isEditTextEmpty(entry.getValue()))
                throw new Exception(entry.getKey()+" is empty !");
        }
    }

    public boolean isEditTextEmpty(EditText e) {
        return e.getText().toString().trim().length() == 0;
    }
}
