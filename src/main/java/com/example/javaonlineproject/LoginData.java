package com.example.javaonlineproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Klasa reprezentująca dane logowania użytkownika.
 *
 * <p>Zawiera login i hasło użytkownika oraz umożliwia ich serializację/deserializację
 * za pomocą biblioteki Jackson.</p>
 */
public class LoginData {
    /**
     * Konstruktor tworzący obiekt {@code LoginData}.
     *
     * @param login    login użytkownika.
     * @param password hasło użytkownika.
     */
    @JsonCreator
    public LoginData(@JsonProperty("login") String login, @JsonProperty("password") String password) {
        this.login = login;
        this.password = password;
    }
    /**
     * Login użytkownika.
     */
    @JsonProperty("login")
    private String login;
    /**
     * Hasło użytkownika.
     */
    @JsonProperty("password")
    private String password;
    /**
     * Pobiera login użytkownika.
     *
     * @return login użytkownika.
     */
    public String getLogin() {
        return login;
    }
    /**
     * Ustawia login użytkownika.
     *
     * @param login login użytkownika.
     */
    public void setLogin(String login) {
        this.login = login;
    }
    /**
     * Pobiera hasło użytkownika.
     *
     * @return hasło użytkownika.
     */
    public String getPassword() {
        return password;
    }
    /**
     * Ustawia hasło użytkownika.
     *
     * @param password hasło użytkownika.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}





