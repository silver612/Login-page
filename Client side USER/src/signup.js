import {UserAuth} from "./model/UserAuth.js";
import { properties } from "./properties.js";

export default async function checkPasswordReq(password){
    let pattern = /^\w{6,15}$/;
    return password.match(pattern)!=null;
}

async function createUser(username, password){
    
    let password_qualified = await checkPasswordReq(password);
    if(!password_qualified)
        return {success: false, message: "Password does not meet criteria"}

    const userAuth = new UserAuth(username, password);

    const response = await fetch(properties.server_url + "/signup", {
        method : 'POST',
        //mode : "same-origin",
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
        body : JSON.stringify(userAuth)
    });
    
    const responseStatus = response.status;
    if(responseStatus != 200)
        return {success: false, message : "Error status code: " + responseStatus};
    
    const responseMessage = await response.text();
    if(responseMessage=="ALREADY_EXISTS")
        return {success: false, message: "Username already exists"};
    else
        return {success: true, message: ""};
}

document.addEventListener('DOMContentLoaded', ()=>{
    let button_element = document.getElementById("signup-button");
    button_element.addEventListener("click", async () =>{

        const username = document.getElementById("uname").value;
        const password = document.getElementById("psw").value;
        let {success, message} = await createUser(username, password);
        if(success)
        {
            window.localStorage.setItem("username", username);
            window.location.pathname = "/views/home.html";
        }
        else 
            document.getElementById("notify").innerText = message;
    });
});