/* global urlValidarCcc, urlHome */
let fnEnableRequiredDocument = (flag = false) => {
    let strMask = (flag) ? "99999999999999999999" : "999999999";
    let idDoc = (flag) ? "cdDui" : "cdPasaporte";
    let idDoc2 = (flag) ? "cdPasaporte" : "cdDui";
    let label1 = (flag) ? "Documento &Uacute;nico de Identidad (DUI):" : "N&uacute;mero de Pasaporte:";
    let label2 = (flag) ? "N&uacute;mero de Pasaporte:<b class='text-danger'>*</b>" : "Documento &Uacute;nico de Identidad (DUI):<b class='text-danger'>*</b>";
    let messageLength = (flag) ? "Debe tener 20 caracteres" : "Debe tener 9 caracteres";
    let messageFormat = (flag) ? "Debe tener un formato correcto" : "Debe tener un formato correcto";
    let label = document.getElementById(idDoc + "Label");
    let doc1 = document.getElementById(idDoc);
    let spanRequiredError = document.getElementById(idDoc + "Error1");
    let spanLengthError = document.getElementById(idDoc + "Error2");
    let spanFormatError = document.getElementById(idDoc + "Error3");
    doc1.setAttribute("disabled", true);
    doc1.removeAttribute("required");
    doc1.value = "";
    label.innerHTML = label1;
    spanRequiredError?spanRequiredError.remove():spanRequiredError;
    spanLengthError?spanLengthError.remove():spanLengthError;
    spanFormatError?spanFormatError.remove():spanFormatError;

    let doc2 = document.getElementById(idDoc2);
    label = document.getElementById(idDoc2 + "Label");
    doc2.setAttribute("required", true);
    doc2.removeAttribute("disabled");
    $("#" + idDoc2).inputmask(strMask);
    label.innerHTML = label2;
    spanRequiredError = createSpanRequiredError(idDoc2);
    spanLengthError = createSpanLengthError(idDoc2, messageLength);
    spanFormatError = createSpanFormatError(idDoc2, messageFormat);

    doc2.parentNode.insertBefore(spanRequiredError, doc2.nextSibling);
    doc2.parentNode.insertBefore(spanLengthError, doc2.nextSibling);
    doc2.parentNode.insertBefore(spanFormatError, doc2.nextSibling);
};

let fnEnableRequiredCcc = (flag = false) => {
    let ccc = document.getElementById("cdCcc");
    let label = document.getElementById("cdCccLabel");
    if (ccc) {
        let spanError = document.getElementById("cdCccError1");
        let spanLengthError = document.getElementById("cdCccError2");
        let spanFormatError = document.getElementById("cdCccError3");
        if (!flag) {
            ccc.setAttribute("disabled", true);
            ccc.removeAttribute("required");
            ccc.value = "";
            label.innerHTML = "N&uacute;mero de Cuenta Corriente";
            spanError.remove();
            return flag;
        }
        ccc.setAttribute("required", true);
        ccc.removeAttribute("disabled");
        label.innerHTML = "N&uacute;mero de Cuenta Corriente<b class='text-danger'>*</b>";
        spanError = createSpanRequiredError("cdCcc");
        spanLengthError = createSpanLengthError("cdCcc", "Debe tener 10 caracteres");
        spanFormatError = createSpanFormatError("cdCcc", "Debe tener un formato correcto");
        ccc.parentNode.insertBefore(spanError, ccc.nextSibling);
        ccc.parentNode.insertBefore(spanLengthError, ccc.nextSibling);
        ccc.parentNode.insertBefore(spanFormatError, ccc.nextSibling);
        return flag;
}
};

let createSpanRequiredError = (id) => {
    let spanError = document.createElement("span");
    spanError.id = id + "Error1";
    spanError.setAttribute("class", "error-form w-100 float-start");
    spanError.style = "display: none; color: red;";
    spanError.innerHTML = "Campo obligatorio";
    return spanError;
};

let createSpanLengthError = (id, message) => {
    let spanError = document.createElement("span");
    spanError.id = id + "Error2";
    spanError.setAttribute("class", "error-form w-100 float-start " + id + "-error-length");
    spanError.style = "display: none; color: red;";
    spanError.innerHTML = message;
    return spanError;
};

let createSpanFormatError = (id, message) => {
    let spanError = document.createElement("span");
    spanError.id = id + "Error3";
    spanError.setAttribute("class", "error-form w-100 float-start " + id + "-error-format");
    spanError.style = "display: none; color: red;";
    spanError.innerHTML = message;
    return spanError;
};

let registrar = (idForm) => {
    $(".error-form").hide();
    var ccc = document.getElementById("cdCcc");
    var user = document.getElementById("cdUserDelegate");
    if (validRequiredForm(idForm) && validPasswords("stPasswordDelegate", "passwordConfirm")) {
        if ((!ccc.disabled) && (ccc.value) && (user.value)) {
            fnResquestGet(urlValidarCcc, {ccc: ccc.value, user: user.value}, "idCdTipoPersona", funcAfterValidarCcc);
        } else {
            $("#" + idForm).addClass('was-validated');
            $("#" + idForm).submit();
        }
    }
};

let validRequiredForm = (idForm) => {
    let inputs = document.getElementById(idForm).elements;
    let cErrors = 0;
    let regexEmail = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    let regexNum = /[0-9]/;
    Array.from(inputs).forEach(function (elemento) {
        if (elemento.nodeName.toLowerCase() === "input" || elemento.nodeName.toLowerCase() === "select") {
            if ((elemento.required) && (!elemento.value)) {
                $("#" + elemento.id + "Error1").show();
                cErrors++;
            } else if ((elemento.classList.contains("emails")) && (!regexEmail.test(elemento.value))) {
                $("." + elemento.id + "-error-format").show();
                cErrors++;
            } else if ((elemento.classList.contains("nit-multiple")) && (!elemento.disabled)) {
                if (elemento.value.replaceAll("_", "").length !== 9 && elemento.value.replaceAll("_", "").length !== 14) {
                    $("." + elemento.id + "-error-length").show();
                    cErrors++;
                } else if (!regexNum.test(elemento.value)) {
                    $("." + elemento.id + "-error-format").show();
                    cErrors++;
                }
            } else if ((elemento.classList.contains("nit")) && (!elemento.disabled)) {
                if (elemento.value.replaceAll("_", "").length !== 14) {
                    $("." + elemento.id + "-error-length").show();
                    cErrors++;
                } else if (!regexNum.test(elemento.value)) {
                    $("." + elemento.id + "-error-format").show();
                    cErrors++;
                }
            } else if ((elemento.classList.contains("dui")) && (!elemento.disabled)) {
                if (elemento.value.replaceAll("_", "").length !== 9) {
                    $("." + elemento.id + "-error-length").show();
                    cErrors++;
                } else if (!regexNum.test(elemento.value)) {
                    $("." + elemento.id + "-error-format").show();
                    cErrors++;
                }
            } else if ((elemento.classList.contains("pasaporte")) && (!elemento.disabled)) {
                if (elemento.value.replaceAll("_", "").length !== 20) {
                    $("." + elemento.id + "-error-length").show();
                    cErrors++;
                } else if (!regexNum.test(elemento.value)) {
                    $("." + elemento.id + "-error-format").show();
                    cErrors++;
                }
            } else if ((elemento.classList.contains("ccc")) && (!elemento.disabled)) {
                if (elemento.value.replaceAll("_", "").length !== 10) {
                    $("." + elemento.id + "-error-length").show();
                    cErrors++;
                } else if (!regexNum.test(elemento.value)) {
                    $("." + elemento.id + "-error-format").show();
                    cErrors++;
                }
            }
        }
    });
    return (cErrors === 0);
};

let validPasswords = (idPassword, idPasswordConfirmacion) => {
    let password = document.getElementById(idPassword).value;
    let passwordConfirmacion = document.getElementById(idPasswordConfirmacion).value;
    let regexMayuscula = /[A-Z]/;
    let regexNumero = /[0-9]/;
    let regexCaracterEspecial = /^([0-9]|\w)+$/;
    if ((!regexMayuscula.test(password)) || (!regexNumero.test(password)) || (regexCaracterEspecial.test(password))) {
        $("#" + idPassword + "Error2").show();
        return false;
    }

    if (!(password.length >= 8 && password.length <= 12)) {
        $("#" + idPassword + "Error3").show();
        return false;
    }

    if (password !== passwordConfirmacion) {
        $("#" + idPasswordConfirmacion + "Error2").show();
        return false;
    }

    return true;
};

let funcAfterValidarCcc = (response, selectorUpdate) => {
    if (!response) {
        Swal.fire({title: "¡El C&oacute;digo de Cuenta Corriente (CCC) ingresado no existe!",
            html: "<p class='fw-bold fs-4'>Revise que el c&oacute;digo de cuenta corriente ingresado sea correcto, si es as&iacute; acercarse a la alcald&iacute;a para modificar la informaci&oacute;n.</p>",
            type: "error",
            confirmButtonText: "Aceptar",});
        return false;
    }
    if (!response.success) {
        Swal.fire({html: `${response.message}`, title: "", type: "error"});
        return false;
    }
    let tipoPersona = document.getElementById("" + selectorUpdate);
    let ccc = document.getElementById("idCcc");
    let skWs = document.getElementById("skWs");
    skWs.value = response.data.skWs;
    ccc.value = response.data.ccc;
    const msjTipoPersona1 = "<p class='fw-bold fs-4'>El Código de Cuenta Corriente (CCC) que has ingresado no coincide con el tipo de persona que has seleccionado en el paso 1 del registro de usuario.</p>";
    const msjTipoPersona2 = "<br><br><p class='fw-bold fs-4'>Por favor verificar que el tipo de persona corresponda al CCC, de ser asi acercarse a la alcaldía para modificar la información.</p>";
    if (tipoPersona.value === "J") {
        if ((response.data.tipoPersona) && (response.data.tipoPersona !== "J")) {
            Swal.fire({html: `${msjTipoPersona1} ${msjTipoPersona2}`, title: "", type: "warning",confirmButtonText: "Aceptar"});
            return false;
        }
        let razonSocial = document.getElementById("idRazonSocial");
        let nombre = document.getElementById("idNombre");
        let nitRepresentante = document.getElementById("idNitRepresentante");
        let txtNitRepresentante = document.getElementById("idTxtNitRepresentante");
        let nrc = document.getElementById("idNrc");
        razonSocial.value = response.data.razonSocial;
        nombre.value = response.data.nombre;
        nitRepresentante.value = response.data.nitRepresentante;
        nrc.value = response.data.nrc;
        let tipoRepresentante = document.querySelector("input[name='cdTipoRepresentante']:checked");
        txtNitRepresentante.innerHTML = (tipoRepresentante.value === "R") ? "NIT del Representante Legal:" : "NIT del Apoderado Legal:";
    } else {
        if ((response.data.tipoPersona) && (response.data.tipoPersona !== "N")) {
            Swal.fire({html: `${msjTipoPersona1} ${msjTipoPersona2}`, title: "", type: "warning", confirmButtonText: "Aceptar"});
            return false;
        }
        let nombres = document.getElementById("idNombres");
        let apellidos = document.getElementById("idApellidos");
        let dui = document.getElementById("idDui");
        let pasaporte = document.getElementById("idPasaporte");
        let nit = document.getElementById("idNit");
        nombres.value = response.data.nombres;
        apellidos.value = response.data.apellidos;
        dui.value = "";
        pasaporte.value = "";
        let boxDui = document.getElementById("display-idDui");
        let boxPasaporte = document.getElementById("display-idPasaporte");
        if (!boxDui.classList.contains("d-none")) {
            boxDui.classList.add("d-none");
        }
        if (!boxPasaporte.classList.contains("d-none")) {
            boxPasaporte.classList.add("d-none");
        }
        dui.value = "";
        pasaporte.value = "";
        if (response.data.dui) {
            dui.value = response.data.dui;
            boxDui.classList.remove("d-none");
        }
        if (response.data.pasaporte) {
            pasaporte.value = response.data.pasaporte;
            boxPasaporte.classList.remove("d-none");
        }
        nit.value = response.data.nit;
    }
    let direccionCobro = document.getElementById("idDireccionCobro");
    direccionCobro.value = response.data.direccionCobro;
    let empresas = document.getElementById("idEmpresasReg");
    empresas.value = response.data.empresas;
    $("#dialogValidCcc").modal('toggle');
};

let validar = (idForm) => {
    let datosCorrectos = document.querySelector("input[name='datosCorrectos']:checked");
    switch (datosCorrectos.value) {
        case "S":
            $("#" + idForm).submit();
            break;
        case "N":
            $("#dialogValidCcc").modal('hide');
            Swal.fire({title: "", html: "<p class='fw-bold fs-4'>Revise que el código de cuenta corriente ingresado sea correcto, si es así acerquese a la alcaldía para modificar la información.</p>", type: "warning",
                confirmButtonText: "Aceptar"});
            break
        default:
            $("#" + idForm).submit();
            break;
    }
};

