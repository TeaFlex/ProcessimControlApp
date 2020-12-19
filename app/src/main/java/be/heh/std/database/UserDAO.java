package be.heh.std.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert
    void addUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM user WHERE id = :id;")
    void deleteUserById(int id);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE id = :id;")
    User getUserById(int id);

    @Query("SELECT * FROM user WHERE email = :email;")
    User getUserByEmail(String email);

    @Query("SELECT * FROM user WHERE role = :role;")
    List<User> getUsersByRole(int role);

    @Query("SELECT COUNT(*) FROM user;")
    int getCountOfUsers();
}
