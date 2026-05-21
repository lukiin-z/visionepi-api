package br.com.fiap.visionepi.service;

import br.com.fiap.visionepi.model.DeteccaoEpi;
import br.com.fiap.visionepi.repository.DeteccaoEpiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Camada de serviço responsável pelas regras de negócio
 * relacionadas às detecções de EPI.
 */
@Service
public class DeteccaoEpiService {

    @Autowired
    private DeteccaoEpiRepository repository;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public DeteccaoEpi salvar(DeteccaoEpi deteccao) {
        return repository.save(deteccao);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<DeteccaoEpi> listarTodas() {
        return repository.findAll();
    }

    public Optional<DeteccaoEpi> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<DeteccaoEpi> buscarPorMatricula(String matricula) {
        return repository.findByMatricula(matricula);
    }

    public List<DeteccaoEpi> buscarPorSetor(String setor) {
        return repository.findBySetor(setor);
    }

    public List<DeteccaoEpi> buscarPorNivelRisco(Integer nivelMinimo) {
        return repository.findByNivelRiscoGreaterThanEqual(nivelMinimo);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public Optional<DeteccaoEpi> atualizar(Long id, DeteccaoEpi dados) {
        return repository.findById(id).map(existente -> {
            existente.setNomeOperador(dados.getNomeOperador());
            existente.setMatricula(dados.getMatricula());
            existente.setSetor(dados.getSetor());
            existente.setDescricaoCamera(dados.getDescricaoCamera());
            existente.setDataDeteccao(dados.getDataDeteccao());
            existente.setCapacete(dados.getCapacete());
            existente.setColete(dados.getColete());
            existente.setLuva(dados.getLuva());
            existente.setOculos(dados.getOculos());
            existente.setBotina(dados.getBotina());
            existente.setConfianca(dados.getConfianca());
            return repository.save(existente);
        });
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deletar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
