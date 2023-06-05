package br.com.clientesAPI.mappers;

import java.util.ArrayList;
import java.util.List;

import br.com.clientesAPI.dto.ClienteDTO;
import br.com.clientesAPI.model.Celular;
import br.com.clientesAPI.model.Cliente;
import br.com.clientesAPI.model.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class ClienteMapper {
	
	public static ClienteDTO mapper(Cliente cliente) {
		ClienteDTO clienteDTO = new ClienteDTO();
		clienteDTO.setId(cliente.getId());
		clienteDTO.setCpf(cliente.getCpf());
		clienteDTO.setNome(cliente.getNome());
		List<Email> listaEmail = cliente.getEmails();
		List<String> listadeEmail = new ArrayList<>();
		for(Email email: listaEmail) {
			listadeEmail.add(email.getEmail());
		}
		clienteDTO.setEmails(listadeEmail);
		
		List<Celular> listaCelular = cliente.getCelulares();
		List<String> listadeCelular = new ArrayList<>();
		for(Celular celular: listaCelular) {
			listadeCelular.add(celular.getNumero());
		}
		clienteDTO.setCelulares(listadeCelular);
		return clienteDTO;
	}

}
