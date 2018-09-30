package com.calebematos.api.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.calebematos.api.model.Lancamento;
import com.calebematos.api.model.Usuario;

@Component
public class Mailer {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine thymeleaf;
	
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//	String template = "mail/aviso-lancamentos-vencidos";
//	
//	List<Lancamento> lista = repo.findAll();
//	
//	Map<String, Object> variaveis = new HashMap<>();
//	variaveis.put("lancamentos", lista);
//		enviarEmail("calebematos@gmail.com", Arrays.asList("calebematos@gmail.com"), "teste", template, variaveis);
//		System.out.println("deu boa o email");
//	}
	
	public void avisarSobreLancamentosVencidos(List<Usuario> destinatarios, List<Lancamento> vencidos) {
		List<String> emails = destinatarios.stream().map(u -> u.getEmail()).collect(Collectors.toList());
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("lancamentos", vencidos);
		
		enviarEmail("calebematos@gmail.com", emails, "Lancamentos vencidos", "mail/aviso-lancamentos-vencidos", variaveis);
	}

	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String template, Map<String, Object> variaveis) {
		Context context = new Context(LocaleContextHolder.getLocale());
		
		variaveis.entrySet().forEach(e -> context.setVariable(e.getKey(), e.getValue()));
		
		String mensagem = thymeleaf.process(template, context);
		
		enviarEmail(remetente, destinatarios, assunto, mensagem);
	}
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem) {

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setText(mensagem, true);

			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Problemas com o envio de email", e);
		}
	}
}
