package cooking;

import com.khan.baron.vcw.ContextActionMap;
import com.khan.baron.vcw.GlobalState;
import cooking.actions.*;

public class CookingContextActionMap extends ContextActionMap {
    public CookingContextActionMap(GlobalState state) {
        super(state);
        setActionList(              "make",         "stir",          "boil",           "pour",     "serve");
        addDefaultContextActions(   new Make(),     new Stir(),      new Boil(),        new Pour(), new Serve());
        addContextActions("spoon",  new Make(),     new StirSpoon(), null,              null,       null);
        addContextActions("cooker", new Make(),     null,            new BoilCooker(),  null,       null);
        addContextActions("food",   new MakeFood(), null,            null,              null,       null);
    }
}
