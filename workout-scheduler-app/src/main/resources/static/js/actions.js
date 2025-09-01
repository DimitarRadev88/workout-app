function showPasswordChangeElement() {
    document.getElementById("password_change").style.display = "flex";
    document.getElementById("change_password_button").style.display = "none";
}

function redirectExercisesWithPageSize() {
    window.location.replace(replaceQueryParam(window.location.href, "pageSize", document.getElementById("page_size").value));
}s

document.getElementById("filter_exercise_button").addEventListener("click", () => {
    document.getElementById("filter_element").style.display = null;
});

function replaceQueryParam(url, param, value) {
    const urlObj = new URL(url);
    const params = urlObj.searchParams;
    params.set(param, value);
    return urlObj.toString();
}