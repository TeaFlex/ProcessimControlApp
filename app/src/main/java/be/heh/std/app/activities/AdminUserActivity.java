package be.heh.std.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import be.heh.std.app.R;
import be.heh.std.app.adapters.UserAdapter;
import be.heh.std.app.databinding.ActivityAdminUserBinding;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.Role;
import be.heh.std.model.database.User;

public class AdminUserActivity extends AppCompatActivity {

    private ActivityAdminUserBinding binding;
    private AppDatabase db;
    private User current_user;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        db = AppDatabase.getInstance(this);
        current_user = db.userDAO().getUserById(intent.getIntExtra("user_id", 0));
        updateList();

        if(current_user.role != Role.ADMIN) {
            finish();
            Toast.makeText(this, R.string.impossible_err, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    public void onAdminUserClick(View v) {
        switch (v.getId()) {
            case R.id.user_del:
                int received_id = Integer.parseInt(v.getTag().toString());
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.user_del_confirmation,
                                received_id))
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.accept, (dialog, which) -> {
                            deleteElement(received_id);
                            Toast.makeText(this,
                                    getString(R.string.user_deleted, received_id),
                                    Toast.LENGTH_LONG).show();
                        })
                        .create()
                        .show();
                break;

            case R.id.user_item:
                received_id = Integer.parseInt(v.getTag(R.id.user_del).toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.role);

                View customLayout = getLayoutInflater().inflate(R.layout.dialog_role_mod, null);
                Spinner spin = (Spinner) customLayout.findViewById(R.id.spinner_role);
                spin.setAdapter(new ArrayAdapter<Role>(this,
                        R.layout.support_simple_spinner_dropdown_item, Role.values()));
                builder.setView(customLayout);
                builder.setPositiveButton(R.string.accept, ((dialog, which) -> {
                    db.userDAO().updateUserRole(received_id,
                            Role.valueOf(spin.getSelectedItem().toString()));
                    updateList();
                    Toast.makeText(this, getString(R.string.user_role_updated, received_id),
                            Toast.LENGTH_LONG).show();
                }));
                builder.setNegativeButton(R.string.cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.user_admin_back:
                finish();
                break;
        }
    }

    public void updateList(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_user);
        ArrayList<User> users = new ArrayList<>(db.userDAO().getAllUsersExcept(current_user.id));
        UserAdapter adapter = new UserAdapter(users);
        binding.userList.setAdapter(adapter);
        binding.setIsListmpty(users.isEmpty());
        binding.setUser(current_user);
    }

    public void deleteElement(int id) {
        db.userDAO().deleteUserById(id);
        updateList();
    }

    public User getUser(int id) {
        return db.userDAO().getUserById(id);
    }
}