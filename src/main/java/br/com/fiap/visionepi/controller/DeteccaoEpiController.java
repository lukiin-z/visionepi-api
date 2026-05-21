package br.com.fiap.visionepi.controller;

import br.com.fiap.visionepi.model.DeteccaoEpi;
import br.com.fiap.visionepi.service.DeteccaoEpiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST da entidade DeteccaoEpi.
 * Expõe os endpoints para gerenciamento das detecções de EPI
 * realizadas pelo sistema de visão computacional VisionEPI.
 */
@RestController
@RequestMapping("/deteccoes")
public class DeteccaoEpiController {

    @Autowired
    private DeteccaoEpiService service;

    // ── POST /deteccoes ───────────────────────────────────────────────────────
    // Registra uma nova detecção de EPI

    @PostMapping
    public ResponseEntity<DeteccaoEpi> criar(@RequestBody @Valid DeteccaoEpi deteccao) {
        DeteccaoEpi salvo = service.salvar(deteccao);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // ── GET /deteccoes ────────────────────────────────────────────────────────
    // Lista todas as detecções; aceita filtros opcionais por query param

    @GetMapping
    public ResponseEntity<List<DeteccaoEpi>> listar(
            @RequestParam(required = false) String setor,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) Integer nivelRiscoMinimo) {

        List<DeteccaoEpi> resultado;

        if (matricula != null) {
            resultado = service.buscarPorMatricula(matricula);
        } else if (setor != null) {
            resultado = service.buscarPorSetor(setor);
        } else if (nivelRiscoMinimo != null) {
            resultado = service.buscarPorNivelRisco(nivelRiscoMinimo);
        } else {
            resultado = service.listarTodas();
        }

        return ResponseEntity.ok(resultado);
    }

    // ── GET /deteccoes/{id} ───────────────────────────────────────────────────
    // Busca uma detecção específica pelo ID

    @GetMapping("/{id}")
    public ResponseEntity<DeteccaoEpi> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── PUT /deteccoes/{id} ───────────────────────────────────────────────────
    // Atualiza os dados de uma detecção existente

    @PutMapping("/{id}")
    public ResponseEntity<DeteccaoEpi> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DeteccaoEpi deteccao) {

        return service.atualizar(id, deteccao)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── DELETE /deteccoes/{id} ────────────────────────────────────────────────
    // Remove uma detecção pelo ID

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
