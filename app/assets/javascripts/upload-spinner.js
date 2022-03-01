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
    var uploadForm = this;
    function submitError(error, jqXHR){
        var errorCode = jqXHR.responseJSON.errorCode
        var errorMessage  = jqXHR.responseJSON.errorCode
        var errorRequestId = jqXHR.responseJSON.errorCode
        var errorUrl = $("#upScanErrorRedirectUrl").val() + "?errorCode="+errorCode+"&errorMessage="+errorMessage+"&errorRequestId="+errorRequestId
        window.location = errorUrl
    };

    function fileUpload(form){
        $.ajax({
              url: form.action,
              type: "POST",
              data: new FormData(form),
              processData: false,
              contentType: false,
              crossDomain: true
        }).error(function(jqXHR, textStatus, errorThrown ){
            submitError("4000", jqXHR)
        }).done(function(){
            // Disable UI
            $("#file-upload").before(
            "<div id=\"processing\" aria-live=\"polite\" class=\"govuk-!-margin-bottom-5\">" +
            "<h2 class=\"govuk-heading-m\">"+ $("#processingMessage").val() +
            "</h2><div><div class=\"ccms-loader\"></div></div></div>"
            )
            $("#file-upload").attr('disabled', 'disabled')
             $("#submit").addClass('govuk-button--disabled')
             refreshPage();
        });
    };

    fileUpload(uploadForm);
}

});
// =====================================================
//  Refresh status page
// =====================================================
function refreshPage(){
    var refreshUrl = $("#fileUploadRefreshUrl").val();
    if (refreshUrl) {
        window.refreshIntervalId = setInterval(function () {
            console.log("scheduling ajax call, refreshUrl", refreshUrl)

            $.getJSON(refreshUrl, function (data, textStatus, jqXhr) {
                if (jqXhr.status === 200) {
                     console.log("status changed, updating page", data);
                      window.location = data.url;
                } else if (jqXhr.status === 100) {
                 console.log("status didn't change, we not updating anything continue", jqXhr.status);
                } else {
                    console.log("Something went wrong", jqXhr);
                }
            });
        }, 3000);
        console.log("intervalRefreshScheduled, id: ", window.refreshIntervalId);
    }

}
