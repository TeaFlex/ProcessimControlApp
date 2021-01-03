package be.heh.std.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.HashMap;

import be.heh.std.app.R;
import be.heh.std.app.databinding.ActivityPlcLiquidBinding;
import be.heh.std.model.core.read.ReadLiquidTask;
import be.heh.std.model.core.write.WriteLiquidTask;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.PlcConf;
import be.heh.std.model.database.Role;
import be.heh.std.model.database.User;

public class PlcLiquidActivity extends AppCompatActivity {

    private Intent intent;
    private AppDatabase db;
    private PlcConf current_conf;
    private User current_user;
    private ReadLiquidTask readS7;
    private WriteLiquidTask writeS7;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;
    private ActivityPlcLiquidBinding binding;
    private int datablock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plc_liquid);
        db = AppDatabase.getInstance(this);
        intent = getIntent();
        current_conf = db.plcConfDAO().getConfById(intent.getIntExtra("plc_id", 0));
        current_user = db.userDAO().getUserById(intent.getIntExtra("user_id", 0));
        datablock = Integer.parseInt(current_conf.data_block);

        HashMap<Integer, TextView> valves = new HashMap<>();
        valves.put(1, binding.rValve1Liquid);
        valves.put(2, binding.rValve2Liquid);
        valves.put(3, binding.rValve3Liquid);
        valves.put(4, binding.rValve4Liquid);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        binding.setConf(current_conf);
        binding.setUser(current_user);

        binding.wLabelMDepositLiquid.setText(getString(R.string.manual_deposit, 0));
        binding.wManualDepositLiquid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener (){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                writeS7.setWordAtDbw(progress, 0, 28);
                binding.wLabelMDepositLiquid.setText(getString(R.string.manual_deposit, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        binding.wLabelPilotLiquid.setText(getString(R.string.pilot, 0));
        binding.wPilotLiquid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener (){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                writeS7.setWordAtDbw(progress, 0, 30);
                binding.wLabelPilotLiquid.setText(getString(R.string.pilot, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        if(networkInfo == null || !networkInfo.isConnectedOrConnecting()){
            Toast.makeText(getApplicationContext(), R.string.network_err,
                    Toast.LENGTH_LONG).show();
            finish();
        }

        readS7 = new ReadLiquidTask(this, valves, binding.rLvlLiquid, binding.reference,
                binding.rModeLiquid, binding.rPilotLiquid, binding.rAutoLiquid,
                binding.rManualLiquid, binding.rIsRemoteLiquid, binding.connectionTestLiquid, datablock);
        readS7.start(current_conf.ip, current_conf.rack, current_conf.slot);

        if(current_user.role != Role.BASIC) {
            writeS7 = new WriteLiquidTask(datablock);
            writeS7.start(current_conf.ip, current_conf.rack, current_conf.slot);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        readS7.stop();
        if(current_user.role != Role.BASIC)
            writeS7.stop();
    }

    public void onPlcLiquidClickManager(View v) {
        try {
            boolean value = false;
            switch (v.getId()) {
                case R.id.w_manual_auto_liquid:
                    value = binding.wManualAutoLiquid.isChecked();
                    writeS7.setWriteBoolDbb(5, value, 2);
                    break;

                case R.id.w_valve1_liquid:
                    value = binding.wValve1Liquid.isChecked();
                    writeS7.setWriteBoolDbb(1, value, 2);
                    break;

                case R.id.w_valve2_liquid:
                    value = binding.wValve2Liquid.isChecked();
                    writeS7.setWriteBoolDbb(2, value, 2);
                    break;

                case R.id.w_valve3_liquid:
                    value = binding.wValve3Liquid.isChecked();
                    writeS7.setWriteBoolDbb(3, value, 2);
                    break;

                case R.id.w_valve4_liquid:
                    value = binding.wValve4Liquid.isChecked();
                    writeS7.setWriteBoolDbb(4, value, 2);
                    break;
            }
            Log.i("INPUT" , String.valueOf(v.getId()));
            Log.i("bool" , String.valueOf(value));
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}