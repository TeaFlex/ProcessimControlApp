package be.heh.std.model.database;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static Role rolefromString(String value) {
        for (Role r: Role.values())
            if(r.name().equals(value)) return r;
        return null;
    }
    @TypeConverter
    public static String roleToString(Role role) {
        return role.name();
    }

    @TypeConverter
    public static PlcType plcTypefromString(String value) {
        for (PlcType p: PlcType.values())
            if(p.name().equals(value)) return p;
        return null;
    }
    @TypeConverter
    public static String plcTypeToString(PlcType plcType) {
        return plcType.name();
    }
}
