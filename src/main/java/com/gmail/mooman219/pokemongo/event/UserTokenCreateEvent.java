package com.gmail.mooman219.pokemongo.event;

import com.gmail.mooman219.pokemongo.UserToken;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class UserTokenCreateEvent {

    public final UserToken token;

    public UserTokenCreateEvent(UserToken token) {
        this.token = token;
    }
}
