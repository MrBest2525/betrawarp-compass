package io.github.mrbest2525.betrawarp_compass;

public class ModTranslationKeys {
    
    /**
     * 文字列結合ヘルパーメゾッド("."結合)
     * @param strings 結合したい文字列 (A, B, C)
     * @return 結合後 "A.B.C"
     */
    private static String join(String... strings) {
        return String.join(".", strings);
    }
    
    
    
    public static final class Menu {
        private static final String BASE = "menu";
        
        public static final class BetrawarpCompass {
            private static final String BASE = join(Menu.BASE, "betrawarp_compass");
            
            public static final class CompassLinker {
                private static final String BASE = join(BetrawarpCompass.BASE, "compass_linker");
                
                public static final String TITLE = join(BASE, "title");
                
                public static final class Info {
                    private static final String BASE = join(CompassLinker.BASE, "info");
                    
                    public static final String NAME = join(BASE, "name");
                    
                    public static final class Lore {
                        private static final String BASE = join(Info.BASE, "lore");
                        
                        public static final String INFO = join(BASE, "info");
                        public static final String TARGET_POS = join(BASE, "target_pos");
                        public static final String ENERGY = join(BASE, "energy");
                    }
                }
            }
        }
    }
    
    public static final class Warp {
        private static final String BASE = "warp";
        
        public static final class BetrawarpCompass {
            private static final String BASE = join(Warp.BASE, "betrawarp_compass");
            
            public static final String DIMENSION_NOT_FOUND = join(BASE, "dimension_not_found");
            public static final String NOT_ENOUGH_ENERGY = join(BASE, "not_enough_energy");
            public static final String NOT_SET_TARGET = join(BASE, "not_set_target");
            public static final String SAFE_POSITION_NOT_FOUND = join(BASE, "safe_position_not_found");
            public static final String WARP_TARGET_NOT_FOUND = join(BASE, "warp_target_not_found");
        }
    }
    
    public static final class Item {
        private static final String BASE = "item";
        
        public static final class BetrawarpCompass {
            private static final String BASE = join(Item.BASE, "betrawarp_compass");
            
            public static final String COMPASS_LINKER = join(BASE, "compass_linker");
        }
    }
    
    public static final class Dimension {
        private static final String BASE = "dimension";
        
        public static final class Minecraft {
            private static final String BASE = join(Dimension.BASE, "minecraft");
            
            public static final String OVERWORLD = join(BASE, "overworld");
            public static final String NETHER = join(BASE, "nether");
            public static final String END = join(BASE, "the_end");
        }
    }
    
    public static final class Tooltip {
        private static final String BASE = "tooltip";
        
        public static final class BetrawarpCompass {
            private static final String BASE = join(Tooltip.BASE, "betrawarp_compass");
            
            public static final String WARP_ENERGY = join(BASE, "warp_energy");
            public static final String TARGET_POSITION = join(BASE, "target_position");
            public static final String TARGET_POSITION_NOT_FOUND = join(BASE, "target_position_not_found");
        }
    }
    
    public static final class CreativeTab {
        private static final String BASE = join("creativeTab");
        
        public static final String BETRAWARP_COMPASS = join(BASE, "betrawarp_compass");
    }
}
