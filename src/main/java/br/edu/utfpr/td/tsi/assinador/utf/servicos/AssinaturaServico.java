package br.edu.utfpr.td.tsi.assinador.utf.servicos;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;

@Service
public class AssinaturaServico {

	private final String caminhoCertificado = "./certificado.pfx";
	private final String senhaCertificado = "123456";

	public byte[] assinarDocumento(MultipartFile pdfFile) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyStore keystore = KeyStore.getInstance("PKCS12");

			try (FileInputStream fis = new FileInputStream(caminhoCertificado)) {
				keystore.load(fis, senhaCertificado.toCharArray());
			}

			String alias = keystore.aliases().nextElement();
			PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, senhaCertificado.toCharArray());
			Certificate[] chain = keystore.getCertificateChain(alias);

			try (InputStream pdfInput = pdfFile.getInputStream();
					ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream()) {

				PdfReader reader = new PdfReader(pdfInput);
				PdfSigner signer = new PdfSigner(reader, pdfOutput, new StampingProperties());

				signer.getSignatureAppearance().setPageRect(new Rectangle(36, 36, 200, 100))
						.setSignatureGraphic(
								ImageDataFactory.create("./src/main/java/projeto/extensao/assets/assinatura_logo.jpeg"))
						.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC).setPageNumber(1)
						.setReason("Assinatura Digital").setLocation("UTFPR - Assinador");

				signer.setFieldName("assinatura_digital");

				IExternalSignature pks = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256,
						BouncyCastleProvider.PROVIDER_NAME);
				IExternalDigest digest = new BouncyCastleDigest();

				signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

				return pdfOutput.toByteArray();
			}

		} catch (Exception e) {
			throw new RuntimeException("Erro ao assinar PDF", e);
		}
	}

}