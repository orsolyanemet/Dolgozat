package ro.edu.ubb.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Entity for the room types.
 * 
 * @author Nemet Orsolya
 *
 */
public class RoomType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String idRoomType;

	private String roomTypeName;

	private List<RoomType> equivalentTo;

	public RoomType() {
		super();
	}

	public RoomType(String idRoomType, String roomTypeName, List<RoomType> equivalentTo) {
		super();
		this.idRoomType = idRoomType;
		this.roomTypeName = roomTypeName;
		this.equivalentTo = equivalentTo;
	}

	public String getIdRoomType() {
		return idRoomType;
	}

	public void setIdRoomType(String idRoomType) {
		this.idRoomType = idRoomType;
	}

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}

	public List<RoomType> getEquivalentTo() {
		return equivalentTo;
	}

	public void setEquivalentTo(List<RoomType> equivalentTo) {
		this.equivalentTo = equivalentTo;
	}

	@Override
	public String toString() {
		return "RoomType [idRoomType=" + idRoomType + ", roomTypeName=" + roomTypeName + ", equivalentTo="
				+ equivalentTo + "]";
	}

}
