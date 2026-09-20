const me = requireLogin();
document.getElementById("myProfileLink").href = `user.html?id=${me.id}`;

if (me.role === "ADMIN") {
    document.getElementById("adminLink").hidden = false;
}

async function loadGroups(nameFilter = "") {
    const query = nameFilter ? `?name=${encodeURIComponent(nameFilter)}` : "";
    const res = await apiFetch(`/groups${query}`);
    renderGroups(await res.json());
}

function renderGroups(groups) {
    const el = document.getElementById("groupsList");
    el.innerHTML = groups.length ? groups.map(g => `
        <article>
            <strong>${g.name}</strong>
            <button data-id="${g.id}" class="join-btn">Вступить</button>
            <button data-id="${g.id}" class="subscribe-group-btn secondary">Подписаться</button>
        </article>
    `).join("") : "Групп пока нет.";
}

document.getElementById("groupsList").addEventListener("click", async (e) => {
    const id = e.target.dataset.id;
    if (!id) return;

    if (e.target.classList.contains("join-btn")) {
        const res = await apiFetch(`/groups/${id}/join`, { method: "POST" });
        alert(res.ok ? "Вы вступили в группу" : await res.text());
    } else if (e.target.classList.contains("subscribe-group-btn")) {
        const res = await apiFetch(`/groups/${id}/subscribe`, { method: "POST" });
        alert(res.ok ? "Вы подписались на группу" : await res.text());
    }
});

document.getElementById("createGroupForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = document.getElementById("groupName").value;
    const res = await apiFetch("/groups", { method: "POST", body: JSON.stringify({ name }) });
    if (res.ok) {
        document.getElementById("groupName").value = "";
        loadGroups();
    } else {
        alert(await res.text());
    }
});

document.getElementById("searchInputGroup").addEventListener("input", (e) => loadGroups(e.target.value));

async function loadUsers(nameFilter = "") {
    const query = nameFilter ? `?name=${encodeURIComponent(nameFilter)}` : "";
    const res = await apiFetch(`/users${query}`);
    renderUsers(await res.json());
}

function renderUsers(users) {
    const el = document.getElementById("usersList");
    el.innerHTML = users.length ? users.map(u => `
        <article>
            <strong>${u.name}</strong> (@${u.username})
            <a role="button" href="user.html?id=${u.id}">Профиль</a>
        </article>
    `).join("") : "Пользователей не найдено.";
}

document.getElementById("searchInputUser").addEventListener("input", (e) => loadUsers(e.target.value));

loadGroups();
loadUsers();