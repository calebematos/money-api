package com.calebematos.api.repository.pessoa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.calebematos.api.model.Pessoa;
import com.calebematos.api.repository.filter.PessoaFilter;

public interface PessoaRepositoryQuery {
	
	Page<Pessoa> filtrar(PessoaFilter filter,Pageable pageable );
}
