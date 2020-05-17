package com.masterplugin.model;

public class PermissionModel {
    private Long id;
    private String name;

    public static final String CHANGE_NAME_ALL_COLOR_SELF = "change.name_all_color.self";
    public static final String CHANGE_NAME_SINGLE_COLOR_SELF = "change.name_single_color.self";
    public static final String CHANGE_NAME_ALL_COLOR_EVERYONE = "change.name_all_color.everyone";
    public static final String CHANGE_NAME_SINGLE_COLOR_EVERYONE = "change.name_single_color.everyone";
    public static final String CHANGE_PREFIX_ALL_COLOR_SELF = "change.prefix_all_color.self";
    public static final String CHANGE_PREFIX_SINGLE_COLOR_SELF = "change.prefix_single_color.self";
    public static final String CHANGE_PREFIX_ALL_COLOR_EVERYONE = "change.prefix_all_color.everyone";
    public static final String CHANGE_PREFIX_SINGLE_COLOR_EVERYONE = "change.prefix_single_color.everyone";
    public static final String CHANGE_RANK_EVERYONE = "change.rank.everyone";
    public static final String LIST_COLORS = "list.colors";
    public static final String LIST_COMMANDS = "list.commands";
    public static final String ADD_RANK = "add.rank";
    public static final String ADD_RANK_PERMISSION = "add.rank_permission";
    public static final String ADD_CHAT_PLAYER = "add.chat_player";
    public static final String DELETE_RANK = "delete.rank";
    public static final String DELETE_RANK_PERMISSION = "delete.rank_permission";
    public static final String DELETE_CHAT_PLAYER = "delete.chat_player";
    public static final String BAL_SELF = "bal.self";
    public static final String BAL_EVERYONE = "bal.everyone";
    public static final String BAL_TOP = "bal.top";
    public static final String HELPOP = "helpop";
    public static final String SEEN_SELF = "seen.self";
    public static final String SEEN_EVERYONE = "seen.everyone";
    public static final String PING_SELF = "ping.self";
    public static final String PING_EVERYONE = "ping.everyone";
    public static final String STATS_SELF = "stats.self";
    public static final String STATS_EVERYONE = "stats.everyone";
    public static final String HAT_SELF = "hat.self";
    public static final String HAT_EVERYONE = "hat.everyone";
    public static final String CREATE_CHAT = "create.chat";
    public static final String GIVE_MONEY = "give.money";

    public Long getId() {
        return id;
    }

    public PermissionModel() {

    }

    public PermissionModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public PermissionModel(String name) {
        this.name = name;
    }

    public PermissionModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PermissionModel setName(String name) {
        this.name = name;
        return this;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		PermissionModel other = (PermissionModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Permission [id=" + id + ", name=" + name + "]";
	}
    
}
