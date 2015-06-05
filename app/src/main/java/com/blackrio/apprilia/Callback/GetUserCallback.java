package com.blackrio.apprilia.Callback;

import com.blackrio.apprilia.Bean.User;

/**
 * Created by Stefan on 31.05.2015.
 */
public interface GetUserCallback {

    public abstract void done(User returnedUser);
}
