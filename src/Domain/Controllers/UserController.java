package Domain.Controllers;

import DataAccess.UserDao;
import Domain.Elements.User;
import Domain.Status;

import java.util.HashMap;
import java.util.List;

public class UserController {
    UserDao userDao;

    private User createUser(HashMap<String,String> userData){
        String Id = userData.get("UserId");
        String Password = userData.get("Password");
        String DateOfBirth = userData.get("DateOfBirth");
        String Name = userData.get("Name");
        return new User(Id, Password, DateOfBirth, Name);
    }

    public UserController() {
        userDao = UserDao.getInstance();
    }

    public Status insertUser(String userId, String password, String dateOfBirth, String name){
        List<HashMap<String, String>> usersInSys = userDao.getAll();
        for (HashMap<String, String> userData: usersInSys) {
            String Id = userData.get("Id");
            if(Id.equals(userId))
                return Status.UserIdTaken;
        }
        HashMap<String, String> toInsert = new HashMap<String, String>() {{
            put("Id", userId);
            put("Password", password);
            put("DateOfBirth", dateOfBirth);
            put("Name", name);
        }};
        boolean res = userDao.save(toInsert);
        if(!res) return Status.SomethingWentWrong;
        return Status.Success;
    }
    public Status logIn(String userId, String password){
        List<HashMap<String, String>> usersInSys = userDao.getAll();
        User rightUser = null;
        // get all sys users and check if userId exists
        for (HashMap<String, String> userData: usersInSys) {
            String Id = userData.get("UserId");
            if(Id.equals(userId)) {
                String pass = userData.get("Password");
                if (!pass.equals(password)) return Status.BadPassword;
                // check user not already logged in
                for(User userLoggedIn : User.loggedIn) if(userLoggedIn.getUserId().equals(userId))
                    return Status.AlreadyLoggedIn;
                // password correct and user not logged in yet
                rightUser = this.createUser(userData);
                break;
            }
        }
        // haven't found userId
        if(rightUser == null) return Status.BadUserName;
        // complete log in
        User.loggedIn.add(rightUser);
        return Status.Success;
    }
}
