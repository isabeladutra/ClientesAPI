package br.com.clientesAPI.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.clientesAPI.exception.ClienteExistenteException;
import br.com.clientesAPI.model.Celular;
import br.com.clientesAPI.model.Cliente;
import br.com.clientesAPI.model.Email;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Service
public class ClienteService {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void insertCliente(Cliente cliente) throws ClienteExistenteException {

		Cliente existingCliente = getClienteByCpf(cliente.getCpf());
		if (existingCliente != null) {
			throw new ClienteExistenteException("Já existe um cliente cadastrado com este CPF.");
		}

		String sql = "INSERT INTO cliente (cpf, nome) VALUES (:cpf, :nome)";
		entityManager.createNativeQuery(sql).setParameter("cpf", cliente.getCpf())
				.setParameter("nome", cliente.getNome()).executeUpdate();

		Cliente insertedCliente = getClienteByCpf(cliente.getCpf());

		for (Email email : cliente.getEmails()) {
			String emailSql = "INSERT INTO email (email, cliente_id) VALUES (:email, :clienteId)";
			entityManager.createNativeQuery(emailSql).setParameter("email", email.getEmail())
					.setParameter("clienteId", insertedCliente.getId()).executeUpdate();
		}

		for (Celular celular : cliente.getCelulares()) {
			String celularSql = "INSERT INTO celular (numero, cliente_id) VALUES (:numero, :clienteId)";
			entityManager.createNativeQuery(celularSql).setParameter("numero", celular.getNumero())
					.setParameter("clienteId", insertedCliente.getId()).executeUpdate();
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Cliente getClienteByCpf(Integer cpf) {
		String sql = "SELECT * FROM cliente WHERE cpf = :cpf";
		List<Cliente> clientes = entityManager.createNativeQuery(sql, Cliente.class).setParameter("cpf", cpf)
				.getResultList();

		if (clientes.isEmpty()) {
			return null; 
		} else {
			return clientes.get(0); 
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Cliente buscarClientePorId(Long clienteId) {
		String sql = "SELECT * FROM cliente WHERE id = :clienteId";
		List<Cliente> clientes = entityManager.createNativeQuery(sql, Cliente.class)
				.setParameter("clienteId", clienteId).getResultList();

		if (clientes.isEmpty()) {
			return null; 
		} else {
			return clientes.get(0); 
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Cliente> getClientesPorDDD(String ddd) {
		String queryString = "SELECT c FROM Cliente c JOIN c.celulares cel WHERE SUBSTR(cel.numero, 1, :dddLength) = :ddd";

		TypedQuery<Cliente> query = entityManager.createQuery(queryString, Cliente.class);
		query.setParameter("dddLength", ddd.length());
		query.setParameter("ddd", ddd);

		return query.getResultList();
	}

	@Transactional
	public Cliente atualizarCliente(Long clienteId, Integer novoCpf, String novoNome, List<String> novosEmails,
			List<String> novosCelulares) {
		String sql = "UPDATE cliente SET ";
		List<String> setStatements = new ArrayList<>();
		Map<String, Object> parameters = new HashMap<>();

		if (novoCpf != null) {
			setStatements.add("cpf = :novoCpf");
			parameters.put("novoCpf", novoCpf);
		}

		if (novoNome != null) {
			setStatements.add("nome = :novoNome");
			parameters.put("novoNome", novoNome);
		}

		if (novosEmails != null) {
			// Atualiza a lista de emails do cliente
			String deleteEmailsSql = "DELETE FROM email WHERE cliente_id = :clienteId";
			Query deleteEmailsQuery = entityManager.createNativeQuery(deleteEmailsSql);
			deleteEmailsQuery.setParameter("clienteId", clienteId);
			deleteEmailsQuery.executeUpdate();

			String insertEmailSql = "INSERT INTO email (email, cliente_id) VALUES (:email, :clienteId)";
			Query insertEmailQuery = entityManager.createNativeQuery(insertEmailSql);

			for (String novoEmail : novosEmails) {
				insertEmailQuery.setParameter("email", novoEmail);
				insertEmailQuery.setParameter("clienteId", clienteId);
				insertEmailQuery.executeUpdate();
			}
		}

		if (novosCelulares != null) {
			// Atualiza a lista de celulares do cliente
			String deleteCelularesSql = "DELETE FROM celular WHERE cliente_id = :clienteId";
			Query deleteCelularesQuery = entityManager.createNativeQuery(deleteCelularesSql);
			deleteCelularesQuery.setParameter("clienteId", clienteId);
			deleteCelularesQuery.executeUpdate();

			String insertCelularSql = "INSERT INTO celular (numero, cliente_id) VALUES (:numero, :clienteId)";
			Query insertCelularQuery = entityManager.createNativeQuery(insertCelularSql);

			for (String novoCelular : novosCelulares) {
				insertCelularQuery.setParameter("numero", novoCelular);
				insertCelularQuery.setParameter("clienteId", clienteId);
				insertCelularQuery.executeUpdate();
			}
		}

		sql += String.join(", ", setStatements);
		sql += " WHERE id = :clienteId";
		parameters.put("clienteId", clienteId);

		Query updateQuery = entityManager.createNativeQuery(sql);
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			updateQuery.setParameter(entry.getKey(), entry.getValue());
		}

		int updatedRows = updateQuery.executeUpdate();

		if (updatedRows > 0) {
			return buscarClientePorId(clienteId);
		} else {
			return null;
		}
	}

	@Transactional
	public void deleteClienteByCpf(Integer cpf) {

		Query deleteEmailQuery = entityManager
				.createNativeQuery("DELETE FROM email WHERE cliente_id IN (SELECT id FROM cliente WHERE cpf = :cpf)");
		deleteEmailQuery.setParameter("cpf", cpf);
		deleteEmailQuery.executeUpdate();

		Query deleteClienteQuery = entityManager.createNativeQuery("DELETE FROM cliente WHERE cpf = :cpf");
		deleteClienteQuery.setParameter("cpf", cpf);
		deleteClienteQuery.executeUpdate();
	}

	@Transactional
	public List<Cliente> filtrarPorNome(String parteDoNome) {
		String sql = "SELECT c FROM Cliente c WHERE c.nome LIKE :parteDoNome";
		TypedQuery<Cliente> query = entityManager.createQuery(sql, Cliente.class);
		query.setParameter("parteDoNome", "%" + parteDoNome + "%");
		return query.getResultList();
	}
}
