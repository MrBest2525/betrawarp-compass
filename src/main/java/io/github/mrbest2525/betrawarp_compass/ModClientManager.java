package io.github.mrbest2525.betrawarp_compass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ModClientManager {
    
    private static final Map<UUID, Boolean> HAS_MOD = new ConcurrentHashMap<>();
    
    public static void setHasMod(UUID uuid, boolean b) {
        HAS_MOD.put(uuid, b);
    }
    
    public static boolean hasMod(UUID uuid) {
        return HAS_MOD.getOrDefault(uuid, false);
    }
    
    public static void removeHasMod(UUID uuid) {
        HAS_MOD.remove(uuid);
    }
}
