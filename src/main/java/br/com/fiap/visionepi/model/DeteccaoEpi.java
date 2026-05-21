package br.com.fiap.visionepi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma detecção de EPI realizada pelo sistema
 * de visão computacional. Cada registro corresponde a um frame analisado
 * por uma câmera, identificando quais EPIs o operador estava utilizando
 * no momento da captura.
 */
@Entity
@Table(name = "deteccao_epi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeteccaoEpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deteccao")
    private Long id;

    // ── Identificação do operador ──────────────────────────────────────────

    @NotBlank(message = "O nome do operador é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nm_operador", nullable = false, length = 100)
    private String nomeOperador;

    @NotBlank(message = "A matrícula é obrigatória")
    @Size(max = 20, message = "Matrícula deve ter no máximo 20 caracteres")
    @Column(name = "cd_matricula", nullable = false, length = 20)
    private String matricula;

    @NotBlank(message = "O setor é obrigatório")
    @Size(max = 100, message = "Setor deve ter no máximo 100 caracteres")
    @Column(name = "nm_setor", nullable = false, length = 100)
    private String setor;

    // ── Informações da câmera ──────────────────────────────────────────────

    @NotBlank(message = "A localização da câmera é obrigatória")
    @Size(max = 200)
    @Column(name = "ds_camera", nullable = false, length = 200)
    private String descricaoCamera;

    // ── Data/hora da detecção ──────────────────────────────────────────────

    @Column(name = "dt_deteccao", nullable = false)
    private LocalDateTime dataDeteccao;

    // ── Flags de EPI (S = detectado / N = ausente) ────────────────────────

    @NotNull
    @Pattern(regexp = "[SN]", message = "Valor deve ser S ou N")
    @Column(name = "fl_capacete", nullable = false, length = 1)
    private String capacete;

    @NotNull
    @Pattern(regexp = "[SN]", message = "Valor deve ser S ou N")
    @Column(name = "fl_colete", nullable = false, length = 1)
    private String colete;

    @NotNull
    @Pattern(regexp = "[SN]", message = "Valor deve ser S ou N")
    @Column(name = "fl_luva", nullable = false, length = 1)
    private String luva;

    @NotNull
    @Pattern(regexp = "[SN]", message = "Valor deve ser S ou N")
    @Column(name = "fl_oculos", nullable = false, length = 1)
    private String oculos;

    @NotNull
    @Pattern(regexp = "[SN]", message = "Valor deve ser S ou N")
    @Column(name = "fl_botina", nullable = false, length = 1)
    private String botina;

    // ── Confiança do modelo ────────────────────────────────────────────────

    @NotNull(message = "A confiança da detecção é obrigatória")
    @DecimalMin(value = "0.0", message = "Confiança mínima é 0%")
    @DecimalMax(value = "100.0", message = "Confiança máxima é 100%")
    @Column(name = "nr_confianca", nullable = false)
    private Double confianca;

    // ── Nível de risco calculado ───────────────────────────────────────────

    @Column(name = "nr_nivel_risco")
    private Integer nivelRisco;

    // ── Callback pre-persist: preenche data e calcula risco ───────────────

    @PrePersist
    public void prePersist() {
        if (this.dataDeteccao == null) {
            this.dataDeteccao = LocalDateTime.now();
        }
        this.nivelRisco = calcularNivelRisco();
    }

    @PreUpdate
    public void preUpdate() {
        this.nivelRisco = calcularNivelRisco();
    }

    /**
     * Calcula o nível de risco (1–5) com base nos EPIs ausentes.
     * Capacete e colete são críticos (peso 2 cada); demais têm peso 1.
     */
    private int calcularNivelRisco() {
        int pontos = 0;
        if ("N".equals(this.capacete)) pontos += 2;
        if ("N".equals(this.colete))   pontos += 2;
        if ("N".equals(this.luva))     pontos += 1;
        if ("N".equals(this.oculos))   pontos += 1;
        if ("N".equals(this.botina))   pontos += 1;
        // Escala: 0 → 1, 1–2 → 2, 3 → 3, 4 → 4, 5+ → 5
        if (pontos == 0) return 1;
        if (pontos <= 2) return 2;
        if (pontos == 3) return 3;
        if (pontos == 4) return 4;
        return 5;
    }
}
