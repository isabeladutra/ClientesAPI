package br.com.clientesAPI.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClienteDTO {

	private Long id;
	private Integer cpf;
	private String nome;
	private List<String> emails;
	private List<String> celulares;
}
