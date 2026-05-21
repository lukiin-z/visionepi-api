package br.com.fiap.visionepi.repository;

import br.com.fiap.visionepi.model.DeteccaoEpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA para a entidade DeteccaoEpi.
 * Herda os métodos padrão de CRUD do JpaRepository e
 * declara consultas derivadas específicas do domínio.
 */
@Repository
public interface DeteccaoEpiRepository extends JpaRepository<DeteccaoEpi, Long> {

    /** Detecções de um operador específico pela matrícula. */
    List<DeteccaoEpi> findByMatricula(String matricula);

    /** Detecções de um setor específico. */
    List<DeteccaoEpi> findBySetor(String setor);

    /** Detecções com nível de risco igual ou acima do informado. */
    List<DeteccaoEpi> findByNivelRiscoGreaterThanEqual(Integer nivelRisco);

    /** Detecções onde o capacete está ausente. */
    List<DeteccaoEpi> findByCapacete(String capacete);

    /** Detecções realizadas dentro de um intervalo de tempo. */
    List<DeteccaoEpi> findByDataDeteccaoBetween(LocalDateTime inicio, LocalDateTime fim);
}
