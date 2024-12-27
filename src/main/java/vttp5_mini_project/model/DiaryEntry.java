package vttp5_mini_project.model;

import java.time.Instant;
import java.util.UUID;

public class DiaryEntry {
    
    private String id; 
    private long date; 
    private String diaryText;
    private String recentlyPlayedSong;
    private String recentlyPlayedImage[]; 

    public DiaryEntry() {

        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.date = Instant.now().toEpochMilli();

    }

    public DiaryEntry(String id, long date, String diaryText, String recentlyPlayedSong) {
        this.id = id;
        this.date = date;
        this.diaryText = diaryText;
        this.recentlyPlayedSong = recentlyPlayedSong;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDiaryText() {
        return diaryText;
    }

    public void setDiaryText(String diaryText) {
        this.diaryText = diaryText;
    }

    public String getRecentlyPlayedSong() {
        return recentlyPlayedSong;
    }

    public void setRecentlyPlayedSong(String recentlyPlayedSong) {
        this.recentlyPlayedSong = recentlyPlayedSong;
    } 

}
