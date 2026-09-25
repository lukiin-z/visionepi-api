package br.com.fiap.visionepi.service;

import br.com.fiap.visionepi.model.DeteccaoEpi;
import br.com.fiap.visionepi.repository.DeteccaoEpiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeteccaoEpiServiceTest {

    private static final LocalDateTime DATA_ORIGINAL = LocalDateTime.of(2026, 5, 21, 10, 35);

    @Mock
    private DeteccaoEpiRepository repository;

    @InjectMocks
    private DeteccaoEpiService service;

    // PUT sem dataDeteccao não pode apagar a data (coluna NOT NULL → HTTP 500)
    @Test
    void atualizarSemDataDeteccaoMantemDataOriginal() {
        prepararRegistroExistente();
        DeteccaoEpi dados = DeteccaoEpi.builder().nomeOperador("João Silva").build();

        DeteccaoEpi atualizado = service.atualizar(1L, dados).orElseThrow();

        assertThat(atualizado.getDataDeteccao()).isEqualTo(DATA_ORIGINAL);
        assertThat(atualizado.getNomeOperador()).isEqualTo("João Silva");
    }

    @Test
    void atualizarComDataDeteccaoSubstituiData() {
        prepararRegistroExistente();
        LocalDateTime novaData = LocalDateTime.of(2026, 9, 1, 8, 0);
        DeteccaoEpi dados = DeteccaoEpi.builder().dataDeteccao(novaData).build();

        DeteccaoEpi atualizado = service.atualizar(1L, dados).orElseThrow();

        assertThat(atualizado.getDataDeteccao()).isEqualTo(novaData);
    }

    private void prepararRegistroExistente() {
        DeteccaoEpi existente = DeteccaoEpi.builder().id(1L).dataDeteccao(DATA_ORIGINAL).build();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(DeteccaoEpi.class))).thenAnswer(inv -> inv.getArgument(0));
    }
}
