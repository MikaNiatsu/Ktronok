package co.edu.unbosque.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.model.Vendedor;

public interface VendedorRepository extends CrudRepository<Vendedor, Integer> {
	@Override
	public Optional<Vendedor> findById(Integer id);

	@Override
	public List<Vendedor> findAll();
}
