package co.edu.unbosque.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.model.Tienda;
import co.edu.unbosque.model.Vendedor;
import co.edu.unbosque.repository.TiendaRepository;
import co.edu.unbosque.repository.VendedorRepository;
import jakarta.transaction.Transactional;

@Transactional
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class TiendaController {
	@Autowired
	private TiendaRepository tiendaRepository;
	@Autowired
	private VendedorRepository vendedorRepository;

	@PostMapping("tienda/vendedor/{id}")
	public ResponseEntity<String> addTienda(@RequestParam Integer idVendedor, @RequestParam Integer id) {
		Optional<Tienda> op = tiendaRepository.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			Optional<Vendedor> op2 = vendedorRepository.findById(idVendedor);
			if (op2.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			} else {
				op.get().getVendedores().add(op2.get());
				op2.get().setTienda(op.get());
				tiendaRepository.save(op.get());
				vendedorRepository.save(op2.get());
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Vendedor añadido");
			}

		}
	}

	@PostMapping("tienda")
	public ResponseEntity<String> add(@RequestParam String nombre, @RequestParam String ubicacion) {
		Tienda tn = new Tienda();
		tn.setNombre(nombre);
		tn.setUbicacion(ubicacion);
		tiendaRepository.save(tn);

		return ResponseEntity.status(HttpStatus.CREATED).body("CREATED (CODE 201)");
	}

	@GetMapping("tienda")
	public ResponseEntity<List<Tienda>> getAll() {
		List<Tienda> list = tiendaRepository.findAll();
		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(list);
		}

	}

	@GetMapping("tienda/{id}")
	public ResponseEntity<Tienda> getVenta(@PathVariable Integer id) {
		Optional<Tienda> op = tiendaRepository.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(op.get());
		}

	}

	@DeleteMapping("tienda/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		Optional<Tienda> op = tiendaRepository.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
		}

		tiendaRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.FOUND).body("Deleted");
	}

	@PutMapping("tienda/{id}")
	public ResponseEntity<String> update(@RequestParam Tienda nuevo, @PathVariable Integer id,
			@RequestParam List<Vendedor> vendedores, @RequestParam String nombre, @RequestParam String ubicacion) {
		Optional<Tienda> op = tiendaRepository.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return op.map(tn -> {
			tn.setNombre(nombre);
			tn.setUbicacion(ubicacion);
			tiendaRepository.save(tn);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Data update");
		}).orElseGet(() -> {
			nuevo.setId(id);
			tiendaRepository.save(nuevo);
			return ResponseEntity.status(HttpStatus.CREATED).body("Data created");
		});
	}

}
