package com.calebematos.api.repository.lancamento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.calebematos.api.dto.LancamentoEstatisticaPorCategoria;
import com.calebematos.api.dto.LancamentoEstatisticaPorDia;
import com.calebematos.api.dto.LancamentoEstatisticaPorPessoa;
import com.calebematos.api.model.Categoria_;
import com.calebematos.api.model.Lancamento;
import com.calebematos.api.model.Lancamento_;
import com.calebematos.api.model.Pessoa_;
import com.calebematos.api.repository.filter.LancamentoFilter;
import com.calebematos.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;


	@Override
	public List<LancamentoEstatisticaPorCategoria> porCategoria(LocalDate mesReferencia) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<LancamentoEstatisticaPorCategoria> criteria = builder.createQuery(LancamentoEstatisticaPorCategoria.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(builder.construct(LancamentoEstatisticaPorCategoria.class, 
				root.get(Lancamento_.categoria),
				builder.sum(root.get(Lancamento_.valor))
				));
		
		LocalDate primeiroDia = mesReferencia!= null ? mesReferencia.withDayOfMonth(1) : LocalDate.now().withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia!= null ? mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth()) : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()) ;
		
		criteria.where(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
				builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia));
		
		criteria.groupBy(root.get(Lancamento_.categoria));
		
		TypedQuery<LancamentoEstatisticaPorCategoria> query = manager.createQuery(criteria);
		
		return query.getResultList();
	}
	
	@Override
	public List<LancamentoEstatisticaPorDia> porDia(LocalDate mesReferencia) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<LancamentoEstatisticaPorDia> criteria = builder.createQuery(LancamentoEstatisticaPorDia.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(builder.construct(LancamentoEstatisticaPorDia.class, 
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.dataVencimento),
				builder.sum(root.get(Lancamento_.valor))
				));
		
		LocalDate primeiroDia = mesReferencia!= null ? mesReferencia.withDayOfMonth(1) : LocalDate.now().withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia!= null ? mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth()) : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()) ;
		
		criteria.where(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
				builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia));
		
		criteria.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.dataVencimento));
		
		TypedQuery<LancamentoEstatisticaPorDia> query = manager.createQuery(criteria);
		
		return query.getResultList();
	}
	
	@Override
	public List<LancamentoEstatisticaPorPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<LancamentoEstatisticaPorPessoa> criteria = builder.createQuery(LancamentoEstatisticaPorPessoa.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(builder.construct(LancamentoEstatisticaPorPessoa.class, 
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.pessoa),
				builder.sum(root.get(Lancamento_.valor))
				));
		
		criteria.where(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), inicio),
				builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), fim));
		
		criteria.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.pessoa));
		
		TypedQuery<LancamentoEstatisticaPorPessoa> query = manager.createQuery(criteria);
		
		return query.getResultList();
	}
	
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<Lancamento> query = manager.createQuery(criteria);

		adicionarRestricoesDePaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(builder.construct(ResumoLancamento.class
				, root.get(Lancamento_.codigo), root.get(Lancamento_.descricao) 
				, root.get(Lancamento_.dataVencimento), root.get(Lancamento_.dataPagamento) 
				, root.get(Lancamento_.valor), root.get(Lancamento_.tipo)
				,root.get(Lancamento_.categoria).get(Categoria_.nome)
				,root.get(Lancamento_.pessoa).get(Pessoa_.nome)
				));
		
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);

		adicionarRestricoesDePaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		List<Predicate> predicates = new ArrayList<>();

		if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
			predicates.add(builder.like(builder.lower(root.get(Lancamento_.descricao)),
					"%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}
		if (lancamentoFilter.getDataVencimentoDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoDe()));
		}
		if (lancamentoFilter.getDataVencimentoAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoAte()));
		}

		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int primeiraPagina = pageable.getPageNumber();
		int totalDeRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = primeiraPagina * totalDeRegistrosPorPagina;

		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalDeRegistrosPorPagina);
	}

	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}


}
