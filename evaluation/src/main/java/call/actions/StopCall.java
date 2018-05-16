package call.actions;

import call.entities.Conversation;
import com.khan.baron.vcw.Action;
import com.khan.baron.vcw.Entity;
import com.khan.baron.vcw.GlobalState;

public class StopCall extends Action {
    public Object execute(GlobalState state, Entity currentTarget) {
        if (currentTarget instanceof Conversation) { return "STOP_CALL"; }
        return "STOP";
    }
}

