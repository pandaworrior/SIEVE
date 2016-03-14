/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package replicationlayer.core.txstore.baseserver;
import replicationlayer.core.txstore.util.Result;
import replicationlayer.core.txstore.util.Operation;

/**
 *
 * @author aclement
 */
public interface ServerApplication {
    
    public Result execute(Operation op);
    
}
