
document.addEventListener('DOMContentLoaded', ()=>{
    const username = window.localStorage.getItem("username");
    if(username==null)
        document.getElementById("links").innerHTML = "<a href=\"./signup.html\">Sign Up</a>\n <a href=\"./login.html\">Login</a>";
    else
        document.getElementById("links").innerHTML = "<a href=\"./profile.html\">Profile</a>";
});