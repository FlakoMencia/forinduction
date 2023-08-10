function renderSelect2(selector, urlsegment, rows) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Buscar y seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            minimumInputLength: 0,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: rows
                    };
                },
                cache: true
            }
        });
}
function renderSelect2NoSearch(selector, urlsegment) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            minimumResultsForSearch: Infinity,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });
}
function renderSelect2Multiple(selector, urlsegment) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            multiple:true,
            minimumInputLength: -1,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });

    $(selector).on('select2:opening select2:closing', function( event ) {
        var $searchfield = $(this).parent().find('.select2-search__field');
        $searchfield.prop('disabled', true);
    });
}
function renderSelect2MultipleDepend(selector, urlsegment, depend) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            multiple:true,
            minimumInputLength: -1,
            ajax: {
                url: function (params) {
                    let id = $(depend).val();
                    if (id === null) {
                        id = 0;
                    }
                    return urlsegment + id;
                },
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });

    $(selector).on('select2:opening select2:closing', function( event ) {
        var $searchfield = $(this).parent().find('.select2-search__field');
        $searchfield.prop('disabled', true);
    });
}
function renderSelect2Depend(selector, urlsegment, depend) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Buscar y seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            minimumInputLength: 0,
            ajax: {
                url: function (params) {
                    let id = $(depend).val();
                    if (id === null) {
                        id = 0;
                    }
                    return urlsegment + id;
                },
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });
}
function renderJQgridSelect2(selector, urlsegment) {
    $(selector)
        .select2({
            placeholder: 'Buscar y filtrar',
            language: "es",
            width: '100%',
            allowClear: true,
            minimumInputLength: 0,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });
}

function renderSelect2DependData(selector, urlsegment, /*function*/ informacionBase) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Buscar y seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            minimumInputLength: 0,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    let informacion = informacionBase();
                    informacion.q = params.term;
                    informacion.page = params.page || 1;
                    informacion.rows = 10;
                    
                    return informacion;
                },
                cache: true
            }
        });
}