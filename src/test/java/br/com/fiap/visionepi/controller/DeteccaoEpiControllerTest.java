package br.com.fiap.visionepi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// CRUD /deteccoes ponta a ponta (controller + JPA, H2 em memória de src/test/resources):
// status HTTP do contrato e nivelRisco calculado no @PrePersist / @PreUpdate.
@SpringBootTest
@AutoConfigureMockMvc
class DeteccaoEpiControllerTest {

    @Autowired
    private MockMvc mvc;

    private static String corpo(String capacete, String colete, String luva, String oculos, String botina) {
        return """
            {"nomeOperador":"Marina Costa","matricula":"OP-204","setor":"Caldeiraria",
             "descricaoCamera":"CAM-04 - Caldeiraria","capacete":"%s","colete":"%s","luva":"%s",
             "oculos":"%s","botina":"%s","confianca":93.5,"nivelRisco":1}
            """.formatted(capacete, colete, luva, oculos, botina);
    }

    private long criar(String json) throws Exception {
        MvcResult r = mvc.perform(post("/deteccoes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataDeteccao").isNotEmpty())
                .andReturn();
        return Long.parseLong(r.getResponse().getContentAsString().replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void crudCompletoComStatusDoContrato() throws Exception {
        // POST 201; nivelRisco enviado pelo cliente é ignorado: capacete + luva ausentes = 3 pontos → 3
        long id = criar(corpo("N", "S", "N", "S", "S"));
        mvc.perform(get("/deteccoes/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelRisco").value(3));
        mvc.perform(get("/deteccoes")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());

        // PUT 200 recalcula o risco (@PreUpdate): capacete + colete ausentes = 4 pontos → 4
        mvc.perform(put("/deteccoes/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("N", "N", "S", "S", "S")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelRisco").value(4))
                .andExpect(jsonPath("$.dataDeteccao").isNotEmpty());

        mvc.perform(delete("/deteccoes/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/deteccoes/{id}", id)).andExpect(status().isNotFound());
        mvc.perform(delete("/deteccoes/{id}", id)).andExpect(status().isNotFound());
        mvc.perform(put("/deteccoes/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("S", "S", "S", "S", "S"))).andExpect(status().isNotFound());
    }

    @Test
    void escalaDeRisco() throws Exception {
        mvc.perform(get("/deteccoes/{id}", criar(corpo("S", "S", "S", "S", "S")))).andExpect(jsonPath("$.nivelRisco").value(1));
        mvc.perform(get("/deteccoes/{id}", criar(corpo("S", "S", "N", "S", "S")))).andExpect(jsonPath("$.nivelRisco").value(2));
        mvc.perform(get("/deteccoes/{id}", criar(corpo("N", "S", "S", "S", "S")))).andExpect(jsonPath("$.nivelRisco").value(2));
        mvc.perform(get("/deteccoes/{id}", criar(corpo("N", "N", "N", "N", "N")))).andExpect(jsonPath("$.nivelRisco").value(5));
    }

    // POST com "id" de um registro existente cria um novo; nunca sobrescreve o original
    @Test
    void postComIdNoCorpoNaoSobrescreveRegistroExistente() throws Exception {
        long original = criar(corpo("S", "S", "S", "S", "S"));
        long novo = criar(corpo("N", "N", "N", "N", "N").replace("{", "{\"id\":" + original + ","));

        org.assertj.core.api.Assertions.assertThat(novo).isNotEqualTo(original);
        mvc.perform(get("/deteccoes/{id}", original)).andExpect(jsonPath("$.nivelRisco").value(1));
    }

    @Test
    void postInvalidoDevolve400ComErroPorCampo() throws Exception {
        mvc.perform(post("/deteccoes").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("X", "S", "S", "S", "S").replace("Marina Costa", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nomeOperador").value("O nome do operador é obrigatório"))
                .andExpect(jsonPath("$.capacete").value("Valor deve ser S ou N"));
    }
}
