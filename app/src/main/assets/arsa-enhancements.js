(function () {
  if (window.__arsaEnhancementsLoaded) return;
  window.__arsaEnhancementsLoaded = true;

  var enhanceTimer = null;
  var toastTimer = null;
  var readySignaled = false;

  function getLabel(el) {
    if (!el) return "";
    return (
      el.getAttribute("aria-label") ||
      el.getAttribute("title") ||
      el.textContent ||
      ""
    ).trim();
  }

  function isVisible(el) {
    if (!el || !el.isConnected) return false;
    if (el.closest('[aria-hidden="true"]')) return false;

    var style = window.getComputedStyle(el);
    if (style.display === "none" || style.visibility === "hidden") return false;

    var rect = el.getBoundingClientRect();
    return rect.width > 4 && rect.height > 4;
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
    }, duration || 1800);
  }

  function visibleRadios() {
    return Array.prototype.slice
      .call(document.querySelectorAll('[role="radio"]'))
      .filter(isVisible);
  }

  function groupsFrom(radios) {
    var nativeGroups = [];
    var seen = [];

    radios.forEach(function (radio) {
      var group = radio.closest('[role="radiogroup"]');
      if (group && seen.indexOf(group) === -1) {
        seen.push(group);
        nativeGroups.push(
          radios.filter(function (item) {
            return item.closest('[role="radiogroup"]') === group;
          })
        );
      }
    });

    if (nativeGroups.length) return nativeGroups;
    return radios.length ? [radios] : [];
  }

  function findSubmitButton() {
    var candidates = Array.prototype.slice.call(
      document.querySelectorAll('[aria-label],[title],[role="button"],button')
    );

    for (var i = 0; i < candidates.length; i += 1) {
      if (!isVisible(candidates[i])) continue;

      var label = getLabel(candidates[i]).toLowerCase().replace(/\s+/g, " ");
      if (
        label === "submit" ||
        label === "kirim" ||
        label === "jawab" ||
        label === "periksa" ||
        label === "cek jawaban" ||
        label.indexOf("submit slide") !== -1
      ) {
        return candidates[i];
      }
    }
    return null;
  }

  function enhance() {
    enhanceTimer = null;

    var radios = visibleRadios();
    var groups = groupsFrom(radios);
    var anySelected = false;

    groups.forEach(function (group) {
      var selected = null;

      group.forEach(function (radio) {
        radio.classList.add("arsa-choice");
        if (isSelected(radio)) selected = radio;
      });

      if (selected) anySelected = true;
      group.forEach(function (radio) {
        radio.classList.toggle("arsa-choice-selected", radio === selected);
      });
    });

    var submit = findSubmitButton();
    if (submit) {
      submit.classList.add("arsa-submit");
      submit.classList.toggle("arsa-submit-ready", anySelected);
      submit.setAttribute("data-arsa-ready", anySelected ? "true" : "false");
    }

    if (radios.length && !sessionStorage.getItem("arsaQuizHintShown")) {
      sessionStorage.setItem("arsaQuizHintShown", "1");
      showToast("Pilih satu jawaban. Tombol kirim akan aktif setelah pilihan dipilih.", 2400);
    }
  }

  function scheduleEnhance() {
    if (enhanceTimer) window.clearTimeout(enhanceTimer);
    enhanceTimer = window.setTimeout(enhance, 120);
  }

  function signalReady() {
    if (readySignaled) return;
    readySignaled = true;
    try {
      if (window.MPIARSA && typeof window.MPIARSA.contentReady === "function") {
        window.MPIARSA.contentReady();
      }
    } catch (error) {
      // Native bridge is optional when the Storyline package runs in a browser.
    }
  }

  document.addEventListener(
    "click",
    function (event) {
      var target = event.target;
      if (!target || !target.closest) return;

      var choice = target.closest('[role="radio"]');
      if (!choice || !isVisible(choice)) return;

      window.setTimeout(scheduleEnhance, 40);
    },
    true
  );

  document.addEventListener(
    "pointerdown",
    function (event) {
      var target = event.target;
      if (!target || !target.closest) return;
      var choice = target.closest('[role="radio"]');
      if (choice && isVisible(choice)) choice.classList.add("arsa-choice-pressed");
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
  document.addEventListener("visibilitychange", scheduleEnhance, true);
  window.addEventListener("resize", scheduleEnhance, { passive: true });

  var observerTarget = document.body || document.documentElement;
  var observer = new MutationObserver(scheduleEnhance);
  observer.observe(observerTarget, {
    subtree: true,
    childList: true,
    attributes: true,
    attributeFilter: ["aria-checked", "aria-selected", "aria-hidden"]
  });

  scheduleEnhance();
  window.setTimeout(signalReady, 250);
})();
