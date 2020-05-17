package com.masterplugin.model;

public class AchievementModel {
	private long id;
	private String name, date;
	private PlayerModel player;
	
	public AchievementModel() {
		
	}
	
	public AchievementModel(long id, String name, String date, PlayerModel player) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.player = player;
	}

	public AchievementModel(String name, String date, PlayerModel player) {
		this.name = name;
		this.date = date;
		this.player = player;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public PlayerModel getPlayer() {
		return player;
	}

	public void setPlayer(PlayerModel player) {
		this.player = player;
	}
	
}
