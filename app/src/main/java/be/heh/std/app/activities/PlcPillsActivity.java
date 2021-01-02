package be.heh.std.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.HashMap;

import be.heh.std.app.R;
import be.heh.std.app.databinding.ActivityPlcPillsBinding;
import be.heh.std.model.core.read.ReadPillsTask;
import be.heh.std.model.core.write.WritePillsTask;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.PlcConf;
import be.heh.std.model.database.Role;
import be.heh.std.model.database.User;

public class PlcPillsActivity extends AppCompatActivity {

    private Intent intent;
    private AppDatabase db;
    private PlcConf current_conf;
    private User current_user;
    private ReadPillsTask readS7;
    private WritePillsTask writeS7;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;
    private  ActivityPlcPillsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plc_pills);
        db = AppDatabase.getInstance(this);
        intent = getIntent();
        current_conf = db.plcConfDAO().getConfById(intent.getIntExtra("plc_id", 0));
        current_user = db.userdao().getUserById(intent.getIntExtra("user_id", 0));

        HashMap<String, String> values = new HashMap<>();


        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        binding.setConf(current_conf);
        binding.setUser(current_user);

        if(networkInfo == null || !networkInfo.isConnectedOrConnecting()){
            Toast.makeText(getApplicationContext(), R.string.network_err, Toast.LENGTH_LONG).show();
            finish();
        }

        readS7 = new ReadPillsTask(this, binding.referencePills, binding.inServicePills,
                binding.rSupplyPills, binding.isRemotePills, binding.rNbBottles,
                binding.rNbPills, binding.connectionTestPills, 5);
        readS7.start(current_conf.ip, current_conf.rack, current_conf.slot);

        if(current_user.role != Role.BASIC) {
            writeS7 = new WritePillsTask(5);
            writeS7.start(current_conf.ip, current_conf.rack, current_conf.slot);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(readS7.isReading())
            readS7.stop();
        if(writeS7.isWriting() && current_user.role != Role.BASIC)
            writeS7.stop();
    }

    public void onPlcPillsClickManager(View v) {
        switch (v.getId()) {

        }
    }
}