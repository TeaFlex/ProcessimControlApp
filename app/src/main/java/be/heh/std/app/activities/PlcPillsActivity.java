package be.heh.std.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
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
    private int datablock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plc_pills);
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
            Toast.makeText(getApplicationContext(), R.string.network_err, Toast.LENGTH_LONG).show();
            finish();
        }

        readS7 = new ReadPillsTask(binding, this, datablock);
        readS7.start(current_conf.ip, current_conf.rack, current_conf.slot);

        if(current_user.role != Role.BASIC) {
            writeS7 = new WritePillsTask(datablock);
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

    public void onPlcPillsClickManager(View v) {
        try {
            boolean value = false;

            switch (v.getId()) {
                //BIT 0
                case R.id.w_set_service_pills:
                    value = binding.wSetServicePills.isChecked();
                    writeS7.setBitAtDbb(0, value, 0, 5);
                    writeS7.setBitAtDbb(0, value, 0, 6);
                    writeS7.setBitAtDbb(0, value, 0, 7);
                    break;

                //BIT 1
                case R.id.w_gen_bottles_pills:
                    value = binding.wGenBottlesPills.isChecked();

                    writeS7.setBitAtDbb(1, value, 0, 5);
                    writeS7.setBitAtDbb(1, value, 0, 6);
                    writeS7.setBitAtDbb(1, value, 0, 7);
                    break;

                //BIT 2
                case R.id.w_reset_bottles_pills:
                    value = binding.wResetBottlesPills.isChecked();
                    writeS7.setBitAtDbb(2, value, 0, 5);
                    writeS7.setBitAtDbb(2, value, 0, 6);
                    writeS7.setBitAtDbb(2, value, 0, 7);
                    break;

                //BIT 3
                case R.id.w_sw1_pills:
                    value = binding.wSw1Pills.isChecked();
                    writeS7.setBitAtDbb(3, value, 0, 5);
                    writeS7.setBitAtDbb(3, value, 0, 6);
                    writeS7.setBitAtDbb(3, value, 0, 7);
                    break;

                //BIT 4
                case R.id.w_5_pills:
                    value = binding.w5Pills.isChecked();
                    writeS7.setBitAtDbb(4, value, 0, 5);
                    writeS7.setBitAtDbb(4, value, 0, 6);
                    writeS7.setBitAtDbb(4, value, 0, 7);
                    break;

                //BIT 5
                case R.id.w_10_pills:
                    value = binding.w10Pills.isChecked();
                    writeS7.setBitAtDbb(5, value, 0, 5);
                    writeS7.setBitAtDbb(5, value, 0, 6);
                    writeS7.setBitAtDbb(5, value, 0, 7);
                    break;

                //BIT 6
                case R.id.w_15_pills:
                    value = binding.w15Pills.isChecked();
                    writeS7.setBitAtDbb(6, value, 0, 5);
                    writeS7.setBitAtDbb(6, value, 0, 6);
                    writeS7.setBitAtDbb(6, value, 0, 7);
                    break;

                //BIT 7
                case R.id.w_sw2_pills:
                    value = binding.wSw2Pills.isChecked();
                    writeS7.setBitAtDbb(7, value, 0, 5);
                    writeS7.setBitAtDbb(7, value, 0, 6);
                    writeS7.setBitAtDbb(7, value, 0, 7);
                    break;

                //BYTE
                case R.id.w_byte_pills:
                    value = binding.wBytePills.isChecked();
                    writeS7.setByteAtDbb(value, 0, 8);
                    break;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    protected void setSeekers() {
        //INTEGER DBW18
        binding.wIntLabelPills.setText(getString(R.string.int_value, 0));
        binding.wIntPills.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.wIntLabelPills.setText(getString(R.string.int_value, progress));
                writeS7.setWordAtDbw(progress, 0, 18);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}