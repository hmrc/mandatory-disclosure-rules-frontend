// =====================================================
// UpScan upload
// =====================================================
$("#uploadForm").submit(function(e){
    e.preventDefault();
    const fileLength = $("#file-upload")[0].files.length;
    if(fileLength === 0){
        var errorRequestId = $("#x-amz-meta-request-id").val()
        var errorUrl = $("#upScanErrorRedirectUrl").val() + "?errorCode=InvalidArgument&errorMessage=FileNotSelected&errorRequestId="+errorRequestId
        window.location = errorUrl
    } else {
        function disableFileUpload(){
            $("#file-upload").attr('disabled', 'disabled')
        };

        function addUploadSpinner(){
            $("#processing").append('<p class="govuk-visually-hidden">'+$("#processingMessage").val()+'</p><div><svg class="ccms-loader" height="100" width="100"><circle cx="50" cy="50" r="40"  fill="none"/></svg></div>')
            $(".govuk-form-group--error").removeClass("govuk-form-group--error")
            $("#file-upload-error").remove()
            $("#error-summary").remove()
           };

        $("#submit").remove();
        this.submit();
        disableFileUpload();
        addUploadSpinner();
    }

});

$(document).ready(function ()
{
    var hasError = (window.location.href.indexOf("errorCode") > -1)
    var preFixError = hasError ? "Error: " : ""
    var appendError = preFixError + $("title").html()
    $("title").html(appendError)
});