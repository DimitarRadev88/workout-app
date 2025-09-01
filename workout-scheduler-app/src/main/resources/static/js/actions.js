function showPasswordChangeElement() {
    document.getElementById("password_change").style.display = "flex";
    document.getElementById("change_password_button").style.display = "none";
}

function redirectExercisesWithPageSize() {
    window.location.replace(replaceQueryParam(window.location.href, "pageSize", document.getElementById("page_size").value));
}s

function showFilters() {
    setFiltersVisibility(null, "none", null)
}

function hideFilters() {
    setFiltersVisibility("none", null, "none")
}

function setFiltersVisibility(element, showButton, hideButton) {
    document.getElementById("filter_element").style.display = element;
    document.getElementById("filter_show_exercise_button").style.display = showButton;
    document.getElementById("filter_hide_exercise_button").style.display = hideButton;
}

function replaceQueryParam(url, param, value) {
    const urlObj = new URL(url);
    const params = urlObj.searchParams;
    params.set(param, value);
    return urlObj.toString();
}

