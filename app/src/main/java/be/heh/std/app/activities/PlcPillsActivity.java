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

import be.heh.std.app.R;
import be.heh.std.app.databinding.ActivityPlcPillsBinding;
import be.heh.std.model.core.ReadPillsTask;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.PlcConf;
import be.heh.std.model.database.User;

public class PlcPillsActivity extends AppCompatActivity {

    private Intent intent;
    private AppDatabase db;
    private PlcConf current_conf;
    private User current_user;
    private ReadPillsTask readS7;
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
        readS7 = new ReadPillsTask(binding.connectionTestPills);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        binding.setConf(current_conf);
        binding.setUser(current_user);

        if(networkInfo == null || !networkInfo.isConnectedOrConnecting()){
            Toast.makeText(getApplicationContext(), R.string.network_err, Toast.LENGTH_LONG).show();
            finish();
        }

        try {
            readS7.start(current_conf.ip, current_conf.rack, current_conf.slot);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.plc_down_err, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(readS7.isReading())
            readS7.stop();
    }

    public void onPlcPillsClickManager(View v) {
        switch (v.getId()) {

        }
    }
}