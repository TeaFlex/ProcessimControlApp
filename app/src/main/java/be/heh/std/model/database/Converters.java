package be.heh.std.model.database;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static Role fromString(String value) {
        for (Role r: Role.values())
            if(r.name().equals(value)) return r;
        return null;
    }
    @TypeConverter
    public static String roleToString(Role role) {
        return role.name();
    }
}
