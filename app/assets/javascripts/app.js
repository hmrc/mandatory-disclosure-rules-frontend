// prevent resubmit warning
if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
  window.history.replaceState(null, null, window.location.href);
}

// handle back click

if (document.querySelector('.govuk-back-link')) {
    document.querySelector('.govuk-back-link').addEventListener('click', function(e){
      e.preventDefault();
      e.stopPropagation();
      window.history.back();
    });
}

var printLink = document.querySelector('.mdr-print-link');
  if (printLink !== null) {
    var html = printLink.innerHTML;
    printLink.innerHTML = '<a class="govuk-link" href="#">' + html + '</a>';

    printLink.addEventListener('click', function(e){
      e.preventDefault();
      e.stopPropagation();
      window.print();
    });
  }
