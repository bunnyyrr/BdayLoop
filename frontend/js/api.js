const API ="http://localhost:8080";

function getCurrentUser(){
    const raw= localStorage.getItem("currentUser");
    return raw ? JSON.parse(raw) : null;
}

function requireLogin(){
    const user = getCurrentUser();
    if(!user) {
        window.location.href = "login.html";
        return null;
    }
    return user;
}

async function apiFetch(path, options ={}){
    return fetch(`${API}${path}`, {
        credentials: "include",
        headers: {"Content-Type": "application/json"},
        ...options
    });
}

document.addEventListener("DOMContentLoaded", ()=> {
    const logoutBtn =document.getElementById("logoutBtn");
    if(logoutBtn){
        logoutBtn.addEventListener("click", async() => {
            await apiFetch("/users/logout", {method: "POST"});
            localStorage.removeItem("currentUser");
            window.location.href= "login.html";
        });
    }
});

