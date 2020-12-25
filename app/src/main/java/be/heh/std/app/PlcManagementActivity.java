package be.heh.std.app;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;

import be.heh.std.app.databinding.ActivityPlcManagementBinding;
import be.heh.std.app.adapters.PlcConfAdapter;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.PlcConf;

public class PlcManagementActivity extends AppCompatActivity {

    private ActivityPlcManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        ArrayList<PlcConf> confs = new ArrayList<>(db.plcConfDAO().getAllConfs());
        if(confs.isEmpty()) confs.add(new PlcConf());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plc_management);
        PlcConfAdapter adapter = new PlcConfAdapter(confs);
        binding.confList.setAdapter(adapter);
        binding.setIsListmpty(confs.isEmpty());

    }

    public void onPlcManageClick(View v) {
        switch (v.getId()) {
            case R.id.plc_management_back:
                finish();
            break;
        }
    }

}