/** Crea filtros individuales de cada campo en DataTable que sea visible.
 * 
 * @param {type} idTabla
 * @param {type} table
 * @return {undefined}
 */
function crearFiltrosDataTable(/*String*/ idTabla, /*DataTable*/ table) {
    // primero preproceso la metadata de la tabla
    var metadataColumna = table.settings()[0].aoColumns;
    
    var conteoColumnas = table.columns()[0];
    var posicionesValidas = conteoColumnas.filter(function(posicionColumna) {
        var columna = table.column(posicionColumna);
        return columna.visible();
    });
    
    var filasConPosicion = [];
    var contadorFilasVisibles = 0;
    for(var i in posicionesValidas) {
        var posicion = posicionesValidas[i];
        filasConPosicion[contadorFilasVisibles] = metadataColumna[posicion].bSearchable;
        contadorFilasVisibles++;
    }

    var filtrosFinales = $(idTabla + ' thead tr:last th')
            .clone(true);

    var elementoContenedor = $("<tr></tr>").html(filtrosFinales);
    elementoContenedor.appendTo(idTabla + ' thead');
    
    // se agregan unas listas de post-procesado de filtros
    let filtrosSelect2 = [];
    let filtrosDate = [];
    
    // objeto a usar por los filtros
    function Filtro(id, posicion) {
        this.id = "#" + id;
        this.posicion = posicion;
    }
    
    $(idTabla + ' thead tr:last th').each(function (i) {
        var title = $(this).text();
        if (!filasConPosicion[i]) {
            $(this).html('<input type="hidden" placeholder=" Buscar ' + title + '" />');
        } else {
            let posicionReal = posicionesValidas[i];
            
            // dejado para debugging de las salidas
//            console.log($(this)[0].outerHTML);
            
            /** obtengo el nombre final del campo ya sea de la propiedad 'name' 
             *  del encabezado o de la propiedad 'data' de la columna de dataTable.
             */
            let idSearch = $(this).attr('name') 
                        ? 'search_' + $(this).attr('name') 
                        : 'search_'
                                + idTabla.replace("#", "") + "_"
                                + metadataColumna[posicionReal].data;
            idSearch = idSearch.replace(".", "_");
            
            /** obtengo el tipo de filtro del campo ya sea de la propiedad 'stype' 
             *  del encabezado o de la propiedad 'filterType' de la columna de dataTable.
             */
            let tipoFiltro = $(this).attr("stype")
                        ? $(this).attr("stype") 
                        : metadataColumna[posicionReal].filterType;
            
            // filtro para select2
            if(tipoFiltro === 'select2'){
                $(this).html('<select data-column="' + posicionReal + '" id="' + idSearch +'" ></select>');
                filtrosSelect2.push(new Filtro(idSearch, posicionReal));
            } else if (tipoFiltro === "date"){
                $(this).html('<input type="text" placeholder=" Buscar ' + title + '" data-column="' + posicionReal + '" id="' + idSearch +'"/>');
                filtrosDate.push(new Filtro(idSearch, posicionReal));
            } else {
                $(this).html('<input type="text" placeholder=" Buscar ' + title + '" id="' + idSearch + '"  />');
                $('input', this).on('keyup change', function (e) {
                    let code = (e.keyCode ? e.keyCode : e.which);
                    if (code == 13) {
                        if (table.column(posicionReal).search() !== this.value) {
                            table
                                .column(posicionReal)
                                .search(this.value)
                                .draw();
                        }
                    }
                });
            }
        }
    });
    
    // ejecuto los filtros de select2
    filtrosSelect2.forEach(function(filtro) {
        var urlSelect2 = metadataColumna[filtro.posicion].filterUrl;
        renderDataTableSelect2(idTabla, filtro.id, urlSelect2, 5); 
    });
    
    // ejecuto los filtros de fecha
    filtrosDate.forEach(function(filtro) {
        renderDatatableDatePicker(idTabla, filtro.id);
    });
    
    // mejoro el metodo destroy para eliminar los filtros
    var destroyPrevio = table.destroy;
    table.destroy = function() {
        $(idTabla + ' thead tr:last').remove();
        destroyPrevio();
    };
}

function PeticionData(data, url, type) {
    this.data = "";
    this.url = "";
    this.type = "";
    
    if(data) {
        this.data = data;
    }
    if(url) {
        this.url = url;
    }
    if(type) {
        this.type = type;
    }
    
    this.procesarPeticionAjax = function(ejecutarSiExitoso) {
        Swal.showLoading("Procesando...");
        $.ajax({
            data: this.data ,
            url: this.url,
            type: this.type,
            success: function (result) {
                Swal.hideLoading();
                if (result.success === true) {
                    ejecutarSiExitoso(result);
                } else {
                    Swal.fire({title: "¡Error!", text: result.message, type:'error'});
                }
            },
            error: function (x, e,  thrownError) {
                Swal.hideLoading();
                Swal.fire({title: "¡Error!", text: "Error interno del servidor.", type:'error'});
            }
        });
    };
}

/** Pequenia clase para validar campos parcialmente. Esta clase asume que cada campo tiene una
 *  etiqueta que debe ser mostrada en caso de error.
 * 
 * @param {type} campo
 * @param {type} campoError
 * @return {Campo}
 */
function Campo(campo, campoError) {
    this.campo = campo;
    this.campoError = campoError;
    
    var self = this;

    this.validarCampo = function(/*boolean*/ scroll) {
        var scrollLocal = scroll ? true : false;
        var campoJquery = $(campo);
        var campoErrorJquery = $(campoError);
        var deshabilitado = campoJquery.is(":disabled") || campoJquery.prop("readonly") === true;
        if(!deshabilitado) {
            if(campoJquery.prop("tagName").toLowerCase() === "select") {
                return validarSelect(campoJquery, campoErrorJquery, scrollLocal);
            }
            else {
                return validarInput(campoJquery, campoErrorJquery, scrollLocal);
            }
        }
        else {
            campoErrorJquery.hide();
            return true;
        } 
    };

    function validarSelect(campo, campoError, scroll) {
        if(campo.is(":required")) {
            var isValido = !(campo.val() === null || campo.val() === "null");
            mostrarErrorSiCumple(isValido, campo, campoError, scroll);

            return isValido;
        }
        else return true;
    }

    function validarInput(campo, campoError, scroll) {
        var isValido = campo.is(':valid');
        mostrarErrorSiCumple(isValido, campo, campoError, scroll);

        return isValido;
    }

    function mostrarErrorSiCumple(condicion, campo, campoError, scroll) {
        if(!condicion) {
            campoError.show();
            if(scroll) {
                campo[0].scrollIntoView();
            }
        }
        else {
            campoError.hide();
        }
    }
    
    this.hideError = function() {
        $(this.campoError).hide();
    };
    
    this.addChangeListener = function() {
        $(this.campo).change(function() {
            self.validarCampo(false);
        });
        $(this.campo).keyup(function() {
            self.validarCampo(false);
        });
    };
}

/** Objeto que realiza las validaciones manuales. Se encarga de hacer las validaciones
 *  que agregan texto de campo vacio en caso en que el campo no cumpla.
 * 
 * @type {Validador} instancia unica que tiene varios metodos para validacion de formularios.
 */
var Validador = new function() {
    /** Funcion que crea la etiqueta de error en caso en que no haya sido creada.
     *  Esto se ha realizado para que sea facil agregar las validaciones de texto
     *  a formularios que fueron generados previamente.
     */
    var crearMensajeErrorSiNoExiste = function(/*String*/ selectorCampo, /*String*/ selectorError) {
        // si no tiene el elemento de error creado, debo crearlo.
        if($(selectorError).length === 0) {
            var elementoNuevo = $("<span></span>")
                    .prop("id", selectorError.replace("#", ""))
                    .css("display", "none")
                    .css("color", "red")
                    .html("Campo obligatorio")
                    ;
            
            /** obtengo al padre del elemento actual, ya que el nuevo elemento debe ser hermano
             *  del elemento a validar
            **/ 
            var padre = $(selectorCampo).parent();
            
            // si el padre es un input-group, se toma el padre del padre para que la etiqueta quede bien.
            if(padre.hasClass("input-group")) {
                padre = padre.parent();
            }
            
            // agrego el elemento creado al padre.
            padre.append(elementoNuevo);
        }
        
        return selectorError;
    };
    
    /** Funcion que obtiene de cada formulario todos sus inputs visibles al usuario
     *  y luego se guardan como objeto Campo, los que luego son retornados al usuario.
     * 
     */
    var camposForm = function(/*JquerySelector*/ formSelector) {
        // obtengo todos los campos del formulario
        var camposPorValidar = formSelector.find('input:text, input:password, input:file, input[type=number], select, textarea')
            .not("[persist], input[name='_csrf']");
    
        // obtengo los ids de cada campo
        var idsCampos = [];    
        camposPorValidar.each(function(indice, valor) {
            idsCampos.push(valor.id);
        });
        
        // creo un objeto Campo por cada id, asumiendo que cada campo tiene su texto de error
        var camposFinales = idsCampos.filter(function(id) {
            return id;
        }).map(function(id) {
            var selectorCampo = "#" + id;
            var campoErrorFinal = crearMensajeErrorSiNoExiste(selectorCampo, selectorCampo + "Error");
            return new Campo(selectorCampo, campoErrorFinal);
        });    
        
        return camposFinales;
    };
    
    /** Valida el formulario completo, obteniendo del formulario enviado
     *  todos sus campos como instancias del objeto Campo y luego procedo
     *  a validarlos todos. Si un campo llega a no ser valido se muestra su
     *  etiqueta de error creada o una generica.
     * 
     * @param {type} formSelector el formulario de jquery a usar.
     * @return si {true} el formulario tiene todos sus campos validos, {false} si no.
     */
    this.validarFormulario = function(/*JquerySelector*/ formSelector) {
        var camposFinales = camposForm(formSelector);
    
        // por ultimo valido cada campo
        return Validador.validarCampos(camposFinales);
    };
    
    /** Recorre el arreglo de objetos Campo y determina si todos son validos o no.
     *  Si un campo llega a no ser valido, muestra su mensaje de error.
     * 
     * @param {type} campos el arreglo de campos a validar.
     * @return {true} si todos los campos estan validos, {false} si no.
     */
    this.validarCampos = function(/*Array<Campo>*/ campos) {
        return campos.map(function(campo) {
            return campo.validarCampo(true);
        }).every(function(valor) {
            return valor;
        });
    };
    
    /** Esconde todos los elementos que fueron validados previamente, dejando
     *  el formulario como si nunca hubiera sido validado.
     * 
     * @param {type} formSelector el selector de jquery del formulario a resetear.
     * @return {undefined}
     */
    this.resetValidacion = function(/*JquerySelector*/ formSelector) {
        var camposFinales = camposForm(formSelector);
        camposFinales.forEach(function(campo) {
            campo.hideError();
        });
    };
    
    /** Para cada input del formulario enviado, crea un listener que agrega un evento
     *  onchange y onkeyup a cada input y asi validarlos de forma mas inmediata que 
     *  llamando el metodo de validacion {Validacion.validarFormulario}.
     *  Se espera que este metodo no sea llamado por el usuario final, sino que 
     *  sea llamado por un metodo que obtiene los formularios a mejorar de forma automatica.
     * 
     * @param {type} formSelector formSelector el selector de jquery del formulario mejorar
     * @return {undefined}
     */
    this.onChangeInput = function(/*JquerySelector*/ formSelector) {
        var camposFinales = camposForm(formSelector);
        camposFinales.forEach(function(campo) {
            campo.addChangeListener();
        });
    };
};

/** Por cada formulario que tenga la clase 'needs-validation-manual'
 *  se le agregan los listeners del metodo {Validador.onChangeInput},
 *  haciendo mas interactivo el formulario.
 * 
 * @return {undefined}
 */
$(function() {
    $("form.needs-validation-manual").each(function(selector, valor) {
        Validador.onChangeInput($(valor));
    });
});