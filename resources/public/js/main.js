(function(interval) {

  var templates = {
    "measurement-status": function(data) {
      var div = $('<div class="measurement-status-container">').addClass(levelClass(data["level"]));
      var measurement = $('<div class="measurement-status-num">').html(data["number"]);
      var description = $('<div class="measurement-status-description">').html(data["description"]);

      return div.append(measurement).append(description);
    },
    "not-available": function() {
      var div = $('<div class="not-available-container">').html("Data not available");
      return div;
    }
  };

  var levelClass = function(level) {
    if (level == 2) {
      return "level-problem";
    } else if (level == 1) {
      return "level-warn";
    } else {
      return "level-good";
    }
  };

  var render = function(data) {
    var template = data["template"] || "not-available";
    var templateFn = templates[template];
    var result = templateFn(data);

    $(".js-slide").empty().append(result);
  };

  var fetch = function() {
    return $.getJSON("/api");
  };

  var changePage = function() {
    fetch()
      .done(function(data){
        render(data);
      });
  };

  changePage();
  setInterval(changePage, interval);
})(10000);
