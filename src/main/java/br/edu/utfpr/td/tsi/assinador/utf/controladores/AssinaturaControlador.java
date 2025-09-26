package br.edu.utfpr.td.tsi.assinador.utf.controladores;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.utfpr.td.tsi.assinador.utf.servicos.AssinaturaServico;

@RestController
@RequestMapping("/assinatura")
public class AssinaturaControlador {

	@Autowired
	AssinaturaServico assinaturaServico;

	@PostMapping(consumes = "multipart/form-data", path = "assinar")
	public ResponseEntity<?> assinarDocumento(@RequestParam("file") MultipartFile file) {
		byte[] pdfAssinado = assinaturaServico.assinarDocumento(file);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documento_assinado.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdfAssinado);
	}
}
