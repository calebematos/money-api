package com.calebematos.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.calebematos.api.dto.LancamentoEstatisticaPorCategoria;
import com.calebematos.api.dto.LancamentoEstatisticaPorDia;
import com.calebematos.api.dto.LancamentoEstatisticaPorPessoa;
import com.calebematos.api.model.Lancamento;
import com.calebematos.api.repository.filter.LancamentoFilter;
import com.calebematos.api.repository.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {

	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	public List<LancamentoEstatisticaPorCategoria> porCategoria(LocalDate mesReferencia);
	
	public List<LancamentoEstatisticaPorDia> porDia(LocalDate mesReferencia);
	
	public List<LancamentoEstatisticaPorPessoa> porPessoa(LocalDate inicio, LocalDate fim);
}
