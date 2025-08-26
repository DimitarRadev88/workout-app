document.getElementById("change_password_button")
    .addEventListener("click", () => {
        document.getElementById("change_password_row").style.display = "flex";
        document.getElementById("change_password_button").style.display = "none";
        document.getElementById("is_changing_password").value = "true";
    })