package call;

import call.actions.*;
import com.khan.baron.vcw.ContextActionMap;
import com.khan.baron.vcw.GlobalState;
import game.actions.*;

public class CallContextActionMap extends ContextActionMap {
    public CallContextActionMap(GlobalState state) {
        super(state);
        setActionList(               "call",                    "stop",                 "mute");
        addDefaultContextActions(    new CallContact(),         new StopCall(),         new Mute());
        addContextActions("video",   new CallContactVideo(),    null,                   null);
        addContextActions("audio",   new CallContactAudio(),    null,                   null);
        addContextActions("contact", null,                      new StopCallContact(),  new MuteContact());
    }
}

