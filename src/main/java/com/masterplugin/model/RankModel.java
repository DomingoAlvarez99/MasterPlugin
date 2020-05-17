package com.masterplugin.model;

import java.util.List;

public class RankModel {
    private Long id;
    private String name;
    private List<PermissionModel> permissions;

    public RankModel(){

    }

    public RankModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public RankModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RankModel setName(String name) {
        this.name = name;
        return this;
    }

	public List<PermissionModel> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionModel> permissions) {
		this.permissions = permissions;
	}
  
}
