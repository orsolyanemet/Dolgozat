package ro.edu.ubb.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity for the requests.
 * 
 * @author Nemet Orsolya
 *
 */
public class Request implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String idRequest;

	private String reservationName;

	private String reservationType;

	private Integer duration;

	private Date fromTime;

	private Date toTime;

	private User user;

	private Room room;

	public Request() {
		super();
	}

	public Request(String idRequest, String reservationName, String reservationType, Integer duration, Date fromTime,
			Date toTime, User user, Room room) {
		super();
		this.idRequest = idRequest;
		this.reservationName = reservationName;
		this.reservationType = reservationType;
		this.duration = duration;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.user = user;
		this.room = room;
	}

	public String getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(String idRequest) {
		this.idRequest = idRequest;
	}

	public String getReservationName() {
		return reservationName;
	}

	public void setReservationName(String reservationName) {
		this.reservationName = reservationName;
	}

	public String getReservationType() {
		return reservationType;
	}

	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	@Override
	public String toString() {
		return "Request [idRequest=" + idRequest + ", reservationName=" + reservationName + ", reservationType="
				+ reservationType + ", duration=" + duration + ", fromTime=" + fromTime + ", toTime=" + toTime
				+ ", user=" + user + ", room=" + room + "]";
	}

}
