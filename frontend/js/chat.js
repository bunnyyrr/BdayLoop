const params = new URLSearchParams(window.location.search);
const subjectId = parseInt(params.get("id"));

const me = requireLogin();

document.getElementById("backLink").href = `user.html?id=${subjectId}`;

async function loadSubjectName() {
    const res = await apiFetch(`/users/${subjectId}`);
    if (res.ok) {
        document.getElementById("subjectName").textContent = (await res.json()).name;
    }
}

async function loadMessages() {
    const res = await apiFetch(`/messages?subjectId=${subjectId}`);
    const el = document.getElementById("messagesList");

    if (!res.ok) {
        el.style.display = "flex";
        el.textContent = await res.text();
        return;
    }

    const messages = await res.json();
    if (messages.length === 0) {
        el.style.display = "flex";
        el.textContent = "Сообщений пока нет...";
        return;
    }

    el.style.display = "block";
    el.innerHTML = messages.map(m => `<p><strong>${m.senderId === me.id ? "Вы" : m.senderId}:</strong> ${m.text}</p>`).join("");
    el.scrollTop = el.scrollHeight;
}

document.getElementById("sendMessageForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const text = document.getElementById("messageText").value;
    const res = await apiFetch("/messages", { method: "POST", body: JSON.stringify({ subjectId, text }) });
    if (res.ok) {
        document.getElementById("messageText").value = "";
        loadMessages();
    } else {
        alert(await res.text());
    }
});

loadSubjectName();
loadMessages();
setInterval(loadMessages, 3000);