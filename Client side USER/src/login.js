import {UserAuth} from "./model/UserAuth.js";
import { properties } from "./properties.js";

async function sendLoginRequest(username, password){
    
    const userAuth = new UserAuth(username, password);

    const response = await fetch(properties.server_url + "/auth", {
        method : 'PATCH',
        //mode : "same-origin",
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
        body : JSON.stringify(userAuth)
    });
    const responseStatus = response.status;
    if(responseStatus == 200)
        return {success: true, message : ""};
    
    const responseMessage = await response.text();
    return {success: false, message: responseMessage};
}

document.addEventListener('DOMContentLoaded', ()=>{
    let button_element = document.getElementById("login-button");
    button_element.addEventListener("click", async () =>{

        const username = document.getElementById("uname").value;
        const password = document.getElementById("psw").value;
        let {success, message} = await sendLoginRequest(username, password);
        if(success)
        {
            window.localStorage.setItem("username", username);
            window.location.pathname = "/views/home.html"
        }
        else 
            document.getElementById("notify").innerText = message;
    });
});