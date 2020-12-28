package be.heh.std.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import be.heh.std.app.databinding.ActivityPlcPillsBinding;
import be.heh.std.model.core.ReadPillsTask;

public class PlcPillsActivity extends AppCompatActivity {

    private ReadPillsTask readS7;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPlcPillsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_plc_pills);
        readS7 = new ReadPillsTask();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    public void onPlcPillsClickManager(View v) {

    }
}