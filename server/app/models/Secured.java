package models;

import play.mvc.Result;
import play.mvc.Http;
import play.mvc.Security;

/**
 * Created by antoniel on 30/10/15.
 */
public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get("username");
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return unauthorized();
    }
}
