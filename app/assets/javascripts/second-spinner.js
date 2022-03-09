// =====================================================
// Second spinner wheel to wait for response from EIS
// =====================================================
var checkProgress = false
$("#sendYourFileForm").submit(function(e){
e.preventDefault();
console.log("====================================================")
{
     var sendYourFileForm = this;

    function submitForError(error, jqXHR){
         window.location =  $("#technicalDifficultiesRedirectUrl").val()
    };

       function addSpinner(){
              $("#information").before(
                "<div id=\"processing\" aria-live=\"polite\" class=\"govuk-!-margin-bottom-5\">" +
                "<div><div class=\"ccms-loader\"></div></div></div>"
                )
       };

    function sendYourFile(form){
        if (checkProgress === false) {
            $.ajax({
                  url: form.action,
                  type: "POST",
                  data: new FormData(form),
                  processData: false,
                  contentType: false,
                  crossDomain: true
            }).error(function(jqXHR, textStatus, errorThrown ){
                submitForError("4000", jqXHR)
            }).done(function(){
                // Disable UI
                 checkProgress = true
                 addSpinner();
                 refreshToCheckStatusPage();
            });
        }
    };
    sendYourFile(sendYourFileForm)
}

});
// =====================================================
//  Refresh status page
// =====================================================
function refreshToCheckStatusPage(){
    var refreshUrl = $("#fileStatusRefreshUrl").val();
    if (refreshUrl) {
        window.refreshIntervalId = setInterval(function () {
            $.getJSON(refreshUrl, function (data, textStatus, jqXhr) {
                if (jqXhr.status === 200) {
                      window.location = data.url;
                } else if (jqXhr.status === 100) {
                   return false
                } else {
                    console.debug("Something went wrong", jqXhr);
                }
            });
        }, 3000);
    }

}
