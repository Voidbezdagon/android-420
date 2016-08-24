package cm.com.teamscheduler.app.utils;

import java.io.IOException;

import cm.com.teamscheduler.app.entity.User;

/**
 * Created by void on 23.08.16.
 */
public class Auth  {

    private static Auth instance = null;

    private Auth()
    {

    }

    public static Auth getInstance()
    {
        if (Auth.instance == null)
        {
            Auth.instance = new Auth();
        }

        return Auth.instance;
    }

    private User loggedUser = null;

    public User getLoggedUser()
    {
        return loggedUser;
    }

    public void setLoggedUser(User user) throws IOException
    {
        this.loggedUser = user;
    }
}
