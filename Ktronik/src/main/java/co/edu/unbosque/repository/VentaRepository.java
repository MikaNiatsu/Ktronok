package co.edu.unbosque.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.model.Venta;

public interface VentaRepository extends CrudRepository<Venta, Integer> {
	@Override
	public Optional<Venta> findById(Integer id);

	@Override
	public List<Venta> findAll();
}
