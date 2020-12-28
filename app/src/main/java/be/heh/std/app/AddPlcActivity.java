package be.heh.std.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;

import be.heh.std.model.database.PlcType;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.PlcConf;

public class AddPlcActivity extends FormActivity {

    private EditText ip;
    private EditText rack;
    private EditText slot;
    private Spinner type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        form = findViewById(R.id.plc_add_form);
        submit = (Button) findViewById(R.id.addPlc_accept);
        cancel = (Button) findViewById(R.id.addPlc_cancel);

        ip = (EditText) findViewById(R.id.addPlc_ip);
        rack = (EditText) findViewById(R.id.addPlc_rack);
        slot = (EditText) findViewById(R.id.addPlc_slot);
        type = (Spinner) findViewById(R.id.addPlc_type);

        notEmptyInputs.addAll(Arrays.asList(ip, rack, slot));
        form.setVisibility(View.VISIBLE);
        type.setAdapter(new ArrayAdapter<PlcType>(this,
                R.layout.support_simple_spinner_dropdown_item, PlcType.values()));
    }

    @Override
    public void checkForm() throws Exception {
        super.checkForm();

        verifyIp(ip);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        PlcConf plcConf = new PlcConf();
        plcConf.ip = ip.getText().toString();
        plcConf.slot = slot.getText().toString();
        plcConf.rack = rack.getText().toString();
        plcConf.type = PlcType.valueOf(type.getSelectedItem().toString());
        db.plcConfDAO().addConf(plcConf);
        toastMessage(getString(R.string.plc_added));
        finish();
    }
}