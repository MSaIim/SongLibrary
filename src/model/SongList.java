package model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SongList 
{
	// For XML reading/writing
	private DocumentBuilderFactory documentFactory;
	private DocumentBuilder documentBuilder;
	private Document document;
	private File xmlFile;
	
	// List to hold songs from XML file and populate listView
	private ObservableList<Song> songList;
	
	public SongList(String filename)
	{
		try {
			this.documentFactory = DocumentBuilderFactory.newInstance();
			this.documentBuilder = documentFactory.newDocumentBuilder();
			this.xmlFile = new File(filename);
			
			// Create file if not exist
			if(!this.xmlFile.exists())
			{
				xmlFile.createNewFile();
				
				PrintWriter writer = new PrintWriter(xmlFile);
				writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
				writer.write("<music>\n");
				writer.write("</music>");
				writer.close();
			}
			
			this.songList = FXCollections.observableArrayList();
			
		} catch (ParserConfigurationException | IOException e) {
				e.printStackTrace();
		}
	}
	
	public ObservableList<Song> getSongList()
	{
		return this.songList;
	}
	
	public Song get(int index)
	{
		if(index >= 0 && index < this.songList.size())
			return this.songList.get(index);
		
		return this.songList.get(0);
	}
	
	public void build()
	{
		try {
			// Parse the xml file and normalize
			this.document = documentBuilder.parse(xmlFile);
			this.document.getDocumentElement().normalize();
			
			// Get all song nodes
			NodeList musicList = this.document.getElementsByTagName("song");
			
			for(int i = 0; i < musicList.getLength(); i++)
			{
				// Get the current song node element
				Element song = (Element) musicList.item(i);
				
				// Get song attributes
				String songTitle = song.getAttribute("title");
				String artistName = song.getAttribute("artist");
				String albumTitle = song.getAttribute("album");
				int albumYear = Integer.parseInt(song.getAttribute("year"));
				String albumArt = song.getAttribute("art");
				
				// Add to song list
				this.songList.add(new Song(songTitle, artistName, albumTitle, albumYear, albumArt));
			}
			
		} catch (SAXException | IOException e) {
				e.printStackTrace();
		}
		
		// Sort the list
		FXCollections.sort(this.songList);
	}
	
	public boolean add(Song song)
	{
		// Add song to list and xml
		try {
			// Parse the xml file and normalize
			this.document = documentBuilder.parse(xmlFile);
			this.document.getDocumentElement().normalize();
			
			// Check for duplicate
			if(this.checkForDuplicate(song))
				return false;
			
			// Create song child
			Element newSong = (Element) this.document.createElement("song");
			newSong.setAttribute("title", song.getSongTitle());
			newSong.setAttribute("artist", song.getArtistName());
			newSong.setAttribute("album", song.getAlbumTitle());
			newSong.setAttribute("year", Integer.toString(song.getAlbumYear()));
			newSong.setAttribute("art", song.getAlbumArt());
			
			// Add to parent node
			this.document.getElementsByTagName("music").item(0).appendChild(newSong);
			
			// Add to list
			this.songList.add(song);
			
			// Generate new xml file
			this.updateXML();
		
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		
		// Sort the list
		FXCollections.sort(this.songList);
		
		// Song added successfully
		return true;
	}
	
	public boolean edit(Song song, int index)
	{
		// Make sure index is within range
		if(index >= this.songList.size())
			return false;
		
		try {
			// Parse the xml file and normalize
			this.document = documentBuilder.parse(xmlFile);
			this.document.getDocumentElement().normalize();
			
			// Find the node
			Element songElement = (Element) findNode(index);
			
			// Update the fields if changing the capitalization or adding album title or year
			if(song.getSongTitle().toLowerCase().equals(this.songList.get(index).getSongTitle().toLowerCase()) && 
			   song.getArtistName().toLowerCase().equals(this.songList.get(index).getArtistName().toLowerCase()))
			{
				// Update the values
				this.songList.get(index).setSongTitle(song.getSongTitle());
				this.songList.get(index).setArtistName(song.getArtistName());
				this.songList.get(index).setAlbumTitle(song.getAlbumTitle());
				this.songList.get(index).setAlbumYear(song.getAlbumYear());
			}
			else
			{
				// Check for duplicate
				if(this.checkForDuplicate(song))
					return false;
			}
			
			// Edit the node
			songElement.setAttribute("title", song.getSongTitle());
			songElement.setAttribute("album", song.getAlbumTitle());
			songElement.setAttribute("artist", song.getArtistName());
			songElement.setAttribute("year", Integer.toString(song.getAlbumYear()));
			songElement.setAttribute("art", song.getAlbumArt());

			// Update the XML
			this.updateXML();
			
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		
		// Change the values in the list
		this.songList.set(index, song);
		
		// Sort the list
		FXCollections.sort(this.songList);
		
		// Song updated
		return true;
	}
	
	public boolean delete(int index)
	{
		// Make sure index is within range
		if(index >= this.songList.size())
			return false;
		
		try {
			// Parse the xml file and normalize
			this.document = documentBuilder.parse(xmlFile);
			this.document.getDocumentElement().normalize();
			
			// Find the node and remove
			Node songNode = this.findNode(index);
			songNode.getParentNode().removeChild(songNode);
			
			// Remove from song list
			this.songList.remove(index);
			
			// Update the XML
			this.updateXML();
			
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		
		// Node deleted
		return true;
	}
	
	/*\ // ======================================================================================
	|*|
	|*|		XML HELPER METHODS
	|*|
	\*/ // ======================================================================================	
	private void updateXML()
	{
		try
		{
			// Remove whitespace outside of tags 			
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document, XPathConstants.NODESET); 
			
			// Remove #text nodes
			for(int i = 0; i < nodeList.getLength(); i++) 
			{
				Node node = nodeList.item(i); 
				node.getParentNode().removeChild(node);
			}
			
			// Format output
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// Save to file
			StreamResult result = new StreamResult(this.xmlFile);
			DOMSource source = new DOMSource(this.document);
			transformer.transform(source, result);
					
		} catch (TransformerFactoryConfigurationError | TransformerException | XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	private Node findNode(int index)
	{
		try {
			// Setup XPath
			XPath xpath = XPathFactory.newInstance().newXPath();
						
			// Select all the node with the same attributes
			String artist = "//song[@artist=\"" + this.songList.get(index).getArtistName() + "\" and ";
			String album = "@album=\"" + this.songList.get(index).getAlbumTitle() + "\" and ";
			String title = "@title=\"" + this.songList.get(index).getSongTitle() + "\" and ";
			String year = "@year=\"" + this.songList.get(index).getAlbumYear() + "\"]";
			//String art = "@art=\"" + this.songList.get(index).getAlbumArt() + "\"]";
			XPathExpression expr = xpath.compile(artist + album + title + year);
						
			// Compile the XPath expression and select the element that needs to be updated
			NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			
			// Node found
			return nodeList.item(0);
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		// Nothing found
		return null;
	}
	
	private boolean checkForDuplicate(Song song)
	{
		// Check if song already exists in XML
		NodeList musicList = this.document.getElementsByTagName("song");
		for(int i = 0; i < musicList.getLength(); i++)
		{
			// Get song node
			Element songNode = (Element) musicList.item(i);
						
			// Trim all the white spaces
			String newSongTitle = song.getSongTitle().toLowerCase().replaceAll("\\s", "");
			String newSongArtistName = song.getArtistName().toLowerCase().replaceAll("\\s", "");
			String songListSongTitle = songNode.getAttribute("title").toLowerCase().replaceAll("\\s", "");
			String songListArtistName = songNode.getAttribute("artist").toLowerCase().replaceAll("\\s", "");
				
			// Check if artist, album, and song title are the same
			if(newSongArtistName.equals(songListArtistName) && newSongTitle.equals(songListSongTitle))
			{
				return true;
			}
		}
		
		// Duplicate not found
		return false;
	}
	
	public int getSongIndex(Song song)
	{
		for(int i = 0; i < songList.size(); i++)
		{
			if(song.getSongTitle().equals(this.songList.get(i).getSongTitle()) && 
			   song.getArtistName().equals(this.songList.get(i).getArtistName()) &&
			   song.getAlbumTitle().equals(this.songList.get(i).getAlbumTitle()) &&
			   song.getAlbumYear() == this.songList.get(i).getAlbumYear())
			{
				return i;
			}
		}
		
		return 0;
	}
}
