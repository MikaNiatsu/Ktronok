package co.edu.unbosque.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.model.Tienda;

public interface TiendaRepository extends CrudRepository<Tienda, Integer> {
	@Override
	public Optional<Tienda> findById(Integer id);

	@Override
	public List<Tienda> findAll();
}
