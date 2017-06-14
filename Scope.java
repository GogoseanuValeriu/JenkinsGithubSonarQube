package ro.bcr.authserver.Model;

/**
 * Created by Andrei-Daniel Ene (andreidaniel.ene@bcr.ro) on 5/26/2017.
 */
public class Scope {
    private String clientID;
    private String language;
    private String scope;
    private String scope_text;

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope_text(String language) {
        if(this.language.equals(language)) return scope_text;
        return "";
    }

    public void setScope_text(String scope_text) {
        this.scope_text = scope_text;
    }

    @Override
    public String toString() {
        return getClientID() + "["+getLanguage()+";"+getScope()+";"+getScope_text("en")+"]";
    }
}
