// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package dk.clanie.bitcoin.client.response;

import dk.clanie.bitcoin.TransactionOutputRef;
import dk.clanie.bitcoin.client.response.ListUnspentResult;
import java.math.BigDecimal;

privileged aspect ListUnspentResult_Roo_JavaBean {
    
    public TransactionOutputRef ListUnspentResult.getTxRef() {
        return this.txRef;
    }
    
    public String ListUnspentResult.getScriptPubKey() {
        return this.scriptPubKey;
    }
    
    public BigDecimal ListUnspentResult.getAmount() {
        return this.amount;
    }
    
    public Integer ListUnspentResult.getConfirmations() {
        return this.confirmations;
    }
    
}
