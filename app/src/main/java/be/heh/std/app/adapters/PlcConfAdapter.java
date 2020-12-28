package be.heh.std.app.adapters;

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
        result.setTag(R.id.plc_del, current_conf.id);
        binding.setPlc(current_conf);
        return result;
    }
}
