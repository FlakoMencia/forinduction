/* global Swal */
let fnResquestGet = (url = "", params = "", selectorUpdate = "", func = "", msjError = "") => {
    if (url) {
        $.get(url, params)
                .done((response) => executeFuncResponse(response, func, selectorUpdate))
                .fail(() => (Swal.fire({title: "¡Error!", text: `${(msjError) ? msjError : "Peticion no realizada."}`, type: "error"})));
}
};

let fnResquestPost = (url = "", params = "", selectorUpdate = "", func = "", msjError = "") => {
    if (url) {
        $.post(url, params)
                .done((response) => executeFuncResponse(response, func, selectorUpdate))
                .fail(() => (Swal.fire({title: "¡Error!", text: `${(msjError) ? msjError : "Peticion no realizada."}`, type: "error"})));
}
};

let fnResquestPostWithJson = (url = "", oJson = {}, selectorUpdate = "", func = "", msjError = "") => {
    if ((url) && Object.keys(oJson).length !== 0) {
        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(oJson),
            dataType: "json",
            contentType: 'application/json;charset=UTF-8'
        })
                .done((response) => executeFuncResponse(response, func, selectorUpdate))
                .fail(() => (Swal.fire({title: "¡Error!", text: `${(msjError) ? msjError : "Peticion no realizada."}`, type: "error"})));
}
};

let executeFuncResponse = (response, func, selectorUpdate) => {
    if (func) {
        new Function("'" + func(response, selectorUpdate) + "'");
    }
};
