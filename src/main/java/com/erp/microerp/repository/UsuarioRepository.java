package com.erp.microerp.repository;

import com.erp.microerp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByUsernameAndSenha(String username, String senha);

}