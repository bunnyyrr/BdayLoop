requireLogin();

document.getElementById("importForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const raw = document.getElementById("importJson").value;

    let users;
    try {
        users = JSON.parse(raw);
    } catch (err) {
        document.getElementById("importResult").textContent = "Некорректный JSON: " + err.message;
        return;
    }

    const res = await apiFetch("/users/import", {
        method: "POST",
        body: JSON.stringify(users)
    });

    const el = document.getElementById("importResult");
    if (res.ok) {
        const created = await res.json();
        el.innerHTML = `Импортировано пользователей: ${created.length}<ul>${created.map(u => `<li>${u.name} (@${u.username})</li>`).join("")}</ul>`;
        document.getElementById("importForm").reset();
    } else {
        el.textContent = "Ошибка импорта: " + await res.text();
    }
});