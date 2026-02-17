package com.sdl.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizationEvents {


    @EventListener
    public void onAuthorizationFailure(Object principal) {

    }
}
