package call.actions;

import call.entities.Contact;
import com.khan.baron.vcw.Action;
import com.khan.baron.vcw.Entity;
import com.khan.baron.vcw.GlobalState;

public class CallContactAudio extends Action {
    public Object execute(GlobalState state, Entity currentTarget) {
        if (currentTarget instanceof Contact) { return "CALL_"+currentTarget.getName().toUpperCase()+"_AUDIO"; }
        return "CALL_AUDIO";
    }
}

