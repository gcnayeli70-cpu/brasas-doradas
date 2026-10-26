document.addEventListener('DOMContentLoaded', function () {
    const boton = document.getElementById('agregarLinea');
    if (boton) {
        boton.addEventListener('click', function () {
            const contenedor = document.getElementById('lineas');
            const primera = contenedor.querySelector('.linea-detalle');
            const nueva = primera.cloneNode(true);
            nueva.querySelector('input[name="cantidad"]').value = 1;
            contenedor.appendChild(nueva);
        });
    }
});