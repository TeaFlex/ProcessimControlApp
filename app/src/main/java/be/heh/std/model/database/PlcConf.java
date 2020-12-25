package be.heh.std.model.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import be.heh.std.model.core.PlcType;

@Entity
public class PlcConf {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String ip;
    @NonNull
    public String rack;
    @NonNull
    public String slot;
    @NonNull
    public PlcType type;
}
