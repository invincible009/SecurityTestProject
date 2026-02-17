package com.sdl.dto;

import java.util.Set;

public record UserRecord(String username,
                         String email,
                         boolean enabled,
                         Set<String>  role,
                         Set<String> authority){
}
