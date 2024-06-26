package com.fempowerfit.FPF.Controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fempowerfit.FPF.Model.Cliente;
import com.fempowerfit.FPF.repository.ClienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = {"*"}, maxAge = 3600)
@RequestMapping("cliente")
@Slf4j
@CacheConfig(cacheNames = "clientes")
@Tag(name = "clientes", description = "Endpoint relacionado com clientes")
public class ClienteController {
    
    @Autowired
    ClienteRepository clienteRepository;

    @GetMapping
    @Operation(summary = "Lista todos os clientes cadastrados no sistema.", description = "Endpoint que retorna um array de objetos do tipo cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public List<Cliente> index(){
        return clienteRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Lista todos os clientes cadastrados no sistema.", description = "Endpoint que retorna um array de objetos do tipo cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação do cliente"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Cliente create(@RequestBody Cliente cliente){
        log.info("cadastrando cliente: {}", cliente);
        if (clienteRepository.findByCpf(cliente.getCpf()) == null){
            return clienteRepository.save(cliente);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente já cadastrado");
        }
        
    }

    @GetMapping("login")
    @Operation(summary = "Realiza o login de um Cliente cadastrado no sistema.", description = "Endpoint que retorna um objeto do tipo cliente com um cpf e uma senha informados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
})
    public Cliente Login(@RequestParam String cpf, @RequestParam String senha) {
        return clienteRepository.login(cpf, senha);
    }

    @GetMapping("{id}")
    @Operation(summary = "Retorna um cliente especifico cadastrado no sistema.", description = "Endpoint que retorna um objeto do tipo cliente com um id informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Cliente> get(@PathVariable Long id){
        log.info("Buscar por id: {}", id);
        return clienteRepository
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("cpf/{cpf_cliente}")
    @Operation(summary = "Retorna um cliente especifico cadastrado no sistema.", description = "Endpoint que retorna um objeto do tipo cliente com um cpf informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Cliente> get(@PathVariable String cpf_cliente){
        log.info("Buscar por CPF: {}", cpf_cliente);
        Cliente cliente = clienteRepository.findByCpf(cpf_cliente);
    if (cliente != null) {
        return ResponseEntity.ok(cliente);
    } else {
        return ResponseEntity.notFound().build();
    }
    }
    
    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Deleta um cliente pelo ID.", description = "Endpoint que deleta um cliente com um ID informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public void destroy (@PathVariable Long id){
        log.info("Apagando id {}", id);
        verificarId(id);
        clienteRepository.deleteById(id);
    }

    @Transactional
    @DeleteMapping("cpf/{cpf_cliente}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Deleta um cliente pelo CPF.", description = "Endpoint que deleta um cliente com um CPF informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public void deleteByCpf_cliente (@PathVariable String cpf_cliente){
        log.info("Apagando Cliente com CPF {}", cpf_cliente);
        verificarCpf(cpf_cliente);
        clienteRepository.deleteByCpf(cpf_cliente);
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualiza um cliente pelo ID.", description = "Endpoint que atualiza um cliente com um ID informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação do cliente"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Cliente update(@PathVariable Long id, @RequestBody Cliente cliente){
        log.info("Atualizando o cadastro do id={} para {}", id, cliente);
        verificarId(id);
        cliente.setId(id);
        return clienteRepository.save(cliente);
    }

    @PutMapping("cpf/{cpf_cliente}")
    @Operation(summary = "Atualiza um cliente pelo CPF.", description = "Endpoint que atualiza um cliente com um CPF informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação do cliente"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Cliente update(@PathVariable String cpf_cliente, @RequestBody Cliente cliente){
        log.info("Atualizando o cadastro do pcf={} para {}", cpf_cliente, cliente);
        Cliente clienteSalvo = verificarCpf(cpf_cliente);
        clienteSalvo.setNome_cliente(cliente.getNome_cliente());
        clienteSalvo.setCpf(cliente.getCpf());
        clienteSalvo.setGenero(cliente.getGenero());
        clienteSalvo.setCep(cliente.getCep());
        clienteSalvo.setTelefone(cliente.getTelefone());
        clienteSalvo.setEmail(cliente.getEmail());
        clienteSalvo.setPreferencia_contato(cliente.getPreferencia_contato());
        clienteSalvo.setDtNascimento(cliente.getDtNascimento());
        clienteSalvo.setSenha_user(cliente.getSenha_user());
        return clienteRepository.save(clienteSalvo);
}

    private void verificarId(Long id){
        clienteRepository.
        findById(id)
        .orElseThrow(
            ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "id não encontrado")
        );
    }

    private Cliente verificarCpf(String cpf_cliente){
        Cliente cliente = clienteRepository.findByCpf(cpf_cliente);
    if (cliente == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente com CPF não encontrado");
    }else{
        return cliente;
    }
}
}
