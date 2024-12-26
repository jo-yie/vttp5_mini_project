package vttp5_mini_project.model;

public class SpotifyUser {

    private String spotifyUsername; 
    private String accessToken; 
    private String refreshToken; 
    private Long tokenExpiry; 

    public SpotifyUser() {
    }

    public SpotifyUser(String spotifyUsername, String accessToken, String refreshToken, Long tokenExpiry) {
        this.spotifyUsername = spotifyUsername;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiry = tokenExpiry;
    }

    public String getSpotifyUsername() {
        return spotifyUsername;
    }

    public void setSpotifyUsername(String spotifyUsername) {
        this.spotifyUsername = spotifyUsername;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(Long tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    } 
    
}
