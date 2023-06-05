if (window.location.pathname.endsWith('index.html') || !window.location.pathname.endsWith('surcusal.html')) {
	window.onload = inicial();
}

if (window.location.pathname.endsWith('surcusal.html')) {
	document.title = localStorage.getItem('nombreTienda');
	const navbarItems = document.querySelectorAll('.navbar-item');
	showWelcomeCard();
	navbarItems.forEach(item => {
		item.addEventListener('click', () => {
			navbarItems.forEach(navItem => {
				navItem.classList.remove('active');
			});
			item.classList.add('active');

			const sectionId = item.getAttribute('href').substring(1);
			const sections = document.querySelectorAll('section');
			sections.forEach(section => {
				section.style.display = 'none';
			});

			document.getElementById(sectionId).style.display = 'block';
			if (sectionId === 'vendedor') {
				const agregarVendedorBtn = document.getElementById('agregar-vendedor-btn');
				agregarVendedorBtn.addEventListener('click', showAgregarForm);

				const actualizarVendedorBtn = document.getElementById('actualizar-vendedor-btn');
				actualizarVendedorBtn.addEventListener('click', showActualizarForm);

				const anadirVentasBtn = document.getElementById('anadir-ventas-btn');
				anadirVentasBtn.addEventListener('click', showAnadirVentasForm);

				const listarVendedoresBtn = document.getElementById('listar-vendedores-btn');
				listarVendedoresBtn.addEventListener('click', showListarVendedoresForm);

				let formContainer = document.getElementById('form-container');
				let forms = {
					agregar: `
<div class="box is-widescreen">
  <form id="agregar-form" class="form" action="/vendedor" method="POST">
    <div class="field">
      <label class="label">Nombre</label>
      <div class="control">
        <input class="input is-fullwidth" type="text" name="nombre" pattern="^[a-zA-Z\\s]+$" title="Solo se permiten letras" required>
      </div>
    </div>
    <div class="field">
      <label class="label">Fecha de nacimiento</label>
      <div class="control">
        <input class="input is-fullwidth" type="date" name="nacimiento" required>
      </div>
    </div>
    <div class="field">
      <label class="label">Fecha de entrada</label>
      <div class="control">
        <input class="input is-fullwidth" type="date" name="entrada" required>
      </div>
    </div>
    <div class="field">
      <label class="label">Sexo</label>
      <div class="control">
        <div class="select is-primary is-fullwidth">
          <select class="select" name="sexo" required>
            <option value="masculino">Masculino</option>
            <option value="femenino">Femenino</option>
          </select>
        </div>
      </div>
    </div>
    <div class="field">
      <div class="control">
        <button class="button is-primary is-fullwidth" type="submit">Agregar</button>
      </div>
    </div>
  </form>
</div>
          `,

					actualizar: `
    <div class="box is-widescreen">
      <form id="actualizar-form" class="form" action="/vendedor" method="PUT">
        <div class="field">
          <label class="label">Vendedor</label>
          <div class="control">
            <div class="select is-primary is-fullwidth">
              <select class="select" name="vendedor" required>
                
              </select>
            </div>
          </div>
        </div>
        <div class="field">
          <label class="label">Nuevo nombre</label>
          <div class="control">
            <input class="input is-fullwidth" type="text" name="nombre" pattern="^[a-zA-Z\\s]+$" title="Solo se permiten letras" required>
          </div>
        </div>
        <div class="field">
          <label class="label">Nueva fecha de nacimiento</label>
          <div class="control">
            <input class="input is-fullwidth" type="date" name="nacimiento" required>
          </div>
        </div>
        <div class="field">
          <label class="label">Nueva fecha de entrada</label>
          <div class="control">
            <input class="input is-fullwidth" type="date" name="entrada" required>
          </div>
        </div>
        <div class="field">
          <label class="label">Nuevo sexo</label>
          <div class="control">
            <div class="select is-primary is-fullwidth">
              <select class="select" name="sexo" required>
                <option value="masculino">Masculino</option>
                <option value="femenino">Femenino</option>
              </select>
            </div>
          </div>
        </div>
        <div class="field">
          <div class="control">
            <button class="button is-primary is-fullwidth" type="submit">Actualizar</button>
          </div>
        </div>
      </form>
    </div>
  `,

					anadirVentas: `
    <div class="box is-widescreen">
      <form id="anadir-ventas-form" class="form" action="/venta" method="POST">
        <div class="field">
          <label class="label">Vendedor</label>
          <div class="control">
            <div class="select is-primary is-fullwidth">
              <select class="select" name="vendedor" required>

              </select>
            </div>
          </div>
        </div>
        <div class="field">
          <label class="label">Productos</label>
          <div class="control">
            <table id="productos-table" class="table is-bordered is-striped is-hoverable is-fullwidth">
              <thead>
                <tr>
                  <th>Seleccionar</th>
                  <th>ID</th>
                  <th>Nombre</th>
                 
                </tr>
              </thead>
              <tbody>
               
              </tbody>
            </table>
          </div>
        </div>
        <div class="field">
          <div class="control">
            <button class="button is-primary is-fullwidth" type="submit">Añadir Venta</button>
          </div>
        </div>
      </form>
    </div>
  `
				};

				function showAgregarForm() {
					formContainer.innerHTML = forms.agregar;
					const agregarForm = document.getElementById('agregar-form');
					agregarForm.addEventListener('submit', function(event) {
						event.preventDefault();
						agregarVendedor();
					});
				}

				function showActualizarForm() {
					disableButtons();
					const idTienda = localStorage.getItem('idTienda');
					fetch(`http://localhost:8081/api/vendedor/tienda/${idTienda}`)
						.then(response => response.json())
						.then(data => {
							const actualizarForm = document.createElement('div');
							actualizarForm.innerHTML = forms.actualizar;

							const vendedorSelect = actualizarForm.querySelector('select[name="vendedor"]');
							vendedorSelect.innerHTML = '';

							data.forEach(element => {
								const option = document.createElement('option');
								option.value = element.id;
								option.text = element.nombre;
								vendedorSelect.appendChild(option);
							});

							const formContainer = document.getElementById('form-container');
							formContainer.innerHTML = '';
							formContainer.appendChild(actualizarForm);
							enableButtons();
							const actualizarFormSubmit = actualizarForm.querySelector('form');
							actualizarFormSubmit.addEventListener('submit', function(event) {
								event.preventDefault();
								actualizarVendedor();
							});
						})
						.catch(error => {
							enableButtons();
							const messageContainer = document.getElementById('message-container');

							const message = document.createElement('article');
							message.classList.add('message', 'is-danger');

							const messageHeader = document.createElement('div');
							messageHeader.classList.add('message-header');

							const messageTitle = document.createElement('p');
							messageTitle.textContent = 'Error';

							const closeButton = document.createElement('button');
							closeButton.classList.add('delete');
							closeButton.setAttribute('aria-label', 'delete');
							closeButton.addEventListener('click', () => {
								message.remove();
							});

							messageHeader.appendChild(messageTitle);
							messageHeader.appendChild(closeButton);

							const messageBody = document.createElement('div');
							messageBody.classList.add('message-body');
							messageBody.textContent = 'No hay vendedores en la surcursal: ' + error;

							message.appendChild(messageHeader);
							message.appendChild(messageBody);

							messageContainer.appendChild(message);
						});
				}

				function showAnadirVentasForm() {
					disableButtons();
					formContainer.innerHTML = forms.anadirVentas;

					const vendedorSelect = document.querySelector('select[name="vendedor"]');
					const productosTable = document.getElementById('productos-table');

					const idTienda = localStorage.getItem('idTienda');
					fetch(`http://localhost:8081/api/vendedor/tienda/${idTienda}`)
						.then(response => response.json())
						.then(data => {
							data.forEach(vendedor => {
								const option = document.createElement('option');
								option.value = vendedor.id;
								option.text = vendedor.nombre;
								vendedorSelect.appendChild(option);
								enableButtons();
							});
						})
						.catch(error => {
							formContainer.innerHTML = '';
							enableButtons();
							const messageContainer = document.getElementById('message-container');

							const message = document.createElement('article');
							message.classList.add('message', 'is-danger');

							const messageHeader = document.createElement('div');
							messageHeader.classList.add('message-header');

							const messageTitle = document.createElement('p');
							messageTitle.textContent = 'Error';

							const closeButton = document.createElement('button');
							closeButton.classList.add('delete');
							closeButton.setAttribute('aria-label', 'delete');
							closeButton.addEventListener('click', () => {
								message.remove();
							});

							messageHeader.appendChild(messageTitle);
							messageHeader.appendChild(closeButton);

							const messageBody = document.createElement('div');
							messageBody.classList.add('message-body');
							messageBody.textContent = 'No hay vendedores en la surcusal: ' + error;

							message.appendChild(messageHeader);
							message.appendChild(messageBody);

							messageContainer.appendChild(message);
						});

					fetch('http://localhost:8081/api/producto/asignar')
						.then(response => response.json())
						.then(data => {
							data.forEach(producto => {

								const tr = document.createElement('tr');
								const tdCheckbox = document.createElement('td');
								const checkbox = document.createElement('input');
								checkbox.type = 'checkbox';
								checkbox.name = 'productos';
								checkbox.value = producto.id;
								tdCheckbox.appendChild(checkbox);

								const tdId = document.createElement('td');
								tdId.textContent = producto.id;

								const tdNombre = document.createElement('td');
								tdNombre.textContent = producto.producto;

								const tdPrecio = document.createElement('td');
								tdPrecio.textContent = producto.precio;

								tr.appendChild(tdCheckbox);
								tr.appendChild(tdId);
								tr.appendChild(tdNombre);

								productosTable.querySelector('tbody').appendChild(tr);

							});
						})
						.catch(error => {
							enableButtons();
							formContainer.innerHTML = '';
							const messageContainer = document.getElementById('message-container');

							const message = document.createElement('article');
							message.classList.add('message', 'is-danger');

							const messageHeader = document.createElement('div');
							messageHeader.classList.add('message-header');

							const messageTitle = document.createElement('p');
							messageTitle.textContent = 'Error';

							const closeButton = document.createElement('button');
							closeButton.classList.add('delete');
							closeButton.setAttribute('aria-label', 'delete');
							closeButton.addEventListener('click', () => {
								message.remove();
							});

							messageHeader.appendChild(messageTitle);
							messageHeader.appendChild(closeButton);

							const messageBody = document.createElement('div');
							messageBody.classList.add('message-body');
							messageBody.textContent = 'No hay productos: ' + error;

							message.appendChild(messageHeader);
							message.appendChild(messageBody);

							messageContainer.appendChild(message);
						});

					const anadirVentasForm = document.getElementById('anadir-ventas-form');
					anadirVentasForm.addEventListener('submit', function(event) {
						event.preventDefault();
						const formData = new FormData(anadirVentasForm);
						const vendedorId = formData.get('vendedor');
						const productosSeleccionados = formData.getAll('productos');
						var formdata = new FormData();
						productosSeleccionados.forEach(producto => {
							formdata.append('productosSeleccionados', producto);
						});
						fetch(`http://localhost:8081/api/venta/producto/${vendedorId}`, {
							method: 'POST',
							body: formdata
						})
							.then(response => {
								if (response.ok) {
									const messageContainer = document.getElementById('message-container');

									const message = document.createElement('article');
									message.classList.add('message', 'is-success');

									const messageHeader = document.createElement('div');
									messageHeader.classList.add('message-header');

									const messageTitle = document.createElement('p');
									messageTitle.textContent = 'Success';

									const closeButton = document.createElement('button');
									closeButton.classList.add('delete');
									closeButton.setAttribute('aria-label', 'delete');
									closeButton.addEventListener('click', () => {
										message.remove();
									});

									messageHeader.appendChild(messageTitle);
									messageHeader.appendChild(closeButton);

									const messageBody = document.createElement('div');
									messageBody.classList.add('message-body');
									messageBody.textContent = 'Venta añadida';

									message.appendChild(messageHeader);
									message.appendChild(messageBody);

									messageContainer.appendChild(message);
									showAnadirVentasForm();
									return response.text();

								} else {
									throw new Error('Error al añadir productos');
								}
							})
							.then(data => {

							})
							.catch(error => {
								const messageContainer = document.getElementById('message-container');

								const message = document.createElement('article');
								message.classList.add('message', 'is-danger');

								const messageHeader = document.createElement('div');
								messageHeader.classList.add('message-header');

								const messageTitle = document.createElement('p');
								messageTitle.textContent = 'Error';

								const closeButton = document.createElement('button');
								closeButton.classList.add('delete');
								closeButton.setAttribute('aria-label', 'delete');
								closeButton.addEventListener('click', () => {
									message.remove();
								});

								messageHeader.appendChild(messageTitle);
								messageHeader.appendChild(closeButton);

								const messageBody = document.createElement('div');
								messageBody.classList.add('message-body');
								messageBody.textContent = 'Venta no realizada: ' + error;

								message.appendChild(messageHeader);
								message.appendChild(messageBody);

								messageContainer.appendChild(message);
							});
					});
				}


				function showListarVendedoresForm() {
					disableButtons();
					const formContainer = document.getElementById('form-container');
					formContainer.innerHTML = '';
					formContainer.innerHTML = '';
					const containerDiv = document.createElement('div');
					containerDiv.setAttribute('class', 'box is-widescreen');

					const table = document.createElement('table');
					table.setAttribute('class', 'table is-bordered is-striped is-hoverable is-fullwidth');
					const thead = document.createElement('thead');
					const trHead = document.createElement('tr');
					const headers = ['ID', 'Nombre', 'Fecha de Nacimiento', 'Fecha de Entrada', 'Sexo', 'Número de Ventas'];
					headers.forEach(headerText => {
						const th = document.createElement('th');
						th.textContent = headerText;
						trHead.appendChild(th);
					});
					thead.appendChild(trHead);
					table.appendChild(thead);

					const idTienda = localStorage.getItem('idTienda');
					fetch(`http://localhost:8081/api/vendedor/tienda/${idTienda}`)
						.then(response => response.json())
						.then(data => {
							const tbody = document.createElement('tbody');
							data.forEach(vendedor => {
								const tr = document.createElement('tr');
								const tdId = document.createElement('td');
								const tdNombre = document.createElement('td');
								const tdNacimiento = document.createElement('td');
								const tdEntrada = document.createElement('td');
								const tdSexo = document.createElement('td');
								const tdVentas = document.createElement('td');

								tdId.textContent = vendedor.id;
								tdNombre.textContent = vendedor.nombre;
								tdNacimiento.textContent = vendedor.fechaNacimiento;
								tdEntrada.textContent = vendedor.fechaEntrada;
								tdSexo.textContent = vendedor.sexo;
								tdVentas.textContent = vendedor.cantidadVentas;

								tr.appendChild(tdId);
								tr.appendChild(tdNombre);
								tr.appendChild(tdNacimiento);
								tr.appendChild(tdEntrada);
								tr.appendChild(tdSexo);
								tr.appendChild(tdVentas);

								tbody.appendChild(tr);
							});

							table.appendChild(tbody);

							containerDiv.appendChild(table);

							const downloadBtn = document.createElement('button');
							downloadBtn.setAttribute('class', 'button is-link');
							downloadBtn.textContent = 'Descargar PDF Vendedores';

							const buttonContainer = document.createElement('div');
							buttonContainer.setAttribute('style', 'display: flex; justify-content: center;');
							buttonContainer.appendChild(downloadBtn);

							containerDiv.appendChild(buttonContainer);

							downloadBtn.addEventListener('click', () => {
								fetch(`http://localhost:8081/api/vendedor/pdf/${idTienda}`, {
									headers: {
										Accept: 'application/pdf'
									}
								})
									.then(response => response.blob())
									.then(blob => {
										const url = URL.createObjectURL(blob);
										const a = document.createElement('a');
										a.href = url;
										a.download = 'vendedores-' + localStorage.getItem('nombreTienda') + '.pdf';
										a.click();
										URL.revokeObjectURL(url);
										formContainer.innerHTML = '';
									})
									.catch(error => {
										console.error(error);
									});
							});

							formContainer.appendChild(containerDiv);
							enableButtons();
						})
						.catch(error => {
							enableButtons();
							const messageContainer = document.getElementById('message-container');

							const message = document.createElement('article');
							message.classList.add('message', 'is-danger');

							const messageHeader = document.createElement('div');
							messageHeader.classList.add('message-header');

							const messageTitle = document.createElement('p');
							messageTitle.textContent = 'Error';

							const closeButton = document.createElement('button');
							closeButton.classList.add('delete');
							closeButton.setAttribute('aria-label', 'delete');
							closeButton.addEventListener('click', () => {
								message.remove();
							});

							messageHeader.appendChild(messageTitle);
							messageHeader.appendChild(closeButton);

							const messageBody = document.createElement('div');
							messageBody.classList.add('message-body');
							messageBody.textContent = 'No hay vendedores en la sucursal: ' + error;

							message.appendChild(messageHeader);
							message.appendChild(messageBody);

							messageContainer.appendChild(message);
						});
				}

			} else if (sectionId === 'inventario') {
				const anadirProductoBtn = document.getElementById('agregar-producto-btn');
				anadirProductoBtn.addEventListener('click', showAnadirProductoForm);

				const listarProductoBtn = document.getElementById('listar-productos-btn');
				listarProductoBtn.addEventListener('click', showListarProducto);

				const listarVentaBtn = document.getElementById('listar-ventas-btn');
				listarVentaBtn.addEventListener('click', showListarVenta);

				const eliminarProductoBtn = document.getElementById('eliminar-producto-btn');
				eliminarProductoBtn.addEventListener('click', showEliminarProductoForm);

				let formContainer = document.getElementById('form-container');
				let forms = {
					anadirProducto: `
      <div class="box is-widescreen">
        <form id="anadir-producto-form" class="form" action="/producto" method="POST">
          <div class="field">
            <label class="label">Nombre</label>
            <div class="control">
              <input class="input is-fullwidth" type="text" name="nombre" pattern="^[a-zA-Z\\s]+$" title="Solo se permiten letras" required>
            </div>
          </div>
          <div class="field">
            <div class="control">
              <button class="button is-primary is-fullwidth" type="submit">Añadir Producto</button>
            </div>
          </div>
        </form>
      </div>
    `,
					eliminarProducto: `
      <div class="box is-widescreen">
        <form id="eliminar-producto-form" class="form" action="/producto" method="DELETE">
          <div class="field">
            <label class="label">Producto</label>
            <div class="control">
              <div class="select is-danger is-fullwidth">
                <select class="select" name="producto" required>
                  
                </select>
              </div>
            </div>
          </div>
          <div class="field">
            <div class="control">
              <button class="button is-danger is-fullwidth" type="submit">Eliminar Producto</button>
                       </div>
          </div>
        </form>
      </div>
    `};
				function showListarVenta() {
					disableButtons();
					const formContainer = document.getElementById('form-container2');
					formContainer.innerHTML = '';
					formContainer.innerHTML = '';

					fetch('http://localhost:8081/api/venta')
						.then(response => response.json())
						.then(data => {
							const tableContainer = document.createElement('div');
							tableContainer.setAttribute('class', 'box is-widescreen');
							const table = document.createElement('table');
							table.setAttribute('class', 'table is-bordered is-striped is-hoverable is-fullwidth');
							const thead = document.createElement('thead');
							const trHead = document.createElement('tr');
							const thId = document.createElement('th');
							thId.textContent = 'ID (Venta)';
							const thVendedor = document.createElement('th');
							thVendedor.textContent = 'Vendedor';
							const thProductos = document.createElement('th');
							thProductos.textContent = 'Productos';
							trHead.appendChild(thId);
							trHead.appendChild(thVendedor);
							trHead.appendChild(thProductos);
							thead.appendChild(trHead);
							table.appendChild(thead);
							const tbody = document.createElement('tbody');
							data.forEach(venta => {
								const tr = document.createElement('tr');
								const tdId = document.createElement('td');
								tdId.textContent = venta.id;
								const tdVendedor = document.createElement('td');
								const tdProductos = document.createElement('td');

								fetch(`http://localhost:8081/api/venta/vendedor/${venta.id}`)
									.then(response => response.json()

									)
									.then(vendedor => {
										tdVendedor.textContent = vendedor.nombre;
									})
									.catch(error => {
										enableButtons();
										formContainer.innerHTML = '';
										console.error(error);
										tdVendedor.textContent = 'Error al obtener el nombre del vendedor.';
									});

								venta.productos.forEach(producto => {
									const spanProducto = document.createElement('span');
									spanProducto.textContent = producto.producto;
									tdProductos.appendChild(spanProducto);
									tdProductos.appendChild(document.createElement('br'));
								});

								tr.appendChild(tdId);
								tr.appendChild(tdVendedor);
								tr.appendChild(tdProductos);
								tbody.appendChild(tr);
							});
							const downloadButton = document.createElement('button');
							downloadButton.setAttribute('class', 'button is-link is-fullwidth');
							downloadButton.textContent = 'Descargar PDF Ventas';
							table.appendChild(tbody);
							tableContainer.appendChild(table);
							tableContainer.appendChild(downloadButton);
							enableButtons();
							downloadButton.addEventListener('click', function() {
								fetch('http://localhost:8081/api/venta/pdf')
									.then(response => response.blob())
									.then(blob => {
										const filename = 'ventas.pdf';

										const link = document.createElement('a');
										if (link.download !== undefined) {
											const url = URL.createObjectURL(blob);
											link.setAttribute('href', url);
											link.setAttribute('download', filename);
											link.style.visibility = 'hidden';
											document.body.appendChild(link);
											link.click();
											document.body.removeChild(link);

										}
									})
									.catch(error => {
										enableButtons();
										const messageContainer = document.getElementById('message-container2');
										const message = document.createElement('article');
										message.classList.add('message', 'is-danger');
										const messageHeader = document.createElement('div');
										messageHeader.classList.add('message-header');
										const messageTitle = document.createElement('p');
										messageTitle.textContent = 'Error';
										const closeButton = document.createElement('button');
										closeButton.classList.add('delete');
										closeButton.setAttribute('aria-label', 'delete');
										closeButton.addEventListener('click', () => {
											message.remove();
										});
										messageHeader.appendChild(messageTitle);
										messageHeader.appendChild(closeButton);
										const messageBody = document.createElement('div');
										messageBody.classList.add('message-body');
										messageBody.textContent = "Error al descargar el pdf: " + error;
										message.appendChild(messageHeader);
										message.appendChild(messageBody);
										messageContainer.appendChild(message);
									});
							});

							formContainer.appendChild(tableContainer);
							formContainer.appendChild(downloadButton);
						})
						.catch(error => {
							enableButtons();
							const messageContainer = document.getElementById('message-container2');
							const message = document.createElement('article');
							message.classList.add('message', 'is-danger');
							const messageHeader = document.createElement('div');
							messageHeader.classList.add('message-header');
							const messageTitle = document.createElement('p');
							messageTitle.textContent = 'Error';
							const closeButton = document.createElement('button');
							closeButton.classList.add('delete');
							closeButton.setAttribute('aria-label', 'delete');
							closeButton.addEventListener('click', () => {
								message.remove();
							});
							messageHeader.appendChild(messageTitle);
							messageHeader.appendChild(closeButton);
							const messageBody = document.createElement('div');
							messageBody.classList.add('message-body');
							messageBody.textContent = "No hay ventas: " + error;
							message.appendChild(messageHeader);
							message.appendChild(messageBody);
							messageContainer.appendChild(message);
						});
				}

				function showListarProducto() {
					disableButtons();
					const formContainer = document.getElementById('form-container2');
					formContainer.innerHTML = '';

					const containerDiv = document.createElement('div');
					containerDiv.setAttribute('class', 'box is-widescreen');

					const table = document.createElement('table');
					table.setAttribute('class', 'table is-bordered is-striped is-hoverable is-fullwidth');
					const thead = document.createElement('thead');
					const headerRow = document.createElement('tr');
					const headers = ['ID', 'Producto', 'Vendido'];

					headers.forEach(headerText => {
						const th = document.createElement('th');
						th.textContent = headerText;
						headerRow.appendChild(th);
					});

					thead.appendChild(headerRow);
					table.appendChild(thead);

					const tbody = document.createElement('tbody');

					fetch('http://localhost:8081/api/producto')
						.then(response => response.json())
						.then(data => {
							if (data && data.length > 0) {
								data.forEach(producto => {
									const row = document.createElement('tr');
									const idCell = document.createElement('td');
									idCell.textContent = producto.id;
									row.appendChild(idCell);
									const productoCell = document.createElement('td');
									productoCell.textContent = producto.producto;
									row.appendChild(productoCell);
									const esVendidoCell = document.createElement('td');
									esVendidoCell.textContent = producto.vendido ? "Sí" : "No";
									row.appendChild(esVendidoCell);
									tbody.appendChild(row);
								});

								table.appendChild(tbody);

								containerDiv.appendChild(table);

								const downloadButton = document.createElement('button');
								downloadButton.setAttribute('class', 'button is-link is-fullwidth');
								downloadButton.textContent = 'Descargar PDF Inventario';
								enableButtons();
								downloadButton.addEventListener('click', function() {
									fetch('http://localhost:8081/api/producto/pdf')
										.then(response => response.blob())
										.then(blob => {

											const filename = 'productos.pdf';

											const link = document.createElement('a');
											if (link.download !== undefined) {
												const url = URL.createObjectURL(blob);
												link.setAttribute('href', url);
												link.setAttribute('download', filename);
												link.style.visibility = 'hidden';
												document.body.appendChild(link);
												link.click();
												document.body.removeChild(link);

											}
										})
										.catch(error => {
											enableButtons();
											const messageContainer = document.getElementById('message-container2');
											const message = document.createElement('article');
											message.classList.add('message', 'is-danger');
											const messageHeader = document.createElement('div');
											messageHeader.classList.add('message-header');
											const messageTitle = document.createElement('p');
											messageTitle.textContent = 'Error';
											const closeButton = document.createElement('button');
											closeButton.classList.add('delete');
											closeButton.setAttribute('aria-label', 'delete');
											closeButton.addEventListener('click', () => {
												message.remove();
											});
											messageHeader.appendChild(messageTitle);
											messageHeader.appendChild(closeButton);
											const messageBody = document.createElement('div');
											messageBody.classList.add('message-body');
											messageBody.textContent = "Error al descargar el pdf: " + error;
											message.appendChild(messageHeader);
											message.appendChild(messageBody);
											messageContainer.appendChild(message);
										});
								});


								containerDiv.appendChild(downloadButton);
								formContainer.appendChild(containerDiv);
							} else {
								formContainer.textContent = 'No se encontraron productos.';
							}
						})
						.catch(error => {
							enableButtons();
							const messageContainer = document.getElementById('message-container2');
							const message = document.createElement('article');
							message.classList.add('message', 'is-danger');
							const messageHeader = document.createElement('div');
							messageHeader.classList.add('message-header');
							const messageTitle = document.createElement('p');
							messageTitle.textContent = 'Error';
							const closeButton = document.createElement('button');
							closeButton.classList.add('delete');
							closeButton.setAttribute('aria-label', 'delete');
							closeButton.addEventListener('click', () => {
								message.remove();
							});
							messageHeader.appendChild(messageTitle);
							messageHeader.appendChild(closeButton);
							const messageBody = document.createElement('div');
							messageBody.classList.add('message-body');
							messageBody.textContent = "No hay productos: " + error;
							message.appendChild(messageHeader);
							message.appendChild(messageBody);
							messageContainer.appendChild(message);
						});
				}

				function showAnadirProductoForm() {
					const formContainer = document.getElementById('form-container2');
					formContainer.innerHTML = forms.anadirProducto;

					const anadirProductoFormSubmit = document.getElementById('anadir-producto-form');
					anadirProductoFormSubmit.addEventListener('submit', function(event) {
						event.preventDefault();
						agregarProducto();
					});
				}
				function showEliminarProductoForm() {
					disableButtons();
					const formContainer = document.getElementById('form-container2');
					formContainer.innerHTML = forms.eliminarProducto;
					fetch('http://localhost:8081/api/producto/asignar')
						.then(response => response.json())
						.then(data => {
							const selectProducto = document.querySelector('select[name="producto"]');
							selectProducto.innerHTML = '';
							data.forEach(producto => {
								const option = document.createElement('option');
								option.value = producto.id;
								option.textContent = producto.producto;
								selectProducto.appendChild(option);
							});

							const eliminarProductoFormSubmit = document.getElementById('eliminar-producto-form');
							enableButtons();
							eliminarProductoFormSubmit.addEventListener('submit', function(event) {
								event.preventDefault();
								const selectedProductId = selectProducto.value;
								fetch(`http://localhost:8081/api/producto/asignar/${selectedProductId}`, {
									method: 'DELETE'
								})
									.then(response => {
										if (response.ok) {
											const messageContainer = document.getElementById('message-container2');

											const message = document.createElement('article');
											message.classList.add('message', 'is-success');

											const messageHeader = document.createElement('div');
											messageHeader.classList.add('message-header');

											const messageTitle = document.createElement('p');
											messageTitle.textContent = 'Success';

											const closeButton = document.createElement('button');
											closeButton.classList.add('delete');
											closeButton.setAttribute('aria-label', 'delete');
											closeButton.addEventListener('click', () => {
												message.remove();
											});

											messageHeader.appendChild(messageTitle);
											messageHeader.appendChild(closeButton);

											const messageBody = document.createElement('div');
											messageBody.classList.add('message-body');
											messageBody.textContent = "Se elimino el producto exitosamente";

											message.appendChild(messageHeader);
											message.appendChild(messageBody);

											messageContainer.appendChild(message);
											showEliminarProductoForm();
										} else {
											throw new Error('Error al eliminar el producto');
										}
									})
									.catch(error => {
										enableButtons();
										const messageContainer = document.getElementById('message-container2');

										const message = document.createElement('article');
										message.classList.add('message', 'is-danger');

										const messageHeader = document.createElement('div');
										messageHeader.classList.add('message-header');

										const messageTitle = document.createElement('p');
										messageTitle.textContent = 'Error';

										const closeButton = document.createElement('button');
										closeButton.classList.add('delete');
										closeButton.setAttribute('aria-label', 'delete');
										closeButton.addEventListener('click', () => {
											message.remove();
										});

										messageHeader.appendChild(messageTitle);
										messageHeader.appendChild(closeButton);

										const messageBody = document.createElement('div');
										messageBody.classList.add('message-body');
										messageBody.textContent = "No se pudo eliminar el producto: " + error;

										message.appendChild(messageHeader);
										message.appendChild(messageBody);

										messageContainer.appendChild(message);
									});
							});
						})
						.catch(error => {
							console.log(error);
						});
				}

			} else if (sectionId === 'despido') {
				const formContainer = document.getElementById('form-container3');

				const despedirIneficienciaBtn = document.getElementById('despedir-ineficiencia-btn');
				despedirIneficienciaBtn.addEventListener('click', showDespedirPorIneficiencia);

				const despedirVendedorBtn = document.getElementById('despedir-vendedor-btn');
				despedirVendedorBtn.addEventListener('click', showDespedirVendedorForm);

				function showDespedirPorIneficiencia() {
					disableButtons();
					formContainer.innerHTML = '';
					enableButtons();
					const id = localStorage.getItem('idTienda');

					const warningBox = document.createElement('div');
					warningBox.classList.add('box');

					const warningMessage = document.createElement('article');
					warningMessage.classList.add('message', 'is-warning');

					const messageBody = document.createElement('div');
					messageBody.classList.add('message-body', 'has-text-centered');
					messageBody.innerHTML = '<p><strong>Advertencia:</strong> Estás a punto de despedir por ineficiencia a los empleado de la surcursal, si no conoce lo que significa por favor revise el manual del trabajador.</p><p>Esta acción no se puede deshacer y perderás todos los datos de los vendedores despedidos. <strong>¿Estás seguro de que quieres continuar?</strong></p><p><u>Se le recurda que no se pueden despedir empleados si la surcursal solo tiene tres empleados</u></p>';

					warningMessage.appendChild(messageBody);

					const despedirButton = document.createElement('button');
					despedirButton.classList.add('button', 'is-warning', 'is-fullwidth');
					despedirButton.textContent = 'Despedir';
					despedirButton.addEventListener('click', () => {
						disableButtons();
						despedirVendedoresPorIneficiencia(id);
						enableButtons();
					});

					warningBox.appendChild(warningMessage);
					warningBox.appendChild(despedirButton);

					formContainer.appendChild(warningBox);
				}





				function showDespedirVendedorForm() {
					disableButtons();
					formContainer.innerHTML = '';
					formContainer.innerHTML = forms.despedirVendedor;
					const id = localStorage.getItem('idTienda');
					fetch(`http://localhost:8081/api/vendedor/tienda/${id}`)
						.then(response => response.json())
						.then(vendedores => {
							const selectVendedor = document.getElementById('sel-vendedor');
							vendedores.forEach(vendedor => {
								const option = document.createElement('option');
								option.value = vendedor.id;
								option.textContent = vendedor.nombre;
								selectVendedor.appendChild(option);
							});
							enableButtons();
							const despedirForm = document.getElementById('despedir-form');

							despedirForm.addEventListener('submit', event => {
								event.preventDefault();
								disableButtons();
								const selectedVendedorId = selectVendedor.value;

								fetch(`http://localhost:8081/api/vendedor/${selectedVendedorId}`, {
									method: 'DELETE',
									responseType: 'blob'
								})
									.then(response => {
										if (response.ok) {
											return response.blob();
										} else {
											throw new Error('Error al despedir al vendedor');
										}
									})
									.then(blob => {
										const fileName = 'carta_despido_' + selectedVendedorId + '.pdf';
										const link = document.createElement('a');
										const url = URL.createObjectURL(blob);
										link.href = url;
										link.download = fileName;
										link.style.visibility = 'hidden';
										document.body.appendChild(link);
										link.click();
										document.body.removeChild(link);
										URL.revokeObjectURL(url);
										enableButtons();
										formContainer.innerHTML = '';
									})
									.catch(error => {
										console.log(error)
									});
							});
						})
						.catch(error => {
							formContainer.innerHTML = '';
							enableButtons();
							const messageContainer = document.getElementById('message-container3');
							const message = document.createElement('article');
							message.classList.add('message', 'is-danger');
							const messageHeader = document.createElement('div');
							messageHeader.classList.add('message-header');
							const messageTitle = document.createElement('p');
							messageTitle.textContent = 'Error';
							const closeButton = document.createElement('button');
							closeButton.classList.add('delete');
							closeButton.setAttribute('aria-label', 'delete');
							closeButton.addEventListener('click', () => {
								message.remove();
							});
							messageHeader.appendChild(messageTitle);
							messageHeader.appendChild(closeButton);
							const messageBody = document.createElement('div');
							messageBody.classList.add('message-body');
							messageBody.textContent = "No hay vendedores en la sucursal: " + error;
							message.appendChild(messageHeader);
							message.appendChild(messageBody);
							messageContainer.appendChild(message);
						});

				}



				const forms = {
					despedirVendedor: `
        <div class="box is-widescreen">
            <form id="despedir-form" class="form" action="/despedir" method="POST">
                <div class="field">
                    <label class="label">Vendedor</label>
                    <div class="control">
                        <div class="select is-danger is-fullwidth">
                            <select name="vendedor" id="sel-vendedor" required>
                                
                            </select>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <div class="control">
                        <button class="button is-danger is-fullwidth" id="despedir-btn" type="submit">Despedir Vendedor</button>
                    </div>
                </div>
            </form>
        </div>
    `
				};

			} else if (sectionId === '') {

				showWelcomeCard();

			}
		});
	});
}

function inicial() {
	const loadingBar = document.getElementById('loading-bar');
	const ingresarBtn = document.getElementById('ingresar-btn');
	const menu = document.getElementById('menu');

	loadingBar.value = 15;

	fetch('http://localhost:8081/api/tienda')
		.then(response => response.json())
		.then(data => {
			loadingBar.value = 100;
			loadingBar.textContent = '100%';
			setTimeout(() => {
				loadingBar.style.display = 'none';
				ingresarBtn.style.display = 'block';
				menu.style.display = 'block';
			}, 200);

			data.forEach(element => {
				const option = document.createElement('option');
				option.value = element.id;
				option.text = element.nombre;
				menu.appendChild(option);
			});

			ingresarBtn.addEventListener('click', () => {
				const selectedOption = menu.options[menu.selectedIndex];
				const selectedValue = selectedOption.value;
				const selectedText = selectedOption.text;
				localStorage.setItem('idTienda', selectedValue);
				localStorage.setItem('nombreTienda', selectedText);
				window.location.href = 'surcusal.html';


			});
		})
		.catch(error => {
			const errorMessage = encodeURIComponent(error.message);
			window.location.href = `error.html?message=${errorMessage}`;
		});
}


function showWelcomeCard() {
	const id = localStorage.getItem('idTienda');
	fetch(`http://localhost:8081/api/tienda/${id}`)
		.then(response => response.json())
		.then(data => {
			const storeNameElement = document.getElementById('surcursal');
			const storeLocationElement = document.getElementById('ubicacion');
			storeNameElement.textContent = 'Bienvenido a la sucursal de ' + data.nombre;
			storeLocationElement.textContent = 'Dirección: ' + data.ubicacion;
			const welcomeCard = document.getElementById('welcome-card');
			welcomeCard.style.display = 'block';
		})
		.catch(error => {
			const errorMessage = encodeURIComponent(error.message);
			window.location.href = `error.html?message=${errorMessage}`;
		});
}
function agregarVendedor() {
	const nombreInput = document.querySelector('input[name="nombre"]');
	const nacimientoInput = document.querySelector('input[name="nacimiento"]');
	const entradaInput = document.querySelector('input[name="entrada"]');
	const sexoSelect = document.querySelector('select[name="sexo"]');

	const nombre = nombreInput.value;
	const nacimiento = nacimientoInput.value;
	const entrada = entradaInput.value;
	const sexo = sexoSelect.value;

	const data = new FormData();
	data.append('nombre', nombre);
	data.append('nacimiento', nacimiento);
	data.append('entrada', entrada);
	data.append('sexo', sexo);
	data.append('idTienda', localStorage.getItem('idTienda'));

	fetch('http://localhost:8081/api/vendedor', {
		method: 'POST',
		body: data
	})
		.then(response => {
			if (response.ok) {
				nombreInput.value = '';
				nacimientoInput.value = '';
				entradaInput.value = '';
				sexoSelect.value = '';
			} else {
				throw new Error('Error al agregar el vendedor');
			}
		})
		.then(responseText => {
			const messageContainer = document.getElementById('message-container');

			const message = document.createElement('article');
			message.classList.add('message', 'is-success');

			const messageHeader = document.createElement('div');
			messageHeader.classList.add('message-header');

			const messageTitle = document.createElement('p');
			messageTitle.textContent = 'Success';

			const closeButton = document.createElement('button');
			closeButton.classList.add('delete');
			closeButton.setAttribute('aria-label', 'delete');
			closeButton.addEventListener('click', () => {
				message.remove();
			});

			messageHeader.appendChild(messageTitle);
			messageHeader.appendChild(closeButton);

			const messageBody = document.createElement('div');
			messageBody.classList.add('message-body');
			messageBody.textContent = 'Se a creado exitosamente:';

			message.appendChild(messageHeader);
			message.appendChild(messageBody);
			messageContainer.appendChild(message);
		})
		.catch(error => {
			const messageContainer = document.getElementById('message-container');

			const message = document.createElement('article');
			message.classList.add('message', 'is-danger');

			const messageHeader = document.createElement('div');
			messageHeader.classList.add('message-header');

			const messageTitle = document.createElement('p');
			messageTitle.textContent = 'Error';

			const closeButton = document.createElement('button');
			closeButton.classList.add('delete');
			closeButton.setAttribute('aria-label', 'delete');
			closeButton.addEventListener('click', () => {
				message.remove();
			});

			messageHeader.appendChild(messageTitle);
			messageHeader.appendChild(closeButton);

			const messageBody = document.createElement('div');
			messageBody.classList.add('message-body');
			messageBody.textContent = 'No se a creado: ' + error;

			message.appendChild(messageHeader);
			message.appendChild(messageBody);

			messageContainer.appendChild(message);
		});
}

function actualizarVendedor() {
	const vendedorId = document.querySelector('select[name="vendedor"]').value;
	const nombreInput = document.querySelector('input[name="nombre"]');
	const nacimientoInput = document.querySelector('input[name="nacimiento"]');
	const entradaInput = document.querySelector('input[name="entrada"]');
	const sexoSelect = document.querySelector('select[name="sexo"]');
	const formContainer = document.getElementById('form-container');
	const formData = new FormData();
	formData.append('nombre', nombreInput.value);
	formData.append('nacimiento', nacimientoInput.value);
	formData.append('entrada', entradaInput.value);
	formData.append('sexo', sexoSelect.value);
	formData.append('id', vendedorId);

	fetch(`http://localhost:8081/api/vendedor/${vendedorId}`, {
		method: 'PUT',
		body: formData
	})
		.then(response => {
			if (response.ok) {

				nombreInput.value = '';
				nacimientoInput.value = '';
				entradaInput.value = '';
				sexoSelect.value = '';
				formContainer.innerHTML = '';
			} else {
				throw new Error('Error al actualizar el vendedor');
			}
		})
		.then(responseText => {
			const messageContainer = document.getElementById('message-container');

			const message = document.createElement('article');
			message.classList.add('message', 'is-success');

			const messageHeader = document.createElement('div');
			messageHeader.classList.add('message-header');

			const messageTitle = document.createElement('p');
			messageTitle.textContent = 'Success';

			const closeButton = document.createElement('button');
			closeButton.classList.add('delete');
			closeButton.setAttribute('aria-label', 'delete');
			closeButton.addEventListener('click', () => {
				message.remove();
			});

			messageHeader.appendChild(messageTitle);
			messageHeader.appendChild(closeButton);

			const messageBody = document.createElement('div');
			messageBody.classList.add('message-body');
			messageBody.textContent = 'Se a actualizado exitosamente';

			message.appendChild(messageHeader);
			message.appendChild(messageBody);

			messageContainer.appendChild(message);

		})
		.catch(error => {
			const messageContainer = document.getElementById('message-container');

			const message = document.createElement('article');
			message.classList.add('message', 'is-danger');

			const messageHeader = document.createElement('div');
			messageHeader.classList.add('message-header');

			const messageTitle = document.createElement('p');
			messageTitle.textContent = 'Error';

			const closeButton = document.createElement('button');
			closeButton.classList.add('delete');
			closeButton.setAttribute('aria-label', 'delete');
			closeButton.addEventListener('click', () => {
				message.remove();
			});

			messageHeader.appendChild(messageTitle);
			messageHeader.appendChild(closeButton);

			const messageBody = document.createElement('div');
			messageBody.classList.add('message-body');
			messageBody.textContent = 'No se a actualizado: ' + error;

			message.appendChild(messageHeader);
			message.appendChild(messageBody);

			messageContainer.appendChild(message);
		});
}
function agregarProducto() {
	const data = new FormData();
	const nombreInput = document.querySelector('input[name="nombre"]');
	const producto = nombreInput.value;
	data.append('producto', producto);
	fetch('http://localhost:8081/api/producto', {
		method: 'POST',
		body: data
	})
		.then(response => {
			if (response.ok) {
				const messageContainer = document.getElementById('message-container2');

				const message = document.createElement('article');
				message.classList.add('message', 'is-success');

				const messageHeader = document.createElement('div');
				messageHeader.classList.add('message-header');

				const messageTitle = document.createElement('p');
				messageTitle.textContent = 'Success';

				const closeButton = document.createElement('button');
				closeButton.classList.add('delete');
				closeButton.setAttribute('aria-label', 'delete');
				closeButton.addEventListener('click', () => {
					message.remove();
				});

				messageHeader.appendChild(messageTitle);
				messageHeader.appendChild(closeButton);

				const messageBody = document.createElement('div');
				messageBody.classList.add('message-body');
				messageBody.textContent = 'Producto añadido con exito';

				message.appendChild(messageHeader);
				message.appendChild(messageBody);

				messageContainer.appendChild(message);
				nombreInput.value = '';
			} else {
				throw new Error('Error al añadir el producto');
			}
		})
		.catch(error => {
			const messageContainer = document.getElementById('message-container2');
			const message = document.createElement('article');
			message.classList.add('message', 'is-danger');
			const messageHeader = document.createElement('div');
			messageHeader.classList.add('message-header');
			const messageTitle = document.createElement('p');
			messageTitle.textContent = 'Error';
			const closeButton = document.createElement('button');
			closeButton.classList.add('delete');
			closeButton.setAttribute('aria-label', 'delete');
			closeButton.addEventListener('click', () => {
				message.remove();
			});
			messageHeader.appendChild(messageTitle);
			messageHeader.appendChild(closeButton);
			const messageBody = document.createElement('div');
			messageBody.classList.add('message-body');
			messageBody.textContent = "No se pudo agregar el producto: " + error;
			message.appendChild(messageHeader);
			message.appendChild(messageBody);
			messageContainer.appendChild(message);
		});
}
function despedirVendedoresPorIneficiencia(id) {
	fetch(`http://localhost:8081/api/vendedor/tienda/ineficiencia/pdf/${id}`, {
		method: 'DELETE'
	})
		.then(response => {
			if (response.ok) {
				return response.blob();
			}
		})
		.then(blob => {
			const filename = 'cartas_despido.zip';
			const link = document.createElement('a');
			const url = URL.createObjectURL(blob);
			link.href = url;
			link.download = filename;
			link.style.display = 'none';
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			URL.revokeObjectURL(url);

			const messageContainer = document.getElementById('message-container3');

			const message = document.createElement('article');
			message.classList.add('message', 'is-info');

			const messageHeader = document.createElement('div');
			messageHeader.classList.add('message-header');

			const messageTitle = document.createElement('p');
			messageTitle.textContent = 'Despedidos';

			const closeButton = document.createElement('button');
			closeButton.classList.add('delete');
			closeButton.setAttribute('aria-label', 'delete');
			closeButton.addEventListener('click', () => {
				message.remove();
			});

			messageHeader.appendChild(messageTitle);
			messageHeader.appendChild(closeButton);

			const messageBody = document.createElement('div');
			messageBody.classList.add('message-body');
			messageBody.textContent = 'Se despidieron los empleados';

			message.appendChild(messageHeader);
			message.appendChild(messageBody);

			messageContainer.appendChild(message);
		})
		.catch(error => {
			const messageContainer = document.getElementById('message-container3');

			const message = document.createElement('article');
			message.classList.add('message', 'is-danger');

			const messageHeader = document.createElement('div');
			messageHeader.classList.add('message-header');

			const messageTitle = document.createElement('p');
			messageTitle.textContent = 'Error';

			const closeButton = document.createElement('button');
			closeButton.classList.add('delete');
			closeButton.setAttribute('aria-label', 'delete');
			closeButton.addEventListener('click', () => {
				message.remove();
			});

			messageHeader.appendChild(messageTitle);
			messageHeader.appendChild(closeButton);

			const messageBody = document.createElement('div');
			messageBody.classList.add('message-body');
			messageBody.textContent = 'No se pudo despedir a alguien, revise que se cumpla con las condiciones: ' + error;

			message.appendChild(messageHeader);
			message.appendChild(messageBody);

			messageContainer.appendChild(message);
		});
}

const buttons = document.querySelectorAll('button');

function disableButtons() {
	buttons.forEach(button => {
		button.disabled = true;
	});
}

function enableButtons() {
	buttons.forEach(button => {
		button.disabled = false;
	});
}

