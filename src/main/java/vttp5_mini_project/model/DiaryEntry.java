package vttp5_mini_project.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;

public class DiaryEntry {
    
    private String id; 
    private long date; 

    @NotEmpty(message = "Diary entry is mandatory")
    private String diaryText;

    private String recentlyPlayedSong;
    private String recentlyPlayedImage; 

    public DiaryEntry() {

        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.date = Instant.now().toEpochMilli();

    }

    public DiaryEntry(String id, long date, String diaryText,
            String recentlyPlayedSong, String recentlyPlayedImage) {
        this.id = id;
        this.date = date;
        this.diaryText = diaryText;
        this.recentlyPlayedSong = recentlyPlayedSong;
        this.recentlyPlayedImage = recentlyPlayedImage;
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

    public String getRecentlyPlayedImage() {
        return recentlyPlayedImage;
    }

    public void setRecentlyPlayedImage(String recentlyPlayedImage) {
        this.recentlyPlayedImage = recentlyPlayedImage;
    } 

}
