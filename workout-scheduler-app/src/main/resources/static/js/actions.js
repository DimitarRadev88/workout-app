function showPasswordChangeElement() {
    document.getElementById("password_change").style.display = "flex";
    document.getElementById("change_password_button").style.display = "none";
}

function redirectExercisesWithPageSize() {
    const pageSize = document.getElementById("page_size").value;
    window.location.replace(replaceQueryParam(window.location.href, "pageSize", pageSize));
}

function redirectPageWithNameAndPageSize(name) {
    const pageSize = document.getElementById("page_size").value;
    const newUrl = replaceQueryParam(window.location.href, "name", name);
    window.location.replace(replaceQueryParam(newUrl, "pageSize", pageSize));
}

function handleExerciseClick(element) {
    const name = element.getAttribute("data-name");
    redirectPageWithNameAndPageSize(name);
}

function filterExercisesByName() {
    const input = document.getElementById("exercise-search");
    const filter = input.value.toLowerCase();
    const dropdown = document.getElementById("exercise-list");
    const items = dropdown.querySelectorAll("li");

    let hasMatch = false;
    items.forEach(item => {
        const text = item.textContent.toLowerCase();
        if (text.includes(filter)) {
            item.style.display = "";
            hasMatch = true;
        } else {
            item.style.display = "none";
        }
    });

    dropdown.style.display = hasMatch ? "block" : "none";
}

function showDropdown() {
    document.getElementById("exercise-list").style.display = "block";
}

function hideDropdown() {
    setTimeout(() => {
        document.getElementById("exercise-list").style.display = "none";
    }, 200);
}

function handleKeyUp(event) {
    filterExercisesByName();
    if (event.key === "Enter") {
        const input = document.getElementById("exercise-search").value;
        redirectPageWithNameAndPageSize(input);
    }
}

function replaceQueryParam(url, param, value) {
    const urlObj = new URL(url);
    const params = urlObj.searchParams;
    params.set(param, value);
    return urlObj.toString();
}

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

function redirectAddWorkoutWithFilterForExercises() {
    const leftTarget = document.querySelectorAll("#target_1:checked");
    leftTarget.forEach(target => {console.log(target.value);});
    const rightTarget = document.querySelectorAll("#target_2:checked");
    rightTarget.forEach(target => {console.log(target.value);});
    const values = [];

    leftTarget.forEach(target => {values.push(target.value);});
    rightTarget.forEach(target => {values.push(target.value);});

    window.location.replace(replaceQueryParam(window.location.href, "selectedBodyParts", values));
}

