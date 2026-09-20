const params = new URLSearchParams(window.location.search);
const profileId = parseInt(params.get("id"));

const me = requireLogin();
const isOwnProfile = me && me.id === profileId;

async function loadProfile() {
    const res = await apiFetch(`/users/${profileId}`);
    if (!res.ok) {
        alert("Пользователь не найден");
        return;
    }
    const user = await res.json();

    document.getElementById("userName").textContent = user.name;
    document.getElementById("userUsername").textContent = user.username;
    document.getElementById("userId").textContent = user.id;
    document.getElementById("userBirthday").textContent = user.birthday;

    if (isOwnProfile) {
        document.getElementById("ownerActions").hidden = false;
        document.getElementById("editName").value = user.name;
        document.getElementById("editBirthday").value = user.birthday;
        document.getElementById("editUsername").value = user.username;
    } else {
        document.getElementById("otherActions").hidden = false;
        document.getElementById("chatLink").href = `chat.html?id=${profileId}`;
    }
}

async function loadUserGroups() {
    const res = await apiFetch(`/groups?memberId=${profileId}`);
    const groups = await res.json();
    document.getElementById("userGroups").textContent = groups.length
        ? groups.map(g => g.name).join(", ")
        : "нет групп";
}

async function loadGifts() {
    const res = await apiFetch(`/gifts?userId=${profileId}`);
    const gifts = await res.json();
    const el = document.getElementById("giftsList");
    el.innerHTML = gifts.length ? gifts.map(g => `
        <li>${g.title} ${isOwnProfile ? `<button data-id="${g.id}" class="delete-gift-btn secondary">Удалить</button>` : ""}</li>
    `).join("") : "<li>Список подарков пуст.</li>";
}

document.getElementById("editForm")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const body = {
        name: document.getElementById("editName").value,
        birthday: document.getElementById("editBirthday").value,
        username: document.getElementById("editUsername").value
    };
    const res = await apiFetch(`/users/${profileId}`, { method: "PUT", body: JSON.stringify(body) });
    if (res.ok) {
        localStorage.setItem("currentUser", JSON.stringify(await res.json()));
        alert("Профиль обновлён");
    } else {
        alert(await res.text());
    }
});

document.getElementById("addGiftForm")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const title = document.getElementById("giftTitle").value;
    const res = await apiFetch("/gifts", { method: "POST", body: JSON.stringify({ title }) });
    if (res.ok) {
        document.getElementById("giftTitle").value = "";
        loadGifts();
    } else {
        alert(await res.text());
    }
});

document.getElementById("giftsList").addEventListener("click", async (e) => {
    if (!e.target.classList.contains("delete-gift-btn")) return;
    const res = await apiFetch(`/gifts/${e.target.dataset.id}`, { method: "DELETE" });
    if (res.ok) loadGifts();
    else alert(await res.text());
});

document.getElementById("subscribeBtn")?.addEventListener("click", async () => {
    const res = await apiFetch(`/users/${profileId}/subscribe`, { method: "POST" });
    alert(res.ok ? "Вы подписались" : await res.text());
});

document.getElementById("unsubscribeBtn")?.addEventListener("click", async () => {
    const res = await apiFetch(`/users/${profileId}/subscribe`, { method: "DELETE" });
    alert(res.ok ? "Вы отписались" : await res.text());
});

loadProfile();
loadGifts();
loadUserGroups();