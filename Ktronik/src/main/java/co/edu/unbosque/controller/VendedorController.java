package co.edu.unbosque.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import co.edu.unbosque.model.Vendedor;
import co.edu.unbosque.model.Venta;
import co.edu.unbosque.repository.VendedorRepository;
import co.edu.unbosque.repository.VentaRepository;
import jakarta.transaction.Transactional;

@Transactional
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class VendedorController {
	@Autowired
	private VendedorRepository vendedorRepository;
	@Autowired
	private VentaRepository ventaRest;

	@PostMapping("vendedor/venta/{id}")
	public ResponseEntity<String> addVenta(@RequestParam Integer idVenta, @RequestParam Integer id) {
		Optional<Vendedor> op = vendedorRepository.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			Optional<Venta> op2 = ventaRest.findById(idVenta);
			if (op2.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			} else {
				int tmp = op.get().getCantidadVentas();
				op.get().setCantidadVentas(tmp + 1);
				op.get().getVentas().add(op2.get());
				op2.get().setVendedor(op.get());
				vendedorRepository.save(op.get());
				ventaRest.save(op2.get());
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Venta añadida");
			}
		}
	}

//	@PostMapping("vendedor/tienda/{id}")
//	public ResponseEntity<String> addTienda(@RequestBody Tienda tienda, @RequestParam Integer id) {
//		Optional<Vendedor> op = vendedorRepository.findById(id);
//		if (op.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//		} else {
//			op.get().setTienda(tienda);
//			vendedorRepository.save(op.get());
//			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Tienda añadida");
//		}
//	}

	@PostMapping("vendedor")
	public ResponseEntity<String> add(@RequestParam String nombre,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate nacimiento,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate entrada, @RequestParam String sexo,
			@RequestParam int numVentas) {
		Vendedor vd = new Vendedor();
		vd.setNombre(nombre);
		vd.setFechaEntrada(LocalDate.parse(entrada.toString()));
		vd.setFechaNacimiento(LocalDate.parse(nacimiento.toString()));
		vd.setSexo(sexo);
		vd.setCantidadVentas(numVentas);
		vendedorRepository.save(vd);
		return ResponseEntity.status(HttpStatus.CREATED).body("CREATED (CODE 201)");
	}

	@GetMapping("vendedor")
	public ResponseEntity<List<Vendedor>> getAll() {
		List<Vendedor> list = vendedorRepository.findAll();
		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(list);
		}

	}

	@GetMapping("vendedor/{id}")
	public ResponseEntity<Vendedor> getVenta(@RequestParam Integer id) {
		Optional<Vendedor> op = vendedorRepository.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(op.get());
		}

	}

	@DeleteMapping("vendedor/{id}")
	public ResponseEntity<String> delete(@RequestParam Integer id) {
		Optional<Vendedor> op = vendedorRepository.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
		}
		vendedorRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.FOUND).body("Deleted");
	}

	@PutMapping("vendedor/{id}")
	public ResponseEntity<String> update(@PathVariable String nombre,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate nacimiento,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate entrada, @RequestParam String sexo,
			@RequestParam int numVentas, @RequestParam Vendedor nuevo, @RequestParam Integer id) {
		Optional<Vendedor> op = vendedorRepository.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return op.map(vd -> {
			vd.setNombre(nombre);
			vd.setFechaEntrada(entrada);
			vd.setFechaNacimiento(nacimiento);
			vd.setSexo(sexo);
			vd.setCantidadVentas(numVentas);
			vendedorRepository.save(vd);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Data update");
		}).orElseGet(() -> {
			nuevo.setId(id);
			vendedorRepository.save(nuevo);
			return ResponseEntity.status(HttpStatus.CREATED).body("Data created");
		});
	}
}
