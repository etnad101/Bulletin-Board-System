/*
* ServerCtx.java
*
* Holds server context information for easy sharing between classes
* - config holds immutable information about the server
* - state holds mutable information about the server
*/

public class ServerCtx {
    public final ServerConfig config;
    public final ServerState state;

    public ServerCtx(ServerConfig config) {
        this.config = config;
        this.state = new ServerState();
    }

}
