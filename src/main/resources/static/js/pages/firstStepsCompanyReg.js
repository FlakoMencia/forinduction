/****************************************************************************************
 ** Creado para primeros pasos en registro de empresa y registro de sucursales         **
 ****************************************************************************************/


var step = 1;

function nextStep() {
    step++;
    changeStep(step);
}

function backward() {
    step--;
    changeStep(step);
}

function showStep1() {
    $(".step1").removeAttr("hidden");
    $(".step1").show();
    $(".step2").attr("hidden", true);
    $(".step2").hide();
    $(".step3").attr("hidden", true);
    $(".step3").hide();
    $("#backStep").hide();
    $("#fwdStep").removeAttr("disabled");
}

function showStep2() {
    $(".step1").attr("hidden", true);
    $(".step1").hide();
    $(".step2").removeAttr("hidden");
    $(".step2").show();
    $(".step3").attr("hidden", true);
    $(".step3").hide();
    $("#backStep").removeAttr("hidden");
    $("#backStep").show();
    $("#fwdStep").removeAttr("disabled");
    $("#rowTitlePN").show();
    $("#rowTitlePJ").show();
}

function showStep3() {
    $(".step1").attr("hidden", true);
    $(".step1").hide();
    $(".step2").attr("hidden", true);
    $(".step2").hide();
    $(".step3").removeAttr("hidden");
    $(".step3").show();
    $("#rowTitlePN").hide();
    $("#rowTitlePJ").hide();
    $("#backStep").removeAttr("hidden");
    $("#backStep").show();
    $("#fwdStep").attr("disabled", true);

}

function changeStep(stepForCase) {
    switch (stepForCase) {
        case 1:
            showStep1();
            break;
        case 2:
            showStep2();
            break;
        case 3:
            showStep3();
            break;
        case 4:
            $("#aceptTermsForm").submit();
            // window.location.href = '../reg/company/data';
            break;
        default:
            showStep1();
            step = 1;
    }
}

function cancelSteps() {
    Swal.fire({
        title: '<strong>¿Desea Salir?</strong>',
        icon: 'info',
        text: 'De hacerlo, perderá todos los datos que ha ingresado.',
        showCloseButton: true,
        showCancelButton: true,
        focusCancel: true,
        cancelButtonText: 'No',
        confirmButtonText: 'Si',
        reverseButtons: true
        //  ,cancelButtonColor: '#d33' //confirmar si desean color rojo en este boton

    }).then(function (result) {
        if (result.value === true) {
            window.location.href = '../index';
        }
    });
}

function aceptTerms() {
    const chkboxTerminosYcond = $("#chkboxTerminosYcond").prop("checked");
    $("#fwdStep").attr("disabled", true);
    if(chkboxTerminosYcond == true){
        $("#fwdStep").removeAttr("disabled");
    }
}

function checkTermsOnSol(){
    var acepted = $("#aceptedTermsInput").val();
    if(acepted && acepted == 1){
        Swal.fire("¡No permitido!", "No puede rechazar términos con una solicitud en trámite.", "error");
        $("#chkboxTerminosYcond").prop("checked" , true);
        $("#fwdStep").removeAttr("disabled");
        return false;
    }
    aceptTerms();
}