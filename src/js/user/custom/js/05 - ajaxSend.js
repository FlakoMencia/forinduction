function blockPageAjax(){
    $.blockUI({
        message: '<i class="fas fa-spin fa-sync text-white"></i>',
        //timeout: 2000, //unblock after 2 seconds
        overlayCSS: {
            backgroundColor: '#000',
            opacity: 0.5,
            cursor: 'wait'
        },
        css: {
            border: 0,
            padding: 0,
            backgroundColor: 'transparent'
        }
    });

}
$(function () {
    $(document).ajaxStop($.unblockUI);
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    if (token != "" && header != "") {
    $(document).ajaxSend(function (e, xhr, options) {
        blockPageAjax();
        xhr.setRequestHeader(header, token);
    });
    }
});