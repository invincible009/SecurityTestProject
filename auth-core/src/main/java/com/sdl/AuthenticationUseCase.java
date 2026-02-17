package com.sdl;

import java.util.Map;

public interface AuthenticationUseCase {

    void configureFilterChain(Map<String, String> attributes);
}
