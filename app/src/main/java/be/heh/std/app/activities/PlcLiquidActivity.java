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
        binding.setConf(current_conf);
        binding.setUser(current_user);
        setSeekers();
        datablock = Integer.parseInt(current_conf.data_block);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        
        if(networkInfo == null || !networkInfo.isConnectedOrConnecting()){
            Toast.makeText(getApplicationContext(), R.string.network_err,
                    Toast.LENGTH_LONG).show();
            finish();
        }

        readS7 = new ReadLiquidTask(binding, this, datablock);
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

                case R.id.w_valve1_liquid:
                    //BIT 0
                    value = binding.wValve1Liquid.isChecked();
                    writeS7.setBitAtDbb(0, value, 0, 2);
                    writeS7.setBitAtDbb(0, value, 0, 3);
                    break;

                case R.id.w_valve2_liquid:
                    //BIT 1
                    value = binding.wValve2Liquid.isChecked();
                    writeS7.setBitAtDbb(1, value, 0, 2);
                    writeS7.setBitAtDbb(1, value, 0, 3);
                    break;

                case R.id.w_valve3_liquid:
                    //BIT 2
                    value = binding.wValve3Liquid.isChecked();
                    writeS7.setBitAtDbb(2, value, 0, 2);
                    writeS7.setBitAtDbb(2, value, 0, 3);
                    break;

                case R.id.w_valve4_liquid:
                    //BIT 3
                    value = binding.wValve4Liquid.isChecked();
                    writeS7.setBitAtDbb(3, value, 0, 2);
                    writeS7.setBitAtDbb(3, value, 0, 3);
                    break;

                case R.id.w_manual_auto_liquid:
                    //BIT 4
                    value = binding.wManualAutoLiquid.isChecked();
                    writeS7.setBitAtDbb(4, value, 0, 2);
                    writeS7.setBitAtDbb(4, value, 0, 3);
                    break;

                case R.id.w_sw1_liquid:
                    //BIT 5
                    value = binding.wSw1Liquid.isChecked();
                    writeS7.setBitAtDbb(5, value, 0, 2);
                    writeS7.setBitAtDbb(5, value, 0, 3);
                    break;

                case R.id.w_sw2_liquid:
                    //BIT 6
                    value = binding.wSw2Liquid.isChecked();
                    writeS7.setBitAtDbb(6, value, 0, 2);
                    writeS7.setBitAtDbb(6, value, 0, 3);
                    break;

                case R.id.w_sw3_liquid:
                    //BIT 7
                    value = binding.wSw3Liquid.isChecked();
                    writeS7.setBitAtDbb(7, value, 0, 2);
                    writeS7.setBitAtDbb(7, value, 0, 3);
                    break;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    protected void setSeekers() {
        //INT DBW24
        binding.wLabelMDepositLiquid.setText(getString(R.string.manual_deposit, 0));
        binding.wManualDepositLiquid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener (){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                writeS7.setWordAtDbw(progress, 0, 24);
                binding.wLabelMDepositLiquid.setText(getString(R.string.manual_deposit, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //INT DBW26
        binding.wLabelPilotLiquid.setText(getString(R.string.pilot, 0));
        binding.wPilotLiquid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener (){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                writeS7.setWordAtDbw(progress, 0, 26);
                binding.wLabelPilotLiquid.setText(getString(R.string.pilot, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //INT DBW28
        binding.wInt1LabelLiquid.setText(getString(R.string.int_value, 0));
        binding.wInt1Liquid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener (){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                writeS7.setWordAtDbw(progress, 0, 28);
                binding.wInt1LabelLiquid.setText(getString(R.string.int_value, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //INT DBW30
        binding.wInt2LabelLiquid.setText(getString(R.string.int_value, 0));
        binding.wInt2Liquid.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener (){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                writeS7.setWordAtDbw(progress, 0, 30);
                binding.wInt2LabelLiquid.setText(getString(R.string.int_value, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}