package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Song;
import model.SongList;

public class SongLibController 
{
	// FXML variables
	@FXML private ListView<Song> listView;
	@FXML private ImageView albumArtImageView, messageImageView, addEditImageView;
	@FXML private TextField songTitleTextField, artistNameTextField, albumNameTextField, albumYearTextField;
	
	@FXML private Label songTitleLabel, artistNameLabel, albumTitleYearLabel, 
						cofirmDeleteLabel, messageLabel, addEditLabel;
	
	@FXML private Button addButton, editButton, submitButton, cancelButton, 
						 deleteButton, confirmDeleteButton, cancelDeleteButton;
	
	// Helper variables
	private SongList songList;
	private Alert errorAlert;
	private String errorList;
	private boolean isAdding, isEditing;
	
	public void start(Stage mainStage)
	{		
		// Initialize everything
		this.songList = new SongList("Music.xml");
		this.errorAlert = new Alert(AlertType.ERROR);
		this.errorList = "";
		this.isAdding = false;
		this.isEditing = false;
		this.albumArtImageView.setImage(new Image(getClass().getResourceAsStream("/album.png")));
		this.messageImageView.setImage(new Image(getClass().getResourceAsStream("/checkmark.png")));
		
		// Build the song list and populate the list view
		this.songList.build();
		this.listView.setItems(songList.getSongList());
		
		// Show add if nothing in list
		if(this.songList.getSongList().size() == 0)
			this.addSong();

		// Add listener event for list and select first item
		this.listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> changeSongDetail());
		this.listView.getSelectionModel().select(0);
		
		// Setup button events
		this.addButton.setOnAction((event) -> this.addSong());
		this.editButton.setOnAction((event) -> this.editSong());
		this.deleteButton.setOnAction((event) -> this.deleteSong());
		this.submitButton.setOnAction((event) -> this.submitAddEdit());
		this.cancelButton.setOnAction((event) -> this.cancelAddEdit());	
		this.confirmDeleteButton.setOnAction((event) -> this.confirmDelete());
		this.cancelDeleteButton.setOnAction((event) -> this.cancelDelete());
	}
	
	/*\ //========================================
	|*|											
	|*|		BUTTON EVENTS
	|*|
	\*/ //========================================
	private void addSong() 
	{
		// Deselect everything
		this.addEditLabel.requestFocus();
		
		// Show text fields and submit/cancel buttons
		this.setVisibility(true, false, true, false);
		
		// Hide message label and image
		this.setMessageVisibility(false);
		
		// Show add edit label/image
		this.addEditLabel.setText("Add");
		this.addEditLabel.setVisible(true);
		this.addEditImageView.setImage(new Image(getClass().getResourceAsStream("/add.png")));
		this.addEditImageView.setVisible(true);
		
		// Set bool
		this.isAdding = true;
	}
	
	private void editSong() 
	{
		if(songList.getSongList().size() != 0)
		{
			// Show text fields and submit/cancel buttons
			this.setVisibility(true, false, true, false);
			
			// Hide message label and image
			this.setMessageVisibility(false);
			
			// Show add edit label
			this.addEditLabel.setText("Edit");
			this.addEditLabel.setVisible(true);
			this.addEditImageView.setImage(new Image(getClass().getResourceAsStream("/edit.png")));
			this.addEditImageView.setVisible(true);
			
			// Set bool
			this.isEditing = true;
			
			// Fill text fields with selected song
			this.fillTextFields();
			
			// Select the correct item in list to prevent array out of bounds
			int albumYear = -1;
			String songTitle = this.songTitleTextField.getText();
			String artistName = this.artistNameTextField.getText();
			String albumName = this.albumNameTextField.getText();
						
			if(!this.albumYearTextField.getText().equals(""))
				albumYear = Integer.parseInt(this.albumYearTextField.getText());
			
			// Create new temporary song and select it
			Song temp = new Song(songTitle, artistName, albumName, albumYear, "#");
			this.listView.getSelectionModel().select(this.songList.getSongIndex(temp));
		}
	}
	
	private void submitAddEdit()
	{
		// Only do when submit is clicked and no errors
		if(!this.checkForErrors())
		{
			// Get selected index
			int index = listView.getSelectionModel().getSelectedIndex();
			
			// Get text field text
			int albumYear = -1;
			String songTitle = this.songTitleTextField.getText();
			String artistName = this.artistNameTextField.getText();
			String albumName = this.albumNameTextField.getText();
			
			if(!this.albumYearTextField.getText().equals(""))
				albumYear = Integer.parseInt(this.albumYearTextField.getText());
					
			// Create new song
			Song song = new Song(songTitle, artistName, albumName, albumYear, "#");
			
			// Add the song
			if(this.isAdding)
			{
				if(!this.songList.add(song))
					this.showCustomAlert("Song not added. It already exists!");
				else
				{
					// Show successful message
					this.messageLabel.setText("Song added successfully!");
					this.messageImageView.setVisible(true);
					this.messageLabel.setVisible(true);
					
					// Stop adding
					this.isAdding = false;
				}
				
				// Select first song once list stops being empty
				if(songList.getSongList().size() == 1)
				{
					// Select first item in list
					this.listView.getSelectionModel().select(0);
				}
			}
			// Edit the song
			else if(this.isEditing)
			{		
				// Update the song
				if(!this.songList.edit(song, index))
					this.showCustomAlert("Song not edited. It already exists!");
				else
				{
					// Show successful message
					this.messageLabel.setText("Song edited successfully!");
					this.messageImageView.setVisible(true);
					this.messageLabel.setVisible(true);
					
					// Stop editing
					this.isEditing = false;
				}
			}
			
			if(!this.isAdding && !this.isEditing)
			{
				// Clear the text fields
				this.clearTextFields();
						
				// Show the Add/Edit/Delete buttons
				this.setVisibility(false, true, false, false);
				
				// Select the added/edited song
				this.listView.getSelectionModel().select(this.songList.getSongIndex(song));
			}
		}
	}
	
	private void cancelAddEdit()
	{
		// Show Add/Edit/Delete buttons
		this.setVisibility(false, true, false, false);
		
		// Set bools
		this.isAdding = false;
		this.isEditing = false;
		
		// Clear the text fields
		this.clearTextFields();
	}
	
	private void deleteSong() 
	{
		if(songList.getSongList().size() != 0)
		{
			// Set delete confirmation
			this.setVisibility(false, false, false, true);
			
			// Hide message label and image
			this.setMessageVisibility(false);
		}
	}
	
	private void confirmDelete() 
	{
		// Get new index first
		int index = this.listView.getSelectionModel().getSelectedIndex();
		
		// Delete the song
		this.songList.delete(index);
		
		// Set visibility of text fields and buttons
		this.setVisibility(false, true, false, false);
		
		// Show successful message
		this.messageLabel.setText("Song deleted successfully!");
		this.messageImageView.setVisible(true);
		this.messageLabel.setVisible(true);
		
		// Select correct song in list
		this.listView.getSelectionModel().select(index);
		
		// Show add if nothing in list
		if(this.songList.getSongList().size() == 0)
			this.addSong();
	}
	
	private void cancelDelete() 
	{
		// Set visibility of text fields and buttons
		this.setVisibility(false, true, false, false);
	}
	
	/*\ //========================================
	|*|											
	|*|		LISTENER METHODS
	|*|
	\*/ //========================================
	private void changeSongDetail()
	{ 
		// Delete confirmation visibility
		this.confirmDeleteButton.setVisible(false);
		this.cancelDeleteButton.setVisible(false);
		this.cofirmDeleteLabel.setVisible(false);
		
		// Keep submit and cancel buttons visible if adding/editing
		if(this.isAdding || this.isEditing)
		{
			// Show Submit/Cancel buttons
			this.submitButton.setVisible(true);
			this.cancelButton.setVisible(true);
			
			// Fill the text fields with selected song
			if(this.isEditing)
				this.fillTextFields();
		}
		else
		{
			// Show Add/Edit/Delete buttons
			this.addButton.setVisible(true);
			this.editButton.setVisible(true);
			this.deleteButton.setVisible(true);
		}

		// Get selected index
		int index = listView.getSelectionModel().getSelectedIndex();
		
		if(songList.getSongList().size() != 0)
		{
			// Change song details
			this.songTitleLabel.setText(this.songList.get(index).getSongTitle());
			this.artistNameLabel.setText("by " + this.songList.get(index).getArtistName());
			this.albumTitleYearLabel.setText("");
			
			// Optional album name and album year
			if(!this.songList.get(index).getAlbumTitle().equals(""))
				this.albumTitleYearLabel.setText(this.songList.get(index).getAlbumTitle());
			
			if(!this.songList.get(index).getAlbumTitle().equals("") && !(this.songList.get(index).getAlbumYear() == -1))
				this.albumTitleYearLabel.setText(this.songList.get(index).getAlbumTitle() + ", " + this.songList.get(index).getAlbumYear());
			else if(!(this.songList.get(index).getAlbumYear() == -1))
				this.albumTitleYearLabel.setText(this.songList.get(index).getAlbumYear() + "");
		}
		else
		{
			// Change song details
			this.songTitleLabel.setText("");
			this.artistNameLabel.setText("");
			this.albumTitleYearLabel.setText("Please add a song.");
		}
	}
	
	/*\ //========================================
	|*|											
	|*|		HELPER METHODS
	|*|
	\*/ //========================================
	private boolean checkForErrors()
	{
		if(this.songTitleTextField.getText().equals(""))
			this.errorList += "Song Title (Cannot be empty)\n";
		if(this.artistNameTextField.getText().equals(""))
			this.errorList += "Artist Name (Cannot be empty)\n";
		if(!this.isNumeric(this.albumYearTextField.getText()))
			this.errorList += "Album Year (Must be a number)\n";
		
		if(!this.errorList.equals(""))
		{
			// Append to errorList
			this.errorList = "Please fix the following errors:\n\n" + errorList;
			
			// Setup the alert pop up
			this.errorAlert.setTitle("Error Adding/Editing");
			this.errorAlert.setHeaderText(null);
			this.errorAlert.setContentText(errorList);
			
			// Show the alert
			this.errorAlert.showAndWait();
			
			// Reset errors string
			this.errorList = "";
			
			return true;
		}
		
		return false;
	}
	
	private void showCustomAlert(String error)
	{
		// Setup the alert pop up
		error = "Please fix the following errors:\n\n" + error;
		
		this.errorAlert.setTitle("Error Adding/Editing");
		this.errorAlert.setHeaderText(null);
		this.errorAlert.setContentText(error);
					
		// Show the alert
		this.errorAlert.showAndWait();
	}
	
	private void setMessageVisibility(boolean value)
	{
		this.messageImageView.setVisible(value);
		this.messageLabel.setVisible(value);
	}
	
	private void setVisibility(boolean textFieldBool, boolean addEditDelBool, boolean submitBool, boolean deleteBool)
	{
		// Text fields visibility
		this.songTitleTextField.setVisible(textFieldBool);
		this.artistNameTextField.setVisible(textFieldBool);
		this.albumNameTextField.setVisible(textFieldBool);
		this.albumYearTextField.setVisible(textFieldBool);
		
		// Add/Edit/Delete visibility
		this.addButton.setVisible(addEditDelBool);
		this.editButton.setVisible(addEditDelBool);
		this.deleteButton.setVisible(addEditDelBool);
		
		// Submit/Cancel visibility
		this.submitButton.setVisible(submitBool);
		this.cancelButton.setVisible(submitBool);
		
		// Delete confirmation visibility
		this.confirmDeleteButton.setVisible(deleteBool);
		this.cancelDeleteButton.setVisible(deleteBool);
		this.cofirmDeleteLabel.setVisible(deleteBool);
		
		// Hide add edit label/image
		this.addEditLabel.setVisible(false);
		this.addEditImageView.setVisible(false);
	}
	
	private void fillTextFields()
	{
		// Fill text fields with selected song
		int index = listView.getSelectionModel().getSelectedIndex();
		this.songTitleTextField.setText(this.songList.get(index).getSongTitle());
		this.artistNameTextField.setText(this.songList.get(index).getArtistName());
		this.albumNameTextField.setText(this.songList.get(index).getAlbumTitle());
		
		// Make sure year is not empty
		if(this.songList.get(index).getAlbumYear() != -1)
			this.albumYearTextField.setText(Integer.toString(this.songList.get(index).getAlbumYear()));
		else
			this.albumYearTextField.setText("");
	}
	
	private void clearTextFields()
	{
		this.songTitleTextField.clear();
		this.artistNameTextField.clear();
		this.albumNameTextField.clear();
		this.albumYearTextField.clear();
	}
	
	private boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c))
	        	return false;
	    }
	    
	    return true;
	}
}
