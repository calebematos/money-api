package com.calebematos.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calebematos.api.model.Cidade;
import com.calebematos.api.repository.CidadeRepository;

@RestController
@RequestMapping("cidades")
public class CidadeResource {

	@Autowired
	private CidadeRepository cidadeRepository;

	@GetMapping("/{estado}")
	public List<Cidade> pesquisarPorEstado(@PathVariable Long estado) {
		return cidadeRepository.findByEstadoCodigo(estado);
	}

}
