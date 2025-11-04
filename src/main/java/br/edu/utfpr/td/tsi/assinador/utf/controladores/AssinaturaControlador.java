package br.edu.utfpr.td.tsi.assinador.utf.controladores;

import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.utfpr.td.tsi.assinador.utf.entidades.Usuario;
import br.edu.utfpr.td.tsi.assinador.utf.repositorios.UsuarioRepositorio;
import br.edu.utfpr.td.tsi.assinador.utf.servicos.AssinaturaServico;

@RestController
@RequestMapping("/assinatura")
public class AssinaturaControlador {

	@Autowired
	private AssinaturaServico assinaturaServico;

	@Autowired
	private UsuarioRepositorio usuarioRepositorio;

	@PostMapping(value = "/assinar",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<byte[]> assinarDocumento(@RequestParam("arquivo") MultipartFile arquivo,
			Authentication authentication) {

		try {
			// Recupera o e-mail usuário a partir do JWT
			String email = authentication.getName();
			Usuario usuario = usuarioRepositorio.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

			// Assina o PDF
			byte[] pdfAssinado = assinaturaServico.assinarDocumento(arquivo, usuario);

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=assinatura_" + arquivo.getOriginalFilename())
					.contentType(MediaType.APPLICATION_PDF).body(pdfAssinado);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping(value = "/validar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> validarDocumento(@RequestParam("arquivo") MultipartFile arquivo) {
		try {
			String email = assinaturaServico.extrairEmailDoPdf(arquivo.getInputStream());
			
			if (email == null) {
				throw new RuntimeException("Não foi possível identificar o autor no documento");
			}

			Usuario usuario = usuarioRepositorio.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));

			boolean valido = assinaturaServico.validarAssinaturaDocumento(arquivo, usuario);
			if (valido) {
				return ResponseEntity.ok("Documento autêntico e integro. Assinado por " + usuario.getNome());
			} else {
				return ResponseEntity.badRequest().body("Documento inválido");
			}

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Erro ao validar: " + e.getMessage());
		}
	}

}
