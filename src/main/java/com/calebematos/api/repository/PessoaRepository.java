package com.calebematos.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.calebematos.api.model.Pessoa;
import com.calebematos.api.repository.pessoa.PessoaRepositoryQuery;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>, PessoaRepositoryQuery{

}
