package br.com.clientesAPI;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.clientesAPI.dto.ClienteDTO;
import br.com.clientesAPI.exception.ClienteExistenteException;
import br.com.clientesAPI.mappers.ClienteMapper;
import br.com.clientesAPI.model.Cliente;
import br.com.clientesAPI.model.ClienteRequest;
import br.com.clientesAPI.services.ClienteService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@OpenAPIDefinition(info = @Info(title = "Clientes API", description = "API que implementa cadastro e consulta de Clientes", contact = @Contact(name = "Isabela Dutra", email = "dutraisabela88@gmail.com")))
@RestController
public class ClientesApiController {

	@Autowired
	ClienteService clienteService;

	public void setClienteService(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@Operation(summary = "Cria um novo cliente", responses = {
			@ApiResponse(responseCode = "201", description = "Cliente criado com Sucesso", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "422", description = "Falha ao cadastrar cliente", content = @Content(mediaType = "text/plain")) })
	@PostMapping(value = "/clientes")
	public ResponseEntity<String> cadastraCliente(@RequestBody Cliente cliente) {
		try {
			
			clienteService.insertCliente(cliente);
			HttpStatus returnStatusCode = HttpStatus.CREATED;
			return new ResponseEntity<String>("Cliente cadastrado com sucesso", returnStatusCode);
		} catch (ClienteExistenteException e) {
			return ResponseEntity.unprocessableEntity().body("Erro ao cadastrar cliente: " + e.getMessage());
		}
	}

	@Operation(summary = "Busca um Cliente pelo CPF", responses = {
			@ApiResponse(responseCode = "200", description = "Consulta Realizada com Sucesso", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Cliente Inexistente") })
	@GetMapping(value = "/client/por-cpf")
	public ResponseEntity<ClienteDTO> retornaCliente(@RequestParam Integer cpf) {
		Cliente cliente = clienteService.getClienteByCpf(cpf);
		if (cliente != null) {
			ClienteDTO dto = ClienteMapper.mapper(cliente);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Deleta um Cliente", responses = {
			@ApiResponse(responseCode = "200", description = "Consulta Realizada com Sucesso", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "404", description = "Cliente Inexistente") })
	@DeleteMapping(value = "/cliente/{cpf}")
	public ResponseEntity<String> deletaCliente(@PathVariable Integer cpf) {
		Cliente cliente = clienteService.getClienteByCpf(cpf);
		if (cliente == null) {
			return ResponseEntity.notFound().build();
		}

		clienteService.deleteClienteByCpf(cpf);
		return ResponseEntity.ok("Cliente deletado com sucesso.");
	}

	@Operation(summary = "Atualiza Dados de um Cliente", responses = {
			@ApiResponse(responseCode = "200", description = "Consulta Realizada com Sucesso", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "404", description = "Cliente Inexistente") })
	@PutMapping("/cliente/{clienteId}")
	public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable Long clienteId,
			@RequestBody ClienteRequest request) {
		Integer novoCpf = request.getCpf();
		String novoNome = request.getNome();
		List<String> novosEmails = request.getEmails();
		List<String> novosCelulares = request.getCelulares();

		Cliente clienteAtualizado = clienteService.atualizarCliente(clienteId, novoCpf, novoNome, novosEmails,
				novosCelulares);

		if (clienteAtualizado != null) {
			ClienteDTO clientedto = ClienteMapper.mapper(clienteAtualizado);
			return ResponseEntity.ok(clientedto);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "Filtra Clientes pelo DDD do Celular", responses = {
			@ApiResponse(responseCode = "200", description = "Consulta Realizada com Sucesso", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "204", description = "Nenhum cliente encontrado com o DDD passado") })
	@GetMapping("/clientes/por-ddd")
	public ResponseEntity<List<ClienteDTO>> listarClientesPorDDD(@RequestParam String ddd) {
		List<Cliente> clientes = clienteService.getClientesPorDDD(ddd);
		List<ClienteDTO> listaDeClientes = new ArrayList<ClienteDTO>();
		if (!clientes.isEmpty()) {
			for (Cliente cliente : clientes) {
				ClienteDTO dto = ClienteMapper.mapper(cliente);
				listaDeClientes.add(dto);
			}
			return ResponseEntity.ok(listaDeClientes);
		}
		return new ResponseEntity<List<ClienteDTO>>(listaDeClientes, HttpStatus.NO_CONTENT);
	}

	@Operation(summary = "Filtra Clientes pelo Nome", responses = {
			@ApiResponse(responseCode = "200", description = "Consulta Realizada com Sucesso", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "204", description = "Nenhum cliente encontrado") })
	@GetMapping("/clientes/por-nome")
	public ResponseEntity<List<ClienteDTO>> filtrarClientesPorNome(@RequestParam("nome") String parteDoNome) {
		List<Cliente> clientes = clienteService.filtrarPorNome(parteDoNome);

		if (clientes.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		List<ClienteDTO> listaDeClientes = new ArrayList<ClienteDTO>();
		for (Cliente cliente : clientes) {
			ClienteDTO dto = ClienteMapper.mapper(cliente);
			listaDeClientes.add(dto);
		}
		return ResponseEntity.ok(listaDeClientes);
	}

}
