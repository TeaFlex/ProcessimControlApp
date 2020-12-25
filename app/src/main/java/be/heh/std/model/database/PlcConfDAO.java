package be.heh.std.model.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlcConfDAO {

    @Insert
    void addConf(PlcConf conf);

    @Delete
    void deleteConf(PlcConf conf);

    @Query("DELETE FROM plcconf WHERE id = :id;")
    void deleteConfById(int id);

    @Query("SELECT * FROM plcconf;")
    List<PlcConf> getAllConfs();

    @Query("SELECT * FROM plcconf WHERE id = :id;")
    PlcConf getConfById(int id);
}
