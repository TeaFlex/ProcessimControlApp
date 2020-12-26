package be.heh.std.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import be.heh.std.app.R;
import be.heh.std.app.databinding.ItemPlcBinding;
import be.heh.std.model.database.PlcConf;

public class PlcConfAdapter extends BaseAdapter {
    private ArrayList<PlcConf> confs;
    private LayoutInflater layoutInflater;

    public PlcConfAdapter(ArrayList<PlcConf> confs) {
        this.confs = confs;
    }

    @Override
    public int getCount() {
        return confs.size();
    }

    @Override
    public PlcConf getItem(int position) {
        return confs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View result = view;
        ItemPlcBinding binding;
        PlcConf current_conf = confs.get(position);
        if(view == null) {
            if(layoutInflater == null) {
                layoutInflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            binding = ItemPlcBinding.inflate(layoutInflater, parent, false);
            result = binding.getRoot();
            result.setTag(binding);
        }
        else
            binding = (ItemPlcBinding) result.getTag();
        binding.plcDel.setTag(current_conf.id);
        /*result.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

        /*result.setOnClickListener(v -> {
            Context c = parent.getContext();

            String desc = String.format("%d (%s)", current_conf.id, current_conf.ip);
            new AlertDialog.Builder(c)
                    .setMessage(c.getString(R.string.connect_plc_msg, desc))
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.accept, (dialog, which) -> {
                        //TODO: start activity connect to plc

                    })
                    .show();
        });*/
        binding.setPlc(current_conf);
        return result;
    }
}
