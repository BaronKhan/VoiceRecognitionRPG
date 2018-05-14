package com.khan.baron.voicerecrpg.call;

import com.khan.baron.voicerecrpg.system.Entity;

public class Contact extends Entity {
    public Contact(String firstName, String ... otherNames) {
        super(firstName, otherNames);
        setContext("contact");
    }

    public Contact(String name) {
        super(name);
        setContext("contact");
    }
}
