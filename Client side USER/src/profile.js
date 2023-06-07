import { properties } from "./properties.js";
import { fetchAfterCheckLoggedIn } from "./middleware/checkLoggedIn.js"
import { UserAuth } from "./model/UserAuth.js";
import checkPasswordReq from "./signup.js";

async function getInfo()
{
    const {response,read} = await fetchAfterCheckLoggedIn(properties.server_url + "/secure/info");
    if(read || response.status!=200)
    {
        document.getElementById("user-info").innerText = "";
        document.getElementById("notify").innerText = "Some error occured. Code: " + response.status;
    }
    else
    {
        const responseJson = await response.json();
        document.getElementById("notify").innerText = "";
        if(responseJson.userInfo==null)
        document.getElementById("user-info").innerText = "No user info has been set";
        else
            document.getElementById("user-info").innerText = responseJson.userInfo.substring(1, responseJson.userInfo.length - 1);
    }
}

async function setInfo(newInfo)
{
    const {response, read} = await fetchAfterCheckLoggedIn(properties.server_url + "/secure/info",{
        method : 'PUT',
        //mode : "same-origin",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
        body : JSON.stringify(newInfo)
    })
    if(response.status!=200)
    {
        document.getElementById("user-info").innerText = "";
        document.getElementById("notify").innerText = "Some error occured. Code: " + response.status;
    }
    else
        getInfo();
}

async function deleteAccount() {
    const {response, read} = await fetchAfterCheckLoggedIn(properties.server_url + "/secure/deleteAccount",{
        method : 'DELETE',
        //mode : "same-origin",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            }
    })
    if(response.status!=200)
    {
        document.getElementById("user-info").innerText = "";
        document.getElementById("notify").innerText = "Some error occured. Code: " + response.status;
    }
    else
    {
        window.localStorage.removeItem("username");
        window.location.pathname = "./views/home.html";
    }
}

async function updateUsername(newUsername) {
    const {response, read} = await fetchAfterCheckLoggedIn(properties.server_url + "/secure/updateAccount/username",{
        method : 'PUT',
        //mode : "same-origin",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
        body: newUsername
    })
    if(response.status!=200)
    {
        document.getElementById("user-info").innerText = "";
        document.getElementById("notify").innerText = "Some error occured. Code: " + response.status;
    }
    else
    {
        window.localStorage.setItem("username", newUsername);
        window.location.pathname = "./views/home.html";
    }
}

async function updatePassword(userAuth) {

    const passwordQualified = await checkPasswordReq(userAuth.newPassword);
    console.log(passwordQualified);
    if(!passwordQualified)
    {
        document.getElementById("notify").innerText = "Password does not meet requirements. Password must be between 6 to 16 characters which contain only characters, numeric digits and underscore."
        return;
    }

    const {response, read} = await fetchAfterCheckLoggedIn(properties.server_url + "/secure/updateAccount/password",{
        method : 'PUT',
        //mode : "same-origin",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
        body: JSON.stringify(userAuth)
    })
    
    if(read || response.status!=200)
    {
        let responseText;
        if(!read)
            responseText = await response.text();
        else
            responseText = response

        document.getElementById("user-info").innerText = "";
        if(read && responseText=="Incorrect Password")
            document.getElementById("notify").innerText = "Password is incorrect";
        else 
            document.getElementById("notify").innerText = "Some error occured. Code: " + (read ? "401" : response.status);
    }
    else
        window.location.pathname = "./views/home.html"
}

async function logoutUser(){
    const {response, read} = await fetchAfterCheckLoggedIn(properties.server_url + "/secure/auth",{
        method : 'PUT',
        //mode : "same-origin",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            }
    })
    if(response.status!=200)
    {
        document.getElementById("user-info").innerText = "";
        document.getElementById("notify").innerText = "Some error occured. Code: " + response.status;
    }
    else
    {
        window.localStorage.removeItem("username");
        window.location.pathname = "./views/home.html";
    }
}

document.addEventListener('DOMContentLoaded', ()=>{

    getInfo();

    document.getElementById("static-text").innerText = "Hi, " + window.localStorage.getItem("username") + ". Displaying user info:";

    document.getElementById("info-button").addEventListener("click", async () =>{
        const newInfo = document.getElementById("info").value;
        setInfo(newInfo);
    });

    document.getElementById("uname-button").addEventListener("click", () => {
        const newUsername = document.getElementById("uname").value;
        updateUsername(newUsername);
    });

    document.getElementById("psw-button").addEventListener("click", () => {
        const password = document.getElementById("psw").value;
        const newPassword = document.getElementById("psw-new").value;
        const userAuth = new UserAuth("", password, "", newPassword)
        updatePassword(userAuth);
    });

    document.getElementById("delete-button").addEventListener("click", () => {
        deleteAccount();
    });

    document.getElementById("logout-button").addEventListener("click", () => {
        logoutUser();
    })
});