package br.com.backend.cervejaria.repository;

import br.com.backend.cervejaria.entity.Cerveja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CervejaRepository  extends JpaRepository<Cerveja, Long> {

    Optional<Cerveja> encontrarPorNome(String nome);

}
