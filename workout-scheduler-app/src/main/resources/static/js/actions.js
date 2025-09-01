function showPasswordChangeElement() {
    document.getElementById("password_change").style.display = "flex";
    document.getElementById("change_password_button").style.display = "none";
}

function redirectExercisesWithPageSize() {
    window.location.replace(replaceQueryParam(window.location.href, "pageSize", document.getElementById("page_size").value));
}s

function showFilters() {
    document.getElementById("filter_element").style.display = null;
    document.getElementById("filter_show_exercise_button").style.display = "none";
    document.getElementById("filter_hide_exercise_button").style.display = null;
}

function hideFilters() {
    document.getElementById("filter_element").style.display = "none";
    document.getElementById("filter_show_exercise_button").style.display = null;
    document.getElementById("filter_hide_exercise_button").style.display = "none";
}

function replaceQueryParam(url, param, value) {
    const urlObj = new URL(url);
    const params = urlObj.searchParams;
    params.set(param, value);
    return urlObj.toString();
}