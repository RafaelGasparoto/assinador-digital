package projeto.extensao.assinador;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.Certificate;

public class Assinador {

    public static void main(String[] args) {
        try {
            String caminhoCertificado = "./certificado.pfx";
            String senhaCertificado = "123456";

            String pdfOriginal = "./documento_original.pdf";
            String pdfAssinado = "./documento_assinado.pdf";

            // Carrega a biblioteca BouncyCastle, é usada para criptograr/assinar
            Security.addProvider(new BouncyCastleProvider());

            // Cria um KeyStore do tipo PKCS12 (é o tipo do arquivo de certificado digital
            // .pfx),
            // que armazena o certificado digital e a chave privada
            // Serve para manipular recuperando informações do certificado
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(caminhoCertificado);
            keystore.load(fis, senhaCertificado.toCharArray());
            String alias = keystore.aliases().nextElement();

            // Recupera a chave privada do certificado
            PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, senhaCertificado.toCharArray());
            // Recupera a cadeia de certificados, cadeia pois tem o meu certificado e o
            // certificado da empresa que emitiu o certificado do meu certificado
            // no caso ainda não tem mas é necessário ser a cadeia
            Certificate[] chain = keystore.getCertificateChain(alias);

            // Lê o documento PDF original
            PdfReader reader = new PdfReader(pdfOriginal);
            // Cria um novo PDF que vai ser assinado
            FileOutputStream os = new FileOutputStream(pdfAssinado);
            // Cria instância do assinador de PDF
            // StampingProperties serve para configurar o assinador, o próprio iText
            // configura
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties());

            // Define local que vai assinar de forma visual
            signer.getSignatureAppearance()
                    .setPageRect(new Rectangle(36, 36, 200, 100))
                    .setSignatureGraphic(ImageDataFactory.create("./src/main/java/projeto/extensao/assets/assinatura_logo.jpeg"))
                    .setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC) 
                    .setPageNumber(1)
                    .setReason("")
                    .setLocation("");

            signer.setFieldName("assinatura_digital");

            // Define o algoritmo de assinatura do hash e a chave privada
            IExternalSignature pks = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256,
                    BouncyCastleProvider.PROVIDER_NAME);

            // Serve para gerar o hash do PDF utilizando o algoritmo SHA256 definido no pks
            // é passado uma instância para o algoritmo de assinatura que vai utlizar para
            // gerar o hash
            IExternalDigest digest = new BouncyCastleDigest();

            // Assina o PDF com o padrão CAdES, usando SHA-256 e a chave
            // privada do certificado
            signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

            System.out.println("PDF assinado: " + pdfAssinado);

        } catch (Exception e) {
            System.err.println("Erro ao assinar o PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}