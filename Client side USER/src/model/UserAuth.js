export class UserAuth {

    constructor(username, password, newUsername = "", newPassword = ""){
        this.username = username;
        this.password = password;
        this.newUsername = newUsername;
        this.newPassword = newPassword;
    }
    
}