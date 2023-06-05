package co.edu.unbosque.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import co.edu.unbosque.model.Tienda;
import co.edu.unbosque.model.Vendedor;
import co.edu.unbosque.model.Venta;
import co.edu.unbosque.repository.TiendaRepository;
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
	@Autowired
	private TiendaRepository tiendaRepository;

	@PostMapping("vendedor/venta")
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

	@PostMapping("vendedor")
	public ResponseEntity<String> add(@RequestParam String nombre,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate nacimiento,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate entrada, @RequestParam String sexo,
			@RequestParam Integer idTienda) {
		Vendedor vd = new Vendedor();
		vd.setNombre(nombre);
		vd.setFechaEntrada(LocalDate.parse(entrada.toString()));
		vd.setFechaNacimiento(LocalDate.parse(nacimiento.toString()));
		vd.setSexo(sexo);
		Optional<Tienda> op = tiendaRepository.findById(idTienda);
		vd.setTienda(op.get());
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
	public ResponseEntity<Vendedor> getVendedor(@PathVariable Integer id) {
		Optional<Vendedor> op = vendedorRepository.findById(id);
		if (op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(op.get());
		}

	}

	@GetMapping("vendedor/tienda/{id}")
	public ResponseEntity<List<Vendedor>> getVendedorTienda(@PathVariable Integer id) {
		List<Vendedor> list = vendedorRepository.findAllByTiendaId(id);
		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.FOUND).body(list);
		}

	}

	@DeleteMapping("vendedor/tienda/ineficiencia/pdf/{id}")
	public ResponseEntity<byte[]> deleteGeneral(@PathVariable Integer id) {
		List<Vendedor> list = vendedorRepository.findAllByTiendaId(id);
		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		int numDeletions = Math.max(0, list.size() - 3);
		int numCartas = numDeletions <= 3 ? numDeletions : 3;
		list.sort(Comparator.comparingInt(vendedor -> vendedor.getCantidadVentas()));
		for (int i = 0; i < numDeletions; i++) {
			Vendedor vendedor = list.get(i);
			vendedorRepository.delete(vendedor);
		}
		if (numCartas > 0) {
			try {
				ByteArrayOutputStream zipFileData = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(zipFileData);

				for (int i = 0; i < numCartas; i++) {
					Vendedor vendedor = list.get(i);
					byte[] cartaDespido = generateCartaDespido(vendedor).toByteArray();
					String fileName = "Carta_Despido_" + vendedor.getId() + ".pdf";
					addToZip(zipOutputStream, fileName, cartaDespido);
				}

				zipOutputStream.close();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.setContentDispositionFormData("attachment", "cartas_despido.zip");

				return new ResponseEntity<>(zipFileData.toByteArray(), headers, HttpStatus.OK);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@DeleteMapping("vendedor/{id}")
	public ResponseEntity<byte[]> delete(@PathVariable Integer id) {

		Optional<Vendedor> op = vendedorRepository.findById(id);

		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		Vendedor vendedor = op.get();
		try {
			byte[] cartaDespido = generateCartaDespido(vendedor).toByteArray();
			vendedorRepository.deleteById(id);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "carta_despido.pdf");
			return new ResponseEntity<>(cartaDespido, headers, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ByteArrayOutputStream generateCartaDespido(Vendedor vendedor) throws Exception {
		Document document = new Document();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, outputStream);
		Font timesRoman = new Font(Font.FontFamily.TIMES_ROMAN, 12);
		document.open();
		Paragraph title = new Paragraph("Carta de Despido", timesRoman);
		title.setAlignment(Element.ALIGN_CENTER);
		title.setSpacingAfter(10f);
		document.add(title);
		Paragraph saludo = new Paragraph("Estimado " + vendedor.getNombre() + ",", timesRoman);
		saludo.setSpacingAfter(10f);
		document.add(saludo);
		Paragraph cuerpo = new Paragraph(
				"Por medio de la presente, le comunicamos que su contrato de trabajo con nuestra empresa ha sido rescindido por causas objetivas, concretamente por bajo rendimiento en las ventas.\n\n"
						+ "Esta decisión se ha tomado después de evaluar su desempeño durante los últimos meses y comprobar que no ha cumplido con los objetivos establecidos por la dirección.\n\n"
						+ "Le agradecemos el tiempo que ha dedicado a nuestra empresa y le deseamos lo mejor en su futuro profesional.\n\n"
						+ "La fecha efectiva de su despido será el "
						+ LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
						+ ", por lo que le rogamos que entregue todo el material y documentación que tenga en su poder antes de esa fecha.\n\n"
						+ "Asimismo, le informamos que tiene derecho a una indemnización, que se le abonará mediante transferencia bancaria a la cuenta que nos indique.\n\n"
						+ "Para cualquier duda o consulta, puede ponerse en contacto con el departamento de recursos humanos.",
				timesRoman);
		cuerpo.setSpacingAfter(20f);
		document.add(cuerpo);
		Paragraph cierre = new Paragraph("Atentamente,\nEquipo de Recursos Humanos", timesRoman);
		document.add(cierre);

		document.close();

		return outputStream;
	}

	public void addToZip(ZipOutputStream zipOutputStream, String fileName, byte[] content) throws Exception {
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.write(content);
		zipOutputStream.closeEntry();
	}

	public void saveZipFile(byte[] content, String fileName) throws Exception {
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		fileOutputStream.write(content);
		fileOutputStream.close();
	}

	@PutMapping("vendedor/{id}")
	public ResponseEntity<String> update(@RequestParam String nombre,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate nacimiento,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate entrada, @RequestParam String sexo,
			@PathVariable Integer id) {
		Optional<Vendedor> op = vendedorRepository.findById(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			Vendedor vd = op.get();
			vd.setId(id);
			vd.setNombre(nombre);
			vd.setFechaEntrada(entrada);
			vd.setFechaNacimiento(nacimiento);
			vd.setSexo(sexo);
			vendedorRepository.save(vd);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Data update");
		}
	}

	@GetMapping("vendedor/pdf/{id}")
	public ResponseEntity<byte[]> descargarPDFVendedores(@PathVariable Integer id) {
		List<Vendedor> vendedores = vendedorRepository.findAllByTiendaId(id);

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ITextRenderer renderer = new ITextRenderer();
			String htmlContent = generateHtmlContent(vendedores);
			renderer.setDocumentFromString(htmlContent);
			renderer.layout();
			renderer.createPDF(baos);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "vendedores.pdf");

			return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String generateHtmlContent(List<Vendedor> vendedores) {
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
		htmlBuilder.append("<h1>Lista de Vendedores</h1>");
		htmlBuilder.append("<table class=\"my-table\">");
		htmlBuilder.append("<thead>");
		htmlBuilder.append("<tr>");
		htmlBuilder.append("<th>ID</th>");
		htmlBuilder.append("<th>Nombre</th>");
		htmlBuilder.append("<th>Fecha de Nacimiento</th>");
		htmlBuilder.append("<th>Fecha de Entrada</th>");
		htmlBuilder.append("<th>Sexo</th>");
		htmlBuilder.append("<th>Sucursal</th>");
		htmlBuilder.append("<th>Número de Ventas</th>");
		htmlBuilder.append("</tr>");
		htmlBuilder.append("</thead>");
		htmlBuilder.append("<tbody>");

		for (Vendedor vendedor : vendedores) {
			htmlBuilder.append("<tr>");
			htmlBuilder.append("<td>").append(vendedor.getId()).append("</td>");
			htmlBuilder.append("<td>").append(vendedor.getNombre()).append("</td>");
			htmlBuilder.append("<td>").append(vendedor.getFechaNacimiento()).append("</td>");
			htmlBuilder.append("<td>").append(vendedor.getFechaEntrada()).append("</td>");
			htmlBuilder.append("<td>").append(vendedor.getSexo()).append("</td>");
			htmlBuilder.append("<td>").append(vendedor.getTienda().getNombre()).append("</td>");
			htmlBuilder.append("<td>").append(vendedor.getCantidadVentas()).append("</td>");
			htmlBuilder.append("</tr>");
		}

		htmlBuilder.append("</tbody>");
		htmlBuilder.append("</table>");
		htmlBuilder.append("</body>");
		htmlBuilder.append("</html>");

		return htmlBuilder.toString();
	}
}