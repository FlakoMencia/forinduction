/* global urlBuscarCcc */

let buscar = (idForm) => {
    var ccc = document.getElementById("cdCccSearch");
    fnResquestGet(urlBuscarCcc, {ccc: ccc.value}, "idCdTipoPersona", funcAfterBuscarCcc);
};

let funcAfterBuscarCcc = (response, selectorUpdate) => {
    if (!response) {
        Swal.fire({title: "¡El Código de Cuenta Corriente (CCC) ingresado no existe!", text: "Revise que el codigo de cuenta corriente ingresado sea correcto, si es asi acercarse a la alcaldía para modificar la información.", type: "error"});
        return false;
    }
    if (!response.success) {
        Swal.fire({html: `${response.message}`, title: "", type: "error"});
        return false;
    }
};

