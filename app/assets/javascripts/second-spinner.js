// =====================================================
// Second spinner wheel to wait for response from EIS
// =====================================================
var checkProgress = false
$("#sendYourFileForm").submit(function(e){
e.preventDefault();
{
     var sendYourFileForm = this;

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
                window.location =  $("#technicalDifficultiesRedirectUrl").val()
            }).done(function(){
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
    var count = 0;
    if (refreshUrl) {
        window.refreshIntervalId = setInterval(function () {
            if (count < 12) { //TODO Need to change it to 3/4 after all the testing
                $.getJSON(refreshUrl)
                .done(function (data, textStatus, jqXhr) {
                    if (jqXhr.status === 200) {
                          window.location = data.url;
                    } else {
                       count += 1
                       return false
                    }
                }).fail(function( jqxhr, textStatus, error ) {
                    window.location =  $("#technicalDifficultiesRedirectUrl").val()
                });
            } else {
                window.location =  $("#slowJourneyUrl").val()
            }
        }, 3000);
    }

}
