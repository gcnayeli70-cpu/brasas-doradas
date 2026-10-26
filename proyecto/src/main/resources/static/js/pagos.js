document.addEventListener('DOMContentLoaded', function () {
    const select = document.getElementById('metodoPago');
    const qrDiv = document.getElementById('qrYape');

    function toggleQr() {
        qrDiv.style.display = (select.value === 'YAPE_PLIN') ? 'block' : 'none';
    }

    if (select && qrDiv) {
        select.addEventListener('change', toggleQr);
        toggleQr();
    }

    const botonImprimir = document.getElementById('btnImprimir');
    if (botonImprimir) {
        botonImprimir.addEventListener('click', function () {
            window.print();
        });
    }
});