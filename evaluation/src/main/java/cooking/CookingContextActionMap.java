package cooking;

import call.actions.*;
import com.khan.baron.vcw.ContextActionMap;
import com.khan.baron.vcw.GlobalState;

public class CookingContextActionMap extends ContextActionMap {
    public CookingContextActionMap(GlobalState state) {
        super(state);
        setActionList(              "make",                    "stir",                 "boil");
        addDefaultContextActions(   null,         null,         null);
        addContextActions("spoon",  null,    null,                   null);
        addContextActions("cooker", null,    null,                   null);
    }
}
