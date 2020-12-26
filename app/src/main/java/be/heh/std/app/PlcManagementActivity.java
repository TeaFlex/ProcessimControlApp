package be.heh.std.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;

import be.heh.std.app.adapters.PlcConfAdapter;
import be.heh.std.app.databinding.ActivityPlcManagementBinding;
import be.heh.std.model.core.Role;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.PlcConf;
import be.heh.std.model.database.User;

public class PlcManagementActivity extends AppCompatActivity {

    private ActivityPlcManagementBinding binding;
    private AppDatabase db;
    private User current_user;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        db =  AppDatabase.getInstance(getApplicationContext());
        current_user = db.userdao().getUserById(intent.getIntExtra("user_id", 0));
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
            case R.id.plc_management_add:
                startActivity(new Intent(this, AddPlcActivity.class));
                break;
            case R.id.plc_del:

                int received_id = Integer.parseInt(v.getTag().toString());
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.plc_del_confirmation,
                                getString(R.string.plc_n, received_id)))
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.accept, (dialog, which) -> {
                            deleteElement(received_id);
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.plc_deleted, received_id),
                                    Toast.LENGTH_LONG).show();
                        })
                        .create()
                        .show();

                break;
        }
    }

    public void updateList(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plc_management);
        ArrayList<PlcConf> confs = new ArrayList<>(db.plcConfDAO().getAllConfs());
        PlcConfAdapter adapter = new PlcConfAdapter(confs);
        binding.confList.setAdapter(adapter);
        binding.setIsListmpty(confs.isEmpty());
        binding.setUser(current_user);
    }

    public void deleteElement(int id) {
        db.plcConfDAO().deleteConfById(id);
        updateList();
    }
}