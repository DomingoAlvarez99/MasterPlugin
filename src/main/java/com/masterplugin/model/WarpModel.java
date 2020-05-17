package com.masterplugin.model;

public class WarpModel {
	
	private long id, coordinateX, coordinateY, coordinateZ;
	private String name;
	private PlayerModel player;

	public WarpModel() {

	}
	
	public WarpModel(String name) {
		this.name = name;
	}
	
	public WarpModel(long id, long coordinateX, long coordinateY, long coordinateZ, String name, PlayerModel player) {
		this.id = id;
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
		this.coordinateZ = coordinateZ;
		this.name = name;
		this.player = player;
	}
	public WarpModel(long coordinateX, long coordinateY, long coordinateZ, String name, PlayerModel player) {
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
		this.coordinateZ = coordinateZ;
		this.name = name;
		this.player = player;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCoordinateX() {
		return coordinateX;
	}
	public void setCoordinateX(long coordinateX) {
		this.coordinateX = coordinateX;
	}
	public long getCoordinateY() {
		return coordinateY;
	}
	public void setCoordinateY(long coordinateY) {
		this.coordinateY = coordinateY;
	}
	public long getCoordinateZ() {
		return coordinateZ;
	}
	public void setCoordinateZ(long coordinateZ) {
		this.coordinateZ = coordinateZ;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PlayerModel getPlayer() {
		return player;
	}
	public void setPlayer(PlayerModel player) {
		this.player = player;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WarpModel other = (WarpModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Warp [id=" + id + ", coordinateX=" + coordinateX + ", coordinateY=" + coordinateY + ", coordinateZ="
				+ coordinateZ + ", name=" + name + ", player=" + player + "]";
	}
	
}
