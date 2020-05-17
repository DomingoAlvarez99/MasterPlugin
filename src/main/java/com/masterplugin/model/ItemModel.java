package com.masterplugin.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.masterplugin.client.HttpClient;
import com.masterplugin.client.Observer;

public class ItemModel {
    private long id, uuid, durability;
    private String name, type;

    public ItemModel() {

    }

    public ItemModel(long id, long uuid, long durability, String name, String type) {
        this.id = id;
        this.uuid = uuid;
        this.durability = durability;
        this.name = name;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public ItemModel setId(long id) {
        this.id = id;
        return this;
    }

    public long getUuid() {
        return uuid;
    }

    public ItemModel setUuid(long uuid) {
        this.uuid = uuid;
        return this;
    }

    public long getDurability() {
        return durability;
    }

    public ItemModel setDurability(long durability) {
        this.durability = durability;
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public ItemModel setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", durability=" + durability +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }


    public static void addItems() {
        try {
            URL url = new URL("https://minecraft-ids.grahamedgecombe.com/");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            ItemModel item = new ItemModel();
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("<td class=\"id\">")) {
                    String idPieces = inputLine.split("<td class=\"id\">")[1];
                    String ids = "";
                    if (idPieces.split("</td>")[0].contains(":")) {
                        ids = idPieces.split("</td>")[0];
                    } else {
                        ids = idPieces.split("</td>")[0] + ":0";
                    }
                    long uuid = Long.parseLong(ids.split(":")[0]);
                    long durability = Long.parseLong(ids.split(":")[1]);
                    item.setDurability(durability)
                            .setUuid(uuid);
//                    System.out.println(uuid);
                }
                if (inputLine.contains("<td class=\"row-desc\"><span class=\"name\">")) {
                    String namePieces = inputLine.split("<td class=\"row-desc\"><span class=\"name\">")[1];
                    String name = "";
                    name = namePieces.split("</span><br /><span class=\"text-id\">")[0];
                    if (name.contains("Mob Head (")) {
                        name = name.split("Mob Head")[1].replace("(", "").replace(")", "").trim() + " Head";
                    }else{

                    }
                    String type = "";
                    type = namePieces.split("</span><br /><span class=\"text-id\">")[1]
                            .split("</span></td>")[0]
                            .substring(1, namePieces.split("</span><br /><span class=\"text-id\">")[1]
                                    .split("</span></td>")[0].length() - 1).split(":")[1];
                    item.setName(name)
                            .setType(type);
//                    System.out.println(item);
                    initAddItemResponse(item);
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initAddItemResponse(ItemModel item) {
        HttpClient.post("item", item, new Observer<String>() {
            @Override
            public void onSuccess(String data) {
            	 System.out.println(data);
            }

            @Override
            public void onFailure(Throwable throwable) {
           	 System.out.println(throwable.getMessage());
            }
        });
    }
}
