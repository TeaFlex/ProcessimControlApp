package be.heh.std.model.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import be.heh.std.model.core.Role;

@Entity(indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String lastname;
    @NonNull
    public String firstname;
    @NonNull
    public String email;
    @NonNull
    public String password;
    @NonNull
    public Role role;
}
