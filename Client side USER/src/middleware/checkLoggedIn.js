
export async function fetchAfterCheckLoggedIn(resource, options){

    let response;

    if(options==undefined)
        response = await fetch(resource,{
            method: 'GET',
            credentials: 'include'
        });
    else
    {
        options.credentials = 'include'
        response = await fetch(resource, options);
    }
    const responseStatus = response.status;
    
    if(responseStatus==401) 
    {
        const responseText = await response.text();
        if(responseText=="Session Id Mismatch")
            window.location.pathname = "/views/login.html";
        else
            return {response: responseText, read:true};
    }
    else
        return {response, read:false};
}