package pl.itmation.budget;


class User
{
    String login = null;
    String password = null;

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public User(String login, String password)
    {
        this.login = login;
        this.password = password;
    }
}
