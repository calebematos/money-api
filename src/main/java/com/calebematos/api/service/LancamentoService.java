package com.calebematos.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.calebematos.api.dto.LancamentoEstatisticaPorPessoa;
import com.calebematos.api.mail.Mailer;
import com.calebematos.api.model.Lancamento;
import com.calebematos.api.model.Pessoa;
import com.calebematos.api.model.Usuario;
import com.calebematos.api.repository.LancamentoRepository;
import com.calebematos.api.repository.PessoaRepository;
import com.calebematos.api.repository.UsuarioRepository;
import com.calebematos.api.service.exception.EntidadeNaoExiteException;
import com.calebematos.api.service.exception.PessoaInexistenteOuInativaException;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {

	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
	
	private static final String PERMISSAO="ROLE_PESQUISAR_LANCAMENTO";
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private Mailer mailer;

	@Scheduled(cron="0 0 6 * * *")
	public void avisarSobreLancamentosVencidos() {
		
		List<Lancamento> vencidos = lancamentoRepository.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		if(vencidos.isEmpty()){
			logger.info("Sem lançamentos vencidos");
			return ;
		}
		
		logger.info("Existem {} lancamentos vencidos", vencidos.size());
		
		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao(PERMISSAO);
		
		if(destinatarios.isEmpty()) {
			logger.warn("Existem lancamentos vencidos, mas nenhum destinatário foi encontrado");
			return;
		}
		
		mailer.avisarSobreLancamentosVencidos(destinatarios, vencidos);
		
		logger.info("Envio de e-mail concluído");
	}
	
	public Lancamento salvar(Lancamento lancamento) {

		Pessoa pessoa = pessoaRepository.getOne(lancamento.getPessoa().getCodigo());

		if (pessoa == null || pessoa.isInativo())
			throw new PessoaInexistenteOuInativaException();

		return lancamentoRepository.save(lancamento);
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		
		if(!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

		return lancamentoRepository.save(lancamentoSalvo);
	}
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
		List<LancamentoEstatisticaPorPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(inicio));
		parametros.put("DT_FIM", Date.valueOf(fim));
		parametros.put("REPORT_LOCALE", LocaleContextHolder.getLocale());
		
		InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/lancamentos-por-pessoa.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, new JRBeanCollectionDataSource(dados));
		
		return JasperExportManager.exportReportToPdf(jasperPrint);
		
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		if(lancamento.getPessoa().getCodigo() != null)
			pessoa = pessoaRepository.getOne(lancamento.getPessoa().getCodigo());
		
		if(pessoa == null || pessoa.isInativo())
			throw new PessoaInexistenteOuInativaException();
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
		Lancamento lancamento = lancamentoRepository.findById(codigo).orElseThrow(() -> new EntidadeNaoExiteException());
		return lancamento;
	}

}
