package br.edu.utfpr.td.tsi.assinador.utf.repositorios;

import br.edu.utfpr.td.tsi.assinador.utf.entidades.Assinatura;
import br.edu.utfpr.td.tsi.assinador.utf.entidades.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssinaturaRepositorio extends JpaRepository<Assinatura, Long> {
    boolean existsByUsuarioAndHashPdfAssinado(Usuario usuario, String hashPdfAssinado);
}
