package com.masterplugin.model;

import com.masterplugin.util.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerModel {

	private long id, timePlayed;
	private String name, uuid, prefix, prefixColor, nameColor, nameFormat, prefixFormat, ip;
	private String firstLogin, lastLogin;
	private boolean init = true;
	private List<String> oldColor, oldName;
	private RankModel rank;
	private List<WarpModel> warps;
	private boolean online;
	private String[] colors = new String[] { Color.LIGHT_RED, Color.DARK_YELLOW, Color.LIGHT_YELLOW, Color.LIGHT_GREEN,
			Color.DARK_TURQUOISE, Color.LIGHT_TURQUOISE, Color.PURPLE, Color.MAGENTA };

	public PlayerModel() {

	}

	public PlayerModel(long id) {
		this.id = id;
	}

	public PlayerModel(long id, long timePlayed, RankModel rank, String name, String uuid, String prefix,
			String prefixColor, String nameColor, String nameFormat, String prefixFormat, String ip, String firstLogin,
			String lastLogin, boolean online) {
		this.id = id;
		this.timePlayed = timePlayed;
		this.rank = rank;
		this.name = name;
		this.uuid = uuid;
		this.prefix = prefix;
		this.prefixColor = prefixColor;
		this.nameColor = nameColor;
		this.nameFormat = nameFormat;
		this.prefixFormat = prefixFormat;
		this.ip = ip;
		this.firstLogin = firstLogin;
		this.lastLogin = lastLogin;
		this.online = online;
	}

	public PlayerModel(long timePlayed, RankModel rank, String name, String uuid, String prefix, String prefixColor,
			String nameColor, String nameFormat, String prefixFormat, String ip, String firstLogin, String lastLogin,
			boolean online) {
		this.timePlayed = timePlayed;
		this.rank = rank;
		this.name = name;
		this.uuid = uuid;
		this.prefix = prefix;
		this.prefixColor = prefixColor;
		this.nameColor = nameColor;
		this.nameFormat = nameFormat;
		this.prefixFormat = prefixFormat;
		this.ip = ip;
		this.firstLogin = firstLogin;
		this.lastLogin = lastLogin;
		this.online = online;
	}

	public long getId() {
		return id;
	}

	public RankModel getRank() {
		return rank;
	}

	public PlayerModel setRank(RankModel rank) {
		this.rank = rank;
		return this;
	}

	public PlayerModel setId(long id) {
		this.id = id;
		return this;
	}

	public long getTimePlayed() {
		return timePlayed;
	}

	public void setTimePlayed(long timePlayed) {
		this.timePlayed = timePlayed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefixColor() {
		return prefixColor;
	}

	public void setPrefixColor(String prefixColor) {
		this.prefixColor = prefixColor;
	}

	public String getNameColor() {
		return nameColor;
	}

	public void setNameColor(String nameColor) {
		this.nameColor = nameColor;
	}

	public String getNameFormat() {
		return nameFormat;
	}

	public void setNameFormat(String nameFormat) {
		this.nameFormat = nameFormat;
	}

	public String getPrefixFormat() {
		return prefixFormat;
	}

	public void setPrefixFormat(String prefixFormat) {
		this.prefixFormat = prefixFormat;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(String firstLogin) {
		this.firstLogin = firstLogin;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public List<String> getOldColor() {
		return oldColor;
	}

	public void setOldColor(List<String> oldColor) {
		this.oldColor = oldColor;
	}

	public List<String> getOldName() {
		return oldName;
	}

	public void setOldName(List<String> oldName) {
		this.oldName = oldName;
	}

	public String[] getColors() {
		return colors;
	}

	public void setColors(String[] colors) {
		this.colors = colors;
	}

	public List<WarpModel> getWarps() {
		return warps;
	}

	public void setWarps(List<WarpModel> warps) {
		this.warps = warps;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public String toString() {
		return "PlayerModel [id=" + id + ", timePlayed=" + timePlayed + ", name=" + name + ", uuid=" + uuid
				+ ", prefix=" + prefix + ", prefixColor=" + prefixColor + ", nameColor=" + nameColor + ", nameFormat="
				+ nameFormat + ", prefixFormat=" + prefixFormat + ", ip=" + ip + ", firstLogin=" + firstLogin
				+ ", lastLogin=" + lastLogin + ", init=" + init + ", oldColor=" + oldColor + ", oldName=" + oldName
				+ ", rank=" + rank + ", colors=" + Arrays.toString(colors) + "]";
	}

	public void writeDefaultColor(String name) {
		oldName = new ArrayList<>();
		oldColor = new ArrayList<>();
		int caracteres = name.length() - colors.length;
		int cont = 0;
		for (int i = 0; i < name.length(); i++) {
			if (i < colors.length) {
				oldColor.add(colors[i]);
				oldName.add(String.valueOf(name.charAt(i)));
			}
			if (caracteres > 0 && i >= colors.length) {
				if (cont == colors.length) {
					cont = 0;
				}
				if (cont < colors.length) {
					oldColor.add(colors[cont]);
					oldName.add(String.valueOf(name.charAt(i)));
					cont++;
					caracteres--;
				}
			}
		}
	}

	public String writeColor(String name) {
		if (init) {
			init = false;
			writeDefaultColor(name);
		}
		String nombreARGB = "";
		List<String> colorNuevo = new ArrayList<>();
		for (int i = 0; i < oldColor.size(); i++) {
			if (oldColor.get(i).equalsIgnoreCase(colors[0])) {
				colorNuevo.add(colors[7]);
			}
			if (oldColor.get(i).equalsIgnoreCase(colors[1])) {
				colorNuevo.add(colors[0]);
			}
			if (oldColor.get(i).equalsIgnoreCase(colors[2])) {
				colorNuevo.add(colors[1]);
			}
			if (oldColor.get(i).equalsIgnoreCase(colors[3])) {
				colorNuevo.add(colors[2]);
			}
			if (oldColor.get(i).equalsIgnoreCase(colors[4])) {
				colorNuevo.add(colors[3]);
			}
			if (oldColor.get(i).equalsIgnoreCase(colors[5])) {
				colorNuevo.add(colors[4]);
			}
			if (oldColor.get(i).equalsIgnoreCase(colors[6])) {
				colorNuevo.add(colors[5]);
			}
			if (oldColor.get(i).equalsIgnoreCase(colors[7])) {
				colorNuevo.add(colors[6]);
			}
		}
		for (int i = 0; i < oldName.size(); i++) {
			nombreARGB += colorNuevo.get(i) + oldName.get(i);
		}
		nombreARGB += "&f";
		oldColor = colorNuevo;
		return nombreARGB;
	}

}
