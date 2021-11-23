// =====================================================
// Ready function
// =====================================================

function ready(fn) {

    if (document.readyState !== 'loading') {

        fn();
    } else if (document.addEventListener) {

        document.addEventListener('DOMContentLoaded', fn);
    } else {

        document.attachEvent('onreadystatechange', function() {

            if (document.readyState !== 'loading') fn();
        });
    }
}

ready(function() {

    const fileUploadRefreshUrl     = document.getElementById("fileUploadRefreshUrl");

    if (fileUploadRefreshUrl) {

        window.refreshIntervalId = setInterval(function () {

            window.location.assign(fileUploadRefreshUrl.value);
        }, 3000);
    }

});
