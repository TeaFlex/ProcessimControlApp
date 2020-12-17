package be.heh.std.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "lastname")
    public String lastname;
    @ColumnInfo(name = "firstname")
    public String firstname;
    @ColumnInfo(name = "email")
    public String email;
    @ColumnInfo(name = "password")
    public String password;
    @ColumnInfo(name = "role")
    public int role;
}
