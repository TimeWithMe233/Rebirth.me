package dramdev.socket.network.info.record;

import dramdev.socket.enums.ClientType;
import dramdev.socket.enums.Rank;


/**
 * @author DiaoLing
 * @since 4/8/2024
 */
public  class OnlineUserInfo{
    private ClientType client;
    private String username;
    private String inGameName;
    private Rank rank;

    // gouzaohanshu
    public OnlineUserInfo(ClientType client,String username,String inGameName,Rank rank){
        this.client = client;
        this.username = username;
        this.inGameName =inGameName;
        this.rank =rank;
    }

    public ClientType getClient() {
        return client;
    }

    public void setClient(ClientType client) {
        this.client = client;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInGameName() {
        return inGameName;
    }

    public void setInGameName(String inGameName) {
        this.inGameName = inGameName;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
}