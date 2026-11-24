package net.kb.tpjwt.dto;

public record LoginRequest (
        String email,
        String password
){ }
