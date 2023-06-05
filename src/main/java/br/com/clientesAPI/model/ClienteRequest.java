package br.com.clientesAPI.model;

import java.util.List;

import lombok.Data;

@Data
public class ClienteRequest {
	 private Integer cpf;
	 private String nome;
	 private List<String> emails;
	 private List<String> celulares;

}
