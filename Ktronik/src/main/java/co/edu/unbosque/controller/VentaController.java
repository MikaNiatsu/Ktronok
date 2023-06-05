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
import co.edu.unbosque.model.Vendedor;
import co.edu.unbosque.model.Venta;
import co.edu.unbosque.repository.ProductoRepository;
import co.edu.unbosque.repository.VendedorRepository;
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
	@Autowired
	private VendedorRepository vendedorRepository;

	@PostMapping("venta/producto/{id}")
	public ResponseEntity<String> addProductos(@PathVariable Integer id,
			@RequestParam List<Integer> productosSeleccionados) {
		Optional<Vendedor> op = vendedorRepository.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Productos no añadidos");
		} else {
			Venta vn = new Venta();
			ArrayList<Producto> prs = new ArrayList<>();
			for (Integer idProducto : productosSeleccionados) {
				Optional<Producto> op2 = productoRepository.findById(idProducto);
				if (op2.isPresent()) {
					prs.add(op2.get());
					op2.get().setVendido(true);
					op2.get().setVenta(vn);
					productoRepository.save(op2.get());
				}
			}
			op.get().getVentas().add(vn);
			int tmp = op.get().getCantidadVentas();
			op.get().setCantidadVentas(tmp + 1);
			vn.setProductos(prs);
			vn.setVendedor(op.get());
			ventaRest.save(vn);
			vendedorRepository.save(op.get());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Productos añadidos");
		}
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

	@GetMapping("venta/vendedor/{id}")
	public ResponseEntity<Vendedor> getVentaVendedor(@PathVariable Integer id) {
		Optional<Venta> op = ventaRest.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(op.get().getVendedor());
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

	@GetMapping("venta/pdf")
	public ResponseEntity<byte[]> descargarPDFVentas() {
		List<Venta> ventas = ventaRest.findAll();

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ITextRenderer renderer = new ITextRenderer();
			String htmlContent = generateHtmlContent(ventas);
			renderer.setDocumentFromString(htmlContent);
			renderer.layout();
			renderer.createPDF(baos);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "ventas.pdf");

			return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
		} catch (Exception e) {
			System.err.println(e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String generateHtmlContent(List<Venta> ventas) {
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
		htmlBuilder.append("<h1>Lista de Ventas</h1>");
		htmlBuilder.append("<table class=\"my-table\">");
		htmlBuilder.append("<thead>");
		htmlBuilder.append("<tr>");
		htmlBuilder.append("<th>ID (Venta)</th>");
		htmlBuilder.append("<th>Vendedor</th>");
		htmlBuilder.append("<th>Sucursal</th>");

		htmlBuilder.append("<th>Productos de la venta</th>");
		htmlBuilder.append("</tr>");
		htmlBuilder.append("</thead>");
		htmlBuilder.append("<tbody>");

		for (Venta venta : ventas) {
			htmlBuilder.append("<tr>");
			htmlBuilder.append("<td>").append(venta.getId()).append("</td>");
			htmlBuilder.append("<td>").append(venta.getVendedor().getNombre()).append("</td>");
			htmlBuilder.append("<td>").append(venta.getVendedor().getTienda().getNombre()).append("</td>");
			htmlBuilder.append("<td>");
			venta.getProductos().forEach(p -> htmlBuilder.append("<p>").append(p.getProducto()).append(" (")
					.append(p.getId()).append(")</p>"));
			htmlBuilder.append("</td>");

			htmlBuilder.append("</tr>");
		}

		htmlBuilder.append("</tbody>");
		htmlBuilder.append("</table>");
		htmlBuilder.append("</body>");
		htmlBuilder.append("</html>");

		return htmlBuilder.toString();
	}

}
