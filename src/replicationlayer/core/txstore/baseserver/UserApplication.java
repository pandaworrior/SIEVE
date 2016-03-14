/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package replicationlayer.core.txstore.baseserver;
import replicationlayer.core.txstore.util.Result;

/**
 *
 * @author aclement
 */
public interface UserApplication {
    public void processResult(Result res);
    
}
