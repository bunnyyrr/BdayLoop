const params = new URLSearchParams(window.location.search);
const groupId = parseInt(params.get("id"));
const me = requireLogin();

async function loadGroup() {
    const res = await apiFetch(`/groups/${groupId}`);
    if (!res.ok) {
        alert("Группа не найдена");
        return;
    }
    const group = await res.json();
    document.getElementById("groupName").textContent = group.name;
    document.getElementById("renameInput").value = group.name;

    const canManage = group.createdBy === me.id || me.role === "ADMIN";
    document.getElementById("renameBox").hidden = !canManage;
    document.getElementById("deleteGroupBtn").hidden = !canManage;
}

async function loadMembers() {
    const res = await apiFetch(`/groups/${groupId}/members`);
    const memberIds = await res.json();

    const isMember = memberIds.includes(me.id);
    document.getElementById("joinBtn").hidden = isMember;
    document.getElementById("leaveBtn").hidden = !isMember;

    const users = await Promise.all(
        memberIds.map(id => apiFetch(`/users/${id}`).then(r => r.json()))
    );

    const el = document.getElementById("membersList");
    el.innerHTML = users.length
        ? users.map(u => `<li><a href="user.html?id=${u.id}">${u.name}</a></li>`).join("")
        : "<li>Участников пока нет.</li>";
}

async function loadSubscriptionStatus() {
    const res = await apiFetch("/groups?subscribed=true");
    if (!res.ok) return;
    const myGroups = await res.json();
    const isSubscribed = myGroups.some(g => g.id === groupId);

    document.getElementById("groupSubStatus").hidden = !isSubscribed;
    document.getElementById("subscribeGroupBtn").hidden = isSubscribed;
    document.getElementById("unsubscribeGroupBtn").hidden = !isSubscribed;
}

document.getElementById("joinBtn").addEventListener("click", async () => {
    const res = await apiFetch(`/groups/${groupId}/join`, { method: "POST" });
    if (res.ok) loadMembers();
    else alert(await res.text());
});

document.getElementById("leaveBtn").addEventListener("click", async () => {
    const res = await apiFetch(`/groups/${groupId}/join`, { method: "DELETE" });
    if (res.ok) loadMembers();
    else alert(await res.text());
});

document.getElementById("subscribeGroupBtn").addEventListener("click", async () => {
    const res = await apiFetch(`/groups/${groupId}/subscribe`, { method: "POST" });
    if (res.ok) loadSubscriptionStatus();
    else alert(await res.text());
});

document.getElementById("unsubscribeGroupBtn").addEventListener("click", async () => {
    const res = await apiFetch(`/groups/${groupId}/subscribe`, { method: "DELETE" });
    if (res.ok) loadSubscriptionStatus();
    else alert(await res.text());
});

document.getElementById("renameForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = document.getElementById("renameInput").value;
    const res = await apiFetch(`/groups/${groupId}`, { method: "PUT", body: JSON.stringify({ name }) });
    if (res.ok) {
        document.getElementById("groupName").textContent = name;
    } else {
        alert(await res.text());
    }
});

document.getElementById("deleteGroupBtn").addEventListener("click", async () => {
    if (!confirm("Удалить группу без возможности восстановления?")) return;
    const res = await apiFetch(`/groups/${groupId}`, { method: "DELETE" });
    if (res.ok) {
        window.location.href = "dashboard.html";
    } else {
        alert(await res.text());
    }
});

loadGroup();
loadMembers();
loadSubscriptionStatus();