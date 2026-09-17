document.getElementById("loginForm").addEventListener("submit", async(e) =>{
    e.preventDefault();
    const username = document.getElementById("loginUsername").value;
    const password = document.getElementById("loginPassword").value;

    const res =await apiFetch("/users/login", {
        method: "POST",
        body: JSON.stringify({username, password})
    });

    if(res.ok){
        const user= await res.json();
        localStorage.setItem("currentUser", JSON.stringify(user));
        window.location.href= "dashboard.html";
    }
    else{
        showMessage("Ошибка входа: " + await res.text(), true);
    }
});

document.getElementById("registerForm").addEventListener("submit", async(e) =>{
    e.preventDefault();
    const username = document.getElementById("regUsername").value;
    const password = document.getElementById("regPassword").value;
    const body= {
        name: document.getElementById("regName").value,
        birthday: document.getElementById("regBirthday").value,
        username,
        password
    };

    const res =await apiFetch("/users", {
        method: "POST",
        body: JSON.stringify(body)
    });

    if(res.ok){
        const loginRes = await apiFetch("/users/login", {
            method: "POST",
            body: JSON.stringify({username, password})
        });
        if(loginRes.ok){
            const user = await loginRes.json();
            localStorage.setItem("currentUser", JSON.stringify(user));
            window.location.href= "dashboard.html";
        }
        else{
            showMessage("Регистрация прошла, но автоматический вход не удался. Попробуйте войти вручную", true);
        }
    }
    else{
        showMessage("Ошибка входа: " + await res.text(), true);
    }
});

function showMessage(text, isError =false){
    const el =document.getElementById("message");
    el.textContent= text;
    el.style.color = isError? "red" : "green";
}