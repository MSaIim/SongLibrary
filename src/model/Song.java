package model;

public class Song implements Comparable<Song>
{
	String songTitle, artistName, albumTitle, albumArt;
	int albumYear;
	
	// Constructors
	public Song(String songTitle, String artistName, String albumTitle, int albumYear, String albumArt)
	{
		this.songTitle = songTitle;
		this.artistName = artistName;
		this.albumTitle = albumTitle;
		this.albumYear = albumYear;
		this.albumArt = albumArt;
	}
	
	// Getters
	public String getSongTitle() { return this.songTitle; }
	public String getArtistName() { return this.artistName; }
	public String getAlbumTitle() { return this.albumTitle; }
	public int getAlbumYear() { return this.albumYear; }
	public String getAlbumArt() { return this.albumArt; }
	
	// Setters
	public void setSongTitle(String songTitle) { this.songTitle = songTitle; }
	public void setArtistName(String artistName) { this.artistName = artistName; }
	public void setAlbumTitle(String albumTitle) { this.albumTitle = albumTitle; }
	public void setAlbumYear(int albumYear) { this.albumYear = albumYear; }
	public void setAlbumArt(String albumArt) { this.albumArt = albumArt; }
	
	// toString (used to display song title in list)
	public String toString()
	{
		return this.songTitle;
	}

	@Override
	public int compareTo(Song song) 
	{
		String thisSongTitle = this.songTitle.toLowerCase();
		String otherSongTitle = song.getSongTitle().toLowerCase();
		
		return thisSongTitle.compareTo(otherSongTitle);
	}
}
