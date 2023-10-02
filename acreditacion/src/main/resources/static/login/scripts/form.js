
// On input gaining focus
$(".text-body input").on("focus", function () {
  $(this).addClass("focus");
})

// On input losing focus
$(".text-body input").on("blur", function () {
  if ($(this).val() == "") {
    $(this).removeClass("focus");
  }
})
