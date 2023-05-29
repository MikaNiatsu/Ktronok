package co.edu.unbosque.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.model.Producto;
import co.edu.unbosque.model.Venta;
import co.edu.unbosque.repository.ProductoRepository;
import jakarta.transaction.Transactional;

@Transactional
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ProductoController {
	@Autowired
	private ProductoRepository productoRepository;

	@PostMapping("producto")
	public ResponseEntity<String> add(@RequestParam String producto) {
		Producto pr = new Producto();
		pr.setProducto(producto);
		productoRepository.save(pr);
		return ResponseEntity.status(HttpStatus.CREATED).body("CREATED (CODE 201)");
	}

	@GetMapping("producto")
	public ResponseEntity<List<Producto>> getAll() {
		List<Producto> list = productoRepository.findAll();
		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(list);
		}

	}

	@GetMapping("producto/{id}")
	public ResponseEntity<Producto> getVenta(@RequestParam Integer id) {
		Optional<Producto> op = productoRepository.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(op.get());
		}

	}

	@DeleteMapping("producto/{id}")
	public ResponseEntity<String> delete(@RequestParam Integer id) {
		Optional<Producto> op = productoRepository.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
		}
		productoRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.FOUND).body("Deleted");
	}

	@PutMapping("producto/{id}")
	public ResponseEntity<String> update(@RequestParam Producto nuevo, @RequestParam Venta venta,
			@RequestParam Integer id) {
		Optional<Producto> op = productoRepository.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return op.map(pr -> {
			pr.setVenta(venta);
			productoRepository.save(pr);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Data update");
		}).orElseGet(() -> {
			nuevo.setId(id);
			productoRepository.save(nuevo);
			return ResponseEntity.status(HttpStatus.CREATED).body("Data created");
		});
	}
}
