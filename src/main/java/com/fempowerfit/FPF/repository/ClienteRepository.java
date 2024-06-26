package com.fempowerfit.FPF.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fempowerfit.FPF.Model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByCpf(String cpf);
    void deleteByCpf(String cpf);

    @Query("SELECT a FROM Cliente a WHERE a.cpf = :cpf AND a.senha = :senha")
    Cliente login(@Param("cpf") String cpf, @Param("senha") String senha);
}
