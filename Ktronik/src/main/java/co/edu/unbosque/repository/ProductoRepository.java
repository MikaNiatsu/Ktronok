package co.edu.unbosque.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.model.Producto;

public interface ProductoRepository extends CrudRepository<Producto, Integer> {
	@Override
	public Optional<Producto> findById(Integer id);

	@Override
	public List<Producto> findAll();
}
