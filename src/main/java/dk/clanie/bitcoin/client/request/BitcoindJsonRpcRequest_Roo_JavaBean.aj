// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package dk.clanie.bitcoin.client.request;

import dk.clanie.bitcoin.client.request.BitcoindJsonRpcRequest;
import java.util.List;

privileged aspect BitcoindJsonRpcRequest_Roo_JavaBean {
    
    public String BitcoindJsonRpcRequest.getJsonrpc() {
        return this.jsonrpc;
    }
    
    public String BitcoindJsonRpcRequest.getMethod() {
        return this.method;
    }
    
    public List<?> BitcoindJsonRpcRequest.getParams() {
        return this.params;
    }
    
}
