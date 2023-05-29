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

import co.edu.unbosque.model.Producto;
import co.edu.unbosque.model.Vendedor;
import co.edu.unbosque.model.Venta;
import co.edu.unbosque.repository.ProductoRepository;
import co.edu.unbosque.repository.VentaRepository;
import jakarta.transaction.Transactional;

@Transactional
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class VentaController {
	@Autowired
	private VentaRepository ventaRest;
	@Autowired
	private ProductoRepository productoRepository;

	@PostMapping("venta/producto/{id}")
	public ResponseEntity<String> addProducto(@RequestParam Integer idProducto, @RequestParam Integer id) {
		Optional<Venta> op = ventaRest.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			Optional<Producto> op2 = productoRepository.findById(idProducto);
			if (op2.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			} else {
				op.get().getProductos().add(op2.get());
				op2.get().setVenta(op.get());
				ventaRest.save(op.get());
				productoRepository.save(op2.get());
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Producto añadido");
			}
		}
	}

	@PostMapping("venta")
	public ResponseEntity<String> add() {
		Venta ven = new Venta();
		ventaRest.save(ven);
		return ResponseEntity.status(HttpStatus.CREATED).body("CREATED (CODE 201)");
	}

	@GetMapping("venta")
	public ResponseEntity<List<Venta>> getAll() {
		List<Venta> list = ventaRest.findAll();
		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(list);
		}

	}

	@GetMapping("venta/{id}")
	public ResponseEntity<Venta> getVenta(@RequestParam Integer id) {
		Optional<Venta> op = ventaRest.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(op.get());
		}

	}

	@DeleteMapping("venta/{id}")
	public ResponseEntity<String> delete(@RequestParam Integer id) {
		Optional<Venta> op = ventaRest.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
		}
		ventaRest.deleteById(id);
		return ResponseEntity.status(HttpStatus.FOUND).body("Deleted");
	}

	@PutMapping("venta/{id}")
	public ResponseEntity<String> update(@RequestParam Venta nuevo, @RequestParam Integer id,
			@PathVariable List<Producto> productos, @RequestParam Vendedor vendedor) {
		Optional<Venta> op = ventaRest.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return op.map(ven -> {
			ven.setProductos(productos);
			ven.setVendedor(vendedor);
			ventaRest.save(ven);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Data update");
		}).orElseGet(() -> {
			nuevo.setId(id);
			ventaRest.save(nuevo);
			return ResponseEntity.status(HttpStatus.CREATED).body("Data created");
		});
	}
}
