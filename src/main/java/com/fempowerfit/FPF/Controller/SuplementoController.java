package com.fempowerfit.FPF.Controller;

import com.fempowerfit.FPF.Model.Suplemento;
import com.fempowerfit.FPF.repository.SuplementoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid; // Import statement added
import java.util.Optional;

@RestController
@RequestMapping("/suplemento")
@CacheConfig(cacheNames = "suplemento")
@Tag(name = "suplementos", description = "Endpoint relacionados com suplementos")
@Validated
public class SuplementoController {

    private static final Logger log = LoggerFactory.getLogger(SuplementoController.class);

    @Autowired
    private SuplementoRepository suplementoRepository;

    @GetMapping("/meusSuplementos")
    @Cacheable
    @Operation(summary = "Lista todos os suplementos", description = "Endpoint que retorna um array de objetos do tipo de suplementos utilizados pelo usuario")
    public Page<Suplemento> listar(
            @RequestParam(required = false) String tipo,
            @PageableDefault(sort = "tipo", direction = Direction.ASC) Pageable pageable
    ) {
        log.info("Listando suplementos, tipo: {}", tipo);
        if (tipo != null) {
            return suplementoRepository.findByTipoIgnoreCase(tipo, pageable);
        }
        return suplementoRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém um suplemento pelo ID", description = "Endpoint que retorna um suplemento específico pelo seu ID")
    public ResponseEntity<Suplemento> get(@PathVariable Long id) {
        log.info("Obtendo suplemento com ID: {}", id);
        Optional<Suplemento> suplementoOptional = suplementoRepository.findById(id);
        return suplementoOptional.map(suplemento -> ResponseEntity.ok().body(suplemento))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @CacheEvict(allEntries = true)
    @Operation(summary = "Cadastra um novo suplemento", description = "Endpoint para cadastrar um novo suplemento")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Erro de validação do suplemento"),
            @ApiResponse(responseCode = "201", description = "Suplemento cadastrado com sucesso")
    })
    public ResponseEntity<Suplemento> cadastrarSuplemento(@Valid @RequestBody Suplemento suplemento) {
        log.info("Cadastrando novo suplemento: {}", suplemento);
        Suplemento entity = suplementoRepository.save(suplemento);
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @PutMapping("/{id}")
    @CacheEvict(allEntries = true)
    @Operation(summary = "Atualiza um suplemento existente", description = "Endpoint para atualizar um suplemento existente pelo ID")
    public ResponseEntity<Suplemento> atualizarSuplemento(@PathVariable Long id, @Valid @RequestBody Suplemento suplemento) {
        log.info("Atualizando suplemento com ID: {}", id);
        Optional<Suplemento> suplementoOptional = suplementoRepository.findById(id);

        if (suplementoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Suplemento suplementoEncontrado = suplementoOptional.get();
        suplementoEncontrado.setMarca(suplemento.getMarca());
        suplementoEncontrado.setQuantidade(suplemento.getQuantidade());
        suplementoEncontrado.setTipo(suplemento.getTipo());

        suplementoRepository.save(suplementoEncontrado);
        return ResponseEntity.ok().body(suplementoEncontrado);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(allEntries = true)
    @Operation(summary = "Deleta um suplemento existente", description = "Endpoint para deletar um suplemento existente pelo ID")
    public ResponseEntity<Void> deletarSuplemento(@PathVariable Long id) {
        log.info("Deletando suplemento com ID: {}", id);
        Optional<Suplemento> suplementoOptional = suplementoRepository.findById(id);

        if (suplementoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        suplementoRepository.delete(suplementoOptional.get());
        return ResponseEntity.noContent().build();
    }
}
