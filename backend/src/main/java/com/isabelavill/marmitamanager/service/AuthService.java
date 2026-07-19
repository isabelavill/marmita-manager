package com.isabelavill.marmitamanager.service;

import com.isabelavill.marmitamanager.config.JwtService;
import com.isabelavill.marmitamanager.dto.LoginRequestDTO;
import com.isabelavill.marmitamanager.dto.RegistroRequestDTO;
import com.isabelavill.marmitamanager.dto.TokenResponseDTO;
import com.isabelavill.marmitamanager.entity.Usuario;
import com.isabelavill.marmitamanager.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationManager authenticationManager
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public TokenResponseDTO registrar(RegistroRequestDTO dto) {
        usuarioRepository.findByEmail(dto.email()).ifPresent(u -> {
            throw new IllegalArgumentException("Email já cadastrado: " + dto.email());
        });

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha())); // hash aqui, nunca texto puro

        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario.getEmail());
        return new TokenResponseDTO(token);
    }

    public TokenResponseDTO login(LoginRequestDTO dto) {
        // Isso dispara a validação de fato: busca o usuário via CustomUserDetailsService
        // e compara a senha usando o PasswordEncoder configurado
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.email(), dto.senha())
        );

        String token = jwtService.gerarToken(dto.email());
        return new TokenResponseDTO(token);
    }
}