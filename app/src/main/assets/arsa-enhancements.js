(function () {
  if (window.__arsaEnhancementsLoaded) return;
  window.__arsaEnhancementsLoaded = true;

  var pending = false;
  var lastQuestionKey = "";
  var toastTimer = null;

  function getLabel(el) {
    if (!el) return "";
    return (
      el.getAttribute("aria-label") ||
      el.getAttribute("title") ||
      el.textContent ||
      ""
    ).trim();
  }

  function isSelected(el) {
    return el.getAttribute("aria-checked") === "true" ||
      el.getAttribute("aria-selected") === "true";
  }

  function showToast(message, duration) {
    var toast = document.getElementById("arsa-choice-hint");
    if (!toast) {
      toast = document.createElement("div");
      toast.id = "arsa-choice-hint";
      toast.setAttribute("role", "status");
      toast.setAttribute("aria-live", "polite");
      document.body.appendChild(toast);
    }

    toast.textContent = message;
    toast.classList.add("arsa-choice-hint-visible");

    if (toastTimer) window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(function () {
      toast.classList.remove("arsa-choice-hint-visible");
    }, duration || 2200);
  }

  function findSubmitButton() {
    var candidates = document.querySelectorAll(
      '[aria-label],[title],[role="button"],button'
    );

    for (var i = 0; i < candidates.length; i += 1) {
      var label = getLabel(candidates[i]).toLowerCase().replace(/\s+/g, " ");
      if (
        label === "submit" ||
        label === "kirim" ||
        label === "lanjut" ||
        label.indexOf("submit slide") !== -1
      ) {
        return candidates[i];
      }
    }
    return null;
  }

  function enhance() {
    pending = false;

    var radios = Array.prototype.slice.call(
      document.querySelectorAll('[role="radio"]')
    );

    if (radios.length < 2) return;

    var selected = null;
    var keyParts = [];

    radios.forEach(function (radio, index) {
      radio.classList.add("arsa-choice");
      if (isSelected(radio)) selected = radio;
      if (index < 2) keyParts.push(getLabel(radio));
    });

    radios.forEach(function (radio) {
      radio.classList.toggle("arsa-choice-selected", radio === selected);
    });

    var submit = findSubmitButton();
    if (submit) {
      submit.classList.add("arsa-submit");
      submit.classList.toggle("arsa-submit-ready", !!selected);
    }

    var questionKey = keyParts.join("|");
    if (questionKey && questionKey !== lastQuestionKey) {
      lastQuestionKey = questionKey;
      showToast("Pilih satu jawaban, lalu tekan SUBMIT.", 2600);
    }
  }

  function scheduleEnhance() {
    if (pending) return;
    pending = true;
    window.requestAnimationFrame(enhance);
  }

  document.addEventListener(
    "click",
    function (event) {
      var target = event.target;
      if (!target || !target.closest) return;

      var choice = target.closest('[role="radio"]');
      if (!choice) return;

      window.setTimeout(function () {
        scheduleEnhance();
        showToast("Jawaban dipilih ✓  •  tekan SUBMIT untuk lanjut.", 1700);
      }, 60);
    },
    true
  );

  document.addEventListener(
    "pointerdown",
    function (event) {
      var target = event.target;
      if (!target || !target.closest) return;
      var choice = target.closest('[role="radio"]');
      if (choice) choice.classList.add("arsa-choice-pressed");
    },
    true
  );

  function clearPressed(event) {
    var target = event.target;
    if (!target || !target.closest) return;
    var choice = target.closest('[role="radio"]');
    if (choice) choice.classList.remove("arsa-choice-pressed");
  }

  document.addEventListener("pointerup", clearPressed, true);
  document.addEventListener("pointercancel", clearPressed, true);

  var observer = new MutationObserver(scheduleEnhance);
  observer.observe(document.documentElement, {
    subtree: true,
    childList: true,
    attributes: true,
    attributeFilter: ["aria-checked", "aria-selected"]
  });

  scheduleEnhance();
})();
