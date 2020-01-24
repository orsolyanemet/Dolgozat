package ro.edu.ubb.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Entity for the rooms.
 * 
 * @author Nemet Orsolya
 *
 */
public class Room implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String idRoom;

	private String roomName;

	private String location;

	private List<RoomType> roomTypeList;
	
	public Room() {
		super();
	}

	public Room(String idRoom, String roomName, String location, List<RoomType> roomTypeList) {
		super();
		this.idRoom = idRoom;
		this.roomName = roomName;
		this.location = location;
		this.roomTypeList = roomTypeList;
	}

	public String getIdRoom() {
		return idRoom;
	}

	public void setIdRoom(String idRoom) {
		this.idRoom = idRoom;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<RoomType> getRoomTypeList() {
		return roomTypeList;
	}

	public void setRoomTypeList(List<RoomType> roomTypeList) {
		this.roomTypeList = roomTypeList;
	}

	@Override
	public String toString() {
		return "Room [idRoom=" + idRoom + ", roomName=" + roomName + ", location=" + location + ", roomTypeList="
				+ roomTypeList + "]";
	}

}
