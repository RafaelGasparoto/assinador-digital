package br.edu.utfpr.td.tsi.assinador.utf.servicos;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.signatures.*;

import br.edu.utfpr.td.tsi.assinador.utf.entidades.Assinatura;
import br.edu.utfpr.td.tsi.assinador.utf.entidades.Usuario;
import br.edu.utfpr.td.tsi.assinador.utf.repositorios.AssinaturaRepositorio;
import br.edu.utfpr.td.tsi.assinador.utf.repositorios.UsuarioRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

@Service
public class AssinaturaServico {

	@Autowired
	private AssinaturaRepositorio assinaturaRepositorio;

	public byte[] assinarDocumento(MultipartFile pdfFile, Usuario usuario) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyStore keystore = gerarCertificadoTemporario(usuario);

			String alias = keystore.aliases().nextElement();
			PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, "123456".toCharArray());
			Certificate[] chain = keystore.getCertificateChain(alias);

			try (InputStream pdfInput = pdfFile.getInputStream();
					ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream()) {

				PdfReader reader = new PdfReader(pdfInput);
				PdfSigner signer = new PdfSigner(reader, pdfOutput, new StampingProperties());

				signer.getSignatureAppearance().setPageRect(new Rectangle(36, 36, 200, 100))
						.setSignatureGraphic(
								ImageDataFactory.create("./src/main/java/projeto/extensao/assets/assinatura_logo.jpeg"))
						.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION)
						.setReason("Assinado digitalmente por " + usuario.getNome()).setLocation("UTFPR - Assinador")
						.setPageNumber(1);

				signer.setFieldName("assinatura_digital");

				IExternalSignature pks = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, "BC");
				IExternalDigest digest = new BouncyCastleDigest();

				signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

				salvarInfoAssinatura(usuario, (X509Certificate) chain[0], pdfOutput.toByteArray());

				return pdfOutput.toByteArray();
			}

		} catch (Exception e) {
			throw new RuntimeException("Erro ao assinar PDF", e);
		}
	}

	private KeyStore gerarCertificadoTemporario(Usuario usuario) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		X500Name issuer = new X500Name("CN=" + usuario.getNome() + ", EMAILADDRESS=" + usuario.getEmail());
		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
		Date inicio = new Date(System.currentTimeMillis() - 1000L);
		Date fim = new Date(System.currentTimeMillis() + 1000L * 2);

		JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer, serial, inicio, fim, issuer,
				keyPair.getPublic());

		ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC")
				.build(keyPair.getPrivate());

		X509CertificateHolder certHolder = builder.build(signer);
		X509Certificate certificado = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(null, null);
		ks.setKeyEntry("temp", keyPair.getPrivate(), "123456".toCharArray(), new Certificate[] { certificado });

		return ks;
	}

	private void salvarInfoAssinatura(Usuario usuario, X509Certificate certificado, byte[] pdfAssinado)
			throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		// Hash do certificado (opcional)
		String hashCertificado = Base64.getEncoder().encodeToString(digest.digest(certificado.getEncoded()));

		// Hash do PDF assinado (principal)
		String hashPdf = Base64.getEncoder().encodeToString(digest.digest(pdfAssinado));

		Assinatura assinatura = new Assinatura();
		assinatura.setUsuario(usuario);
		assinatura.setHashCertificado(hashCertificado);
		assinatura.setHashPdfAssinado(hashPdf);
		assinatura.setDataAssinatura(LocalDateTime.now());

		assinaturaRepositorio.save(assinatura);
	}
	
	public boolean validarAssinaturaDocumento(MultipartFile pdfAssinado, Usuario usuario) {
	    try {
	    	
	       
	    	// Calcula o hash do arquivo recebido
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hashBytes = digest.digest(pdfAssinado.getBytes());
	        String hashBase64 = Base64.getEncoder().encodeToString(hashBytes);

	        // Busca a assinatura com o mesmo usuário e hash
	        boolean existe = assinaturaRepositorio.existsByUsuarioAndHashPdfAssinado(usuario, hashBase64);

	        return existe;
	    } catch (Exception e) {
	        throw new RuntimeException("Erro ao validar hash do documento", e);
	    }
	}

	public X509Certificate extrairCertificadoDoPdf(InputStream pdfStream) {
	    try {
	        Security.addProvider(new BouncyCastleProvider());

	        try (PdfReader reader = new PdfReader(pdfStream);
	             PdfDocument pdfDoc = new PdfDocument(reader)) {

	            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
	            List<String> sigNames = signUtil.getSignatureNames();

	            if (sigNames == null || sigNames.isEmpty()) {
	                return null;
	            }

	            for (String sigName : sigNames) {
	                try {
	                    PdfPKCS7 pkcs7 = signUtil.readSignatureData(sigName);
	                    if (pkcs7 != null && pkcs7.getSigningCertificate() != null) {
	                        return (X509Certificate) pkcs7.getSigningCertificate();
	                    }
	                } catch (Exception e) {
	                    // ignora assinaturas inválidas
	                    continue;
	                }
	            }
	        }

	        return null;

	    } catch (Exception e) {
	        throw new RuntimeException("Erro ao extrair certificado do PDF", e);
	    }
	}

	public String extrairEmailDoPdf(InputStream pdfStream) {
	    try {
	        X509Certificate cert = extrairCertificadoDoPdf(pdfStream);
	        if (cert == null) return null;

	        X500Name x500name = new X500Name(cert.getSubjectX500Principal().getName());
	        RDN[] rdns = x500name.getRDNs(new ASN1ObjectIdentifier("1.2.840.113549.1.9.1"));
	        if (rdns != null && rdns.length > 0) {
	            String email = IETFUtils.valueToString(rdns[0].getFirst().getValue());
	            return email;
	        }

	        return null;

	    } catch (Exception e) {
	        throw new RuntimeException("Erro ao extrair e-mail do PDF", e);
	    }
	}
}