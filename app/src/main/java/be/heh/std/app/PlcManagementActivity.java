package be.heh.std.app;

import android.content.Intent;
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
        updateList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    public void onPlcManageClick(View v) {
        switch (v.getId()) {
            case R.id.plc_management_back:
                finish();
            break;
            case R.id.add_plc:
                startActivity(new Intent(this, AddPlcActivity.class));
                break;
        }
    }

    public void updateList(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plc_management);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        ArrayList<PlcConf> confs = new ArrayList<>(db.plcConfDAO().getAllConfs());
        PlcConfAdapter adapter = new PlcConfAdapter(confs);
        binding.confList.setAdapter(adapter);
        binding.setIsListmpty(confs.isEmpty());
    }
}