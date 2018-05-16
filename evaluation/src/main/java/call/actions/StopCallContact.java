package call.actions;

import call.entities.Conversation;
import com.khan.baron.vcw.Action;
import com.khan.baron.vcw.Entity;
import com.khan.baron.vcw.GlobalState;

public class StopCallContact extends Action {
    public Object execute(GlobalState state, Entity currentTarget) {
        if (currentTarget instanceof Conversation) { return "STOP_CALL_"+Action.getCurrentContext().getName().toUpperCase(); }
        return "STOP_"+Action.getCurrentContext().getName().toUpperCase();
    }
}
