package be.heh.std.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import be.heh.std.database.User;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE id = :id")
    User getUserById(int id);

    @Query("SELECT * FROM user WHERE role = :role")
    User getUserByRole(int role);


}
