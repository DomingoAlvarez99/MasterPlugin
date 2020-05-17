package com.masterplugin.model;

public class DeathModel {
	private long id;
	private String date;
	private String cause;
	private PlayerModel assasin, murdered;

	public DeathModel() {

	}

	public DeathModel(String date, String cause, PlayerModel assasin, PlayerModel murdered) {
		this.date = date;
		this.cause = cause;
		this.assasin = assasin;
		this.murdered = murdered;
	}

	public DeathModel(long id, String date, String cause, PlayerModel assasin, PlayerModel murdered) {
		this.id = id;
		this.date = date;
		this.cause = cause;
		this.assasin = assasin;
		this.murdered = murdered;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public PlayerModel getAssasin() {
		return assasin;
	}

	public void setAssasin(PlayerModel assasin) {
		this.assasin = assasin;
	}

	public PlayerModel getMurdered() {
		return murdered;
	}

	public void setMurdered(PlayerModel murdered) {
		this.murdered = murdered;
	}

	@Override
	public String toString() {
		return "Death [id=" + id + ", date=" + date + ", cause=" + cause + ", assasin=" + assasin + ", murdered="
				+ murdered + "]";
	}
}
