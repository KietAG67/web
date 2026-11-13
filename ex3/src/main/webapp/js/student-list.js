document.addEventListener('DOMContentLoaded', () => {
    const selectAll = document.querySelector('#select-all');
    const checkboxes = [...document.querySelectorAll('.student-checkbox')];

    if (!selectAll) {
        return;
    }

    selectAll.addEventListener('change', () => {
        checkboxes.forEach((checkbox) => {
            checkbox.checked = selectAll.checked;
        });
    });

    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener('change', () => {
            selectAll.checked = checkboxes.length > 0 && checkboxes.every((item) => item.checked);
            selectAll.indeterminate = !selectAll.checked && checkboxes.some((item) => item.checked);
        });
    });
});
