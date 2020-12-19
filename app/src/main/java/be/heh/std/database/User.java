package be.heh.std.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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
    @NonNull @ColumnInfo(defaultValue = "BASIC")
    public String role;
}
