document.getElementById("change_password_button")
    .addEventListener("click", () => {
        document.getElementById("password_change").style.display = "flex";
        document.getElementById("change_password_button").style.display = "none";
    })

function redirectExercisesForReviewWithPageSize() {
    window.location.replace("http://localhost:8081/exercises/for-review?pageSize=" + document.getElementById("page_size").value);
}