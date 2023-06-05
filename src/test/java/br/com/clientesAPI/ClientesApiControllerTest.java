package br.com.clientesAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import br.com.clientesAPI.model.Email;

import br.com.clientesAPI.dto.ClienteDTO;
import br.com.clientesAPI.exception.ClienteExistenteException;
import br.com.clientesAPI.model.Celular;
import br.com.clientesAPI.model.Cliente;
import br.com.clientesAPI.model.ClienteRequest;
import br.com.clientesAPI.services.ClienteService;
import nonapi.io.github.classgraph.utils.Assert;

public class ClientesApiControllerTest {
	
	private ClienteService clienteService = Mockito.mock(ClienteService.class);
	private AutoCloseable closeable;
	
	@BeforeEach
	public void initMocks() {
		 closeable = MockitoAnnotations.openMocks(this);
	}
	
	@AfterEach 
	public void releaseMocks() throws Exception {
        closeable.close();
    }
	
	@Test
	public void testaCadastraClienteComSucesso () {
		ClientesApiController controller = new ClientesApiController();
	    controller.setClienteService(clienteService);
	    Cliente cliente = new Cliente();
	    ResponseEntity<String> retorno = controller.cadastraCliente(cliente);
	    assert( retorno.getStatusCode().isSameCodeAs(HttpStatus.CREATED)); 
		
	}
	
	@Test
	public void testaCadastraClienteComFalha () throws ClienteExistenteException {
		ClientesApiController controller = new ClientesApiController();
	    controller.setClienteService(clienteService);
	    Cliente cliente = new Cliente();
	    doThrow(ClienteExistenteException.class).when(clienteService).insertCliente(any());
	    ResponseEntity<String> retorno = controller.cadastraCliente(cliente);
	    assert( retorno.getStatusCode().isSameCodeAs(HttpStatus.UNPROCESSABLE_ENTITY)); 
	}
	
	@Test
	public void testaRetornaClienteComSucesso () {
		ClientesApiController controller = new ClientesApiController();
	    controller.setClienteService(clienteService);
	    
	    Cliente cli = new Cliente();
	    cli.setCpf(123);
	    cli.setId(1L);
	    cli.setNome("Fulano");
	    Email email = new Email();
	    email.setId(1L);
	    email.setEmail("fulano@Gmail.com");
	    List<Email> listaEmail = new ArrayList<Email>();
	    listaEmail.add(email);
	    cli.setEmails(listaEmail);
	    Celular celular = new Celular();
	    celular.setId(1L);
	    celular.setNumero("2199999999");
	    List<Celular> listaCelular = new ArrayList<>();
	    listaCelular.add(celular);
	    cli.setCelulares(listaCelular);
	    
	    when(clienteService.getClienteByCpf(any())).thenReturn(cli);
	    ResponseEntity<ClienteDTO> retorno =  controller.retornaCliente(123);
	    assertInstanceOf(ClienteDTO.class, retorno.getBody());
	}
	
	@Test
	public void testaDeletarCliente() {
		ClientesApiController controller = new ClientesApiController();
	    controller.setClienteService(clienteService);
	    when(clienteService.getClienteByCpf(any())).thenReturn(new Cliente ());
	    ResponseEntity<String> retorno = controller.deletaCliente(123);
	    assert( retorno.getStatusCode().isSameCodeAs(HttpStatus.OK)); 
		
	}
	
	@Test
	public void testaAtualizarCliente() {
		ClientesApiController controller = new ClientesApiController();
	    controller.setClienteService(clienteService);
		ClienteRequest request = new ClienteRequest();
		request.setNome("Fulano");
		request.setCpf(123);
		List<String> listaCelulares = new ArrayList<>();
		listaCelulares.add("21999999999");
		request.setCelulares(listaCelulares);
		List<String> listaEmails = new ArrayList<>();
		listaEmails.add("fulano@gmail.com");
		request.setEmails(listaEmails);
		
		Cliente cli = new Cliente();
	    cli.setCpf(123);
	    cli.setId(1L);
	    cli.setNome("Fulano");
	    Email email = new Email();
	    email.setId(1L);
	    email.setEmail("fulano@Gmail.com");
	    List<Email> listaEmail = new ArrayList<Email>();
	    listaEmail.add(email);
	    cli.setEmails(listaEmail);
	    Celular celular = new Celular();
	    celular.setId(1L);
	    celular.setNumero("2199999999");
	    List<Celular> listaCelular = new ArrayList<>();
	    listaCelular.add(celular);
	    cli.setCelulares(listaCelular);
	    
		when(clienteService.atualizarCliente(any(), any(), any(), any(), any())).thenReturn(cli);
		ResponseEntity<ClienteDTO> retorno = controller.atualizarCliente(1L, request);
		 assert( retorno.getStatusCode().isSameCodeAs(HttpStatus.OK)); 
		
	}
	
	@Test
	public void testaListarClientesPorDDD() {
		ClientesApiController controller = new ClientesApiController();
	    controller.setClienteService(clienteService);
	    
	    Cliente cli = new Cliente();
	    cli.setCpf(123);
	    cli.setId(1L);
	    cli.setNome("Fulano");
	    Email email = new Email();
	    email.setId(1L);
	    email.setEmail("fulano@Gmail.com");
	    List<Email> listaEmail = new ArrayList<Email>();
	    listaEmail.add(email);
	    cli.setEmails(listaEmail);
	    Celular celular = new Celular();
	    celular.setId(1L);
	    celular.setNumero("2199999999");
	    List<Celular> listaCelular = new ArrayList<>();
	    listaCelular.add(celular);
	    cli.setCelulares(listaCelular);
	    
	    List<Cliente> clientes = new ArrayList<>();
	    clientes.add(cli);
	    when(clienteService.getClientesPorDDD(any())).thenReturn(clientes);
	    ResponseEntity<List<ClienteDTO>>retorno = controller.listarClientesPorDDD("21");
	    assert( retorno.getStatusCode().isSameCodeAs(HttpStatus.OK)); 
		
	}
	
	@Test
	public void testaFiltrarsClientesPorNome () {
		ClientesApiController controller = new ClientesApiController();
	    controller.setClienteService(clienteService);
	    
	    Cliente cli = new Cliente();
	    cli.setCpf(123);
	    cli.setId(1L);
	    cli.setNome("Fulano");
	    Email email = new Email();
	    email.setId(1L);
	    email.setEmail("fulano@Gmail.com");
	    List<Email> listaEmail = new ArrayList<Email>();
	    listaEmail.add(email);
	    cli.setEmails(listaEmail);
	    Celular celular = new Celular();
	    celular.setId(1L);
	    celular.setNumero("2199999999");
	    List<Celular> listaCelular = new ArrayList<>();
	    listaCelular.add(celular);
	    cli.setCelulares(listaCelular);
	    
	    List<Cliente> clientes = new ArrayList<>();
	    clientes.add(cli);
	    when(clienteService.filtrarPorNome(any())).thenReturn(clientes);
	    ResponseEntity<List<ClienteDTO>> retorno =  controller.filtrarClientesPorNome("abc");
	    assert( retorno.getStatusCode().isSameCodeAs(HttpStatus.OK)); 
		
	}

}
