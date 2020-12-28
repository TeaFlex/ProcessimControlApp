package be.heh.std.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import be.heh.std.app.R;
import be.heh.std.app.databinding.ItemUserBinding;
import be.heh.std.model.database.User;

public class UserAdapter extends BaseAdapter {
    private ArrayList<User> users;
    private LayoutInflater layoutInflater;

    public UserAdapter(ArrayList<User> users) {
        this.users = users;
    }
    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).id;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View result = view;
        ItemUserBinding binding;
        User current_user = users.get(position);
        if(view == null) {
            if(layoutInflater == null) {
                layoutInflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            binding = ItemUserBinding.inflate(layoutInflater, parent, false);
            result = binding.getRoot();
            result.setTag(binding);
        }
        else
            binding = (ItemUserBinding) result.getTag();
        binding.userDel.setTag(current_user.id);
        result.setTag(R.id.user_del, current_user.id);
        binding.setUser(current_user);
        return result;
    }
}
