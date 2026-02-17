package com.sdl.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEvents {

    @EventListener
    public void onAuthenticationSuccess(Object authentication) {

    }

    @EventListener
    public void onAuthenticationFailure(Object authentication) {

    }
}
