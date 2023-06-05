package co.edu.unbosque.controller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.xhtmlrenderer.pdf.ITextRenderer;

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

	@GetMapping("producto/asignar")
	public ResponseEntity<List<Producto>> getAllEmpty() {
		List<Producto> list = productoRepository.findAll();
		ArrayList<Producto> tmp = new ArrayList<>();
		for (Producto producto : list) {
			if (producto.isVendido() == false) {
				tmp.add(producto);
			}
		}
		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(tmp);
		}
	}

	@GetMapping("producto/{id}")
	public ResponseEntity<Producto> getVenta(@PathVariable Integer id) {
		Optional<Producto> op = productoRepository.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(op.get());
		}

	}

	@DeleteMapping("producto/asignar/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		Optional<Producto> op = productoRepository.findById(id);
		if (!op.isPresent() || op.get().isVendido() == true) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
		} else {
			productoRepository.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body("Deleted");
		}

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

	@GetMapping("producto/pdf")
	public ResponseEntity<byte[]> descargarPDFProductos() {
		List<Producto> productos = productoRepository.findAll();

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ITextRenderer renderer = new ITextRenderer();
			String htmlContent = generateHtmlContent(productos);
			renderer.setDocumentFromString(htmlContent);
			renderer.layout();
			renderer.createPDF(baos);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "productos.pdf");

			return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String generateHtmlContent(List<Producto> productos) {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head>");
		htmlBuilder.append("<style>");
		htmlBuilder.append("table { border-collapse: collapse; width: 100%; }");
		htmlBuilder.append("th, td { text-align: left; padding: 8px; }");
		htmlBuilder.append("th { background-color: #f2f2f2; }");
		htmlBuilder.append("tr:nth-child(even) { background-color: #f2f2f2; }");
		htmlBuilder.append("tr:hover { background-color: #ddd; }");
		htmlBuilder.append("</style>");
		htmlBuilder.append("</head>");
		htmlBuilder.append("<body>");
		htmlBuilder.append("<h1>Lista de Productos</h1>");
		htmlBuilder.append("<table class=\"my-table\">");
		htmlBuilder.append("<thead>");
		htmlBuilder.append("<tr>");
		htmlBuilder.append("<th>ID</th>");
		htmlBuilder.append("<th>Producto</th>");
		htmlBuilder.append("<th>Vendido Estado</th>");
		htmlBuilder.append("</tr>");
		htmlBuilder.append("</thead>");
		htmlBuilder.append("<tbody>");

		for (Producto producto : productos) {
			htmlBuilder.append("<tr>");
			htmlBuilder.append("<td>").append(producto.getId()).append("</td>");
			htmlBuilder.append("<td>").append(producto.getProducto()).append("</td>");
			htmlBuilder.append("<td>").append(producto.isVendido() ? "Sí" : "No").append("</td>");
			htmlBuilder.append("</tr>");
		}

		htmlBuilder.append("</tbody>");
		htmlBuilder.append("</table>");
		htmlBuilder.append("</body>");
		htmlBuilder.append("</html>");

		return htmlBuilder.toString();
	}

}
