package com.androidexample.databaseapp.shoppingonline;

/**
 * Created by Belal on 9/9/2017.
 */

public class Api {

//    private static final String ROOT_URL = "http://192.168.43.18:8081/HeroApi2/v1/Api.php?apicall=";
    private static final String ROOT_URL = "http://3dcopilot.com/HeroApi2/v1/Api.php?apicall=";

    public static final String URL_CREATE_ACCOUNT = ROOT_URL + "createaccount";
    public static final String URL_CECK_USERNAME = ROOT_URL + "checkusername";
    public static final String URL_Login_ACCOUNT = ROOT_URL + "checklogin";

    public static final String URL_READ_HEROES = ROOT_URL + "getheroes";
    public static final String URL_UPDATE_HERO = ROOT_URL + "updatehero";
    public static final String URL_DELETE_HERO = ROOT_URL + "deletehero&id=";

}
