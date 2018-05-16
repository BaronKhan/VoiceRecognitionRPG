package call.actions;

import call.entities.Audio;
import call.entities.Video;
import com.khan.baron.vcw.Action;
import com.khan.baron.vcw.Entity;
import com.khan.baron.vcw.GlobalState;

public class MuteContact extends Action {
    public Object execute(GlobalState state, Entity currentTarget) {
        if (currentTarget instanceof Video) { return "MUTE_VIDEO_"+Action.getCurrentContext().getName().toUpperCase(); }
        if (currentTarget instanceof Audio) { return "MUTE_AUDIO_"+Action.getCurrentContext().getName().toUpperCase(); }
        return "MUTE";
    }
}
