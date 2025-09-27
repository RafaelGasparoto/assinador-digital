package br.edu.utfpr.td.tsi.assinador.utf.servicos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServico {
	@Autowired
	private JavaMailSender mailSender;

	public void enviarEmail(String para, String assunto, String texto) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(para);
			message.setSubject(assunto);
			message.setText(texto);
			mailSender.send(message);
		} catch (MailException e) {
			System.err.println("Erro ao enviar e-mail para " + para + ": " + e.getMessage());
			throw new RuntimeException("Não foi possível enviar o e-mail. Tente novamente mais tarde.");
		}
	}
}
